<script setup>
    import {ref, computed,onBeforeMount} from 'vue'
    import {useRoute, useRouter} from 'vue-router'
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    import { useSignIn } from '../state/signIn.js';
    import modalDeleteEvent from '../components/modalDeleteEvent.vue'
    import modalEditEvent from '../components/modalEditEvent.vue'

    // import Icons
    import IconSchedule from '../components/icons/iconSchedule.vue'
    import IconMore from '../components/icons/iconMore.vue'
    import IconEdit from '../components/icons/iconEdit.vue'
    import IconDelete from '../components/icons/iconDelete.vue'
    import IconLoading from '../components/icons/iconLoading.vue'

    const datetimeFormat = useDatetimeFormat()
    const {params} = useRoute()
    const signIn = useSignIn()

// --- Go back --- 
    const myRouter = useRouter()
    const goToViewEvent= () => myRouter.push({ name: 'ViewEvent'})
    const goToError401 = () => myRouter.push({ name: 'Error401'})
    const goToError403 = () => myRouter.push({ name: 'Error403'})
    const goToError404 = () => myRouter.push({ name: 'Error404'})
    const goToError500 = () => myRouter.push({ name: 'Error500'})
    
// --- Get all list --- 
    const thisEvent = ref({})
    const showDetail = ref({})
    const loading =ref()
    const message = ref()

    const getThisEvent = async () => {
        loading.value = true
        message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
    ).catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
    });
    console.log(res.status)
        if(res.status===200){
            thisEvent.value = await res.json()
            console.log(`GET This Event id: ${params.eventId} OK`)
            loading.value=false
            showDetail.value = true
        }else if(res.status===404){
            console.log(`Not Found! This Event id: ${params.eventId}`)
            showDetail.value = false
            message.value = `Not Found! This Event id: ${params.eventId}`
        }else if(res.status===401){
            let errorText = await res.text()
            console.log(errorText)
            if(errorText==="Token is expired."){
                await signIn.sendRefreshToken()
            }else{
                message.value = "Please login"
                goToError401()
            }
        }else if(res.status===403){
            console.log('Unauthorized access')
            goToError403()
        }
        loading.value = false
    }

    onBeforeMount(async () => {
      await getThisEvent()
      edit()
    })

// --- GET List Overlap ---    
const listOverlap = ref([])
const selectedCategory = ref('')
const selectedDate = ref('')
    
const getListOverlap = async (editingEvent) => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/list-edit-overlap/${editingEvent.id}/${editingEvent.date}`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
    ).catch((error)=> console.log(error));
    console.log(res.status)
    if(res.status==200){
        listOverlap.value = await res.json()
        console.log(`-- Get List Overlap Times --`)
        console.log(listOverlap.value)
    }else if(res.status===404){
        console.log(`Not Found This Event id: ${editingEvent.eventId}`)
    }else if(res.status===401){
        console.log('Please login')
    }else if(res.status===403){
        console.log('Unauthorized access')
    }
    console.log(listOverlap.value)
    selectedCategory.value = thisEvent.value.categoryName
    selectedDate.value = datetimeFormat.showDate(new Date(editingEvent.date)).substring(4,15)
}

// --- Edit Mode ---
    const editMode = ref(false)
    const showEditMode = () => editMode.value = true
    const hideEditMode = () => {
        editMode.value = false
        overlapStatus.value = true
        listOverlap.value = []
        edit()
    }
    const editingEvent =ref({})
    const edit = ()=>{
        editingEvent.value = {
            id: thisEvent.value.id,
            dateTime : thisEvent.value.startTime,
            date : thisEvent.value.startTime.substring(0, 10),
            time : thisEvent.value.startTime.substring(11, 16),
            notes : thisEvent.value.notes,
            attachmentName : thisEvent.value.eventAttachmentName
        }
    }

// --- Edit---

    const updateEvent = async (editingEvent,file)=>{
        const status = await checkOverlap(editingEvent)
        console.log(status)
        if(!status){
            // true เข้ามาในนี้ แปลว่า overlap 2 ใส่ไม่ได้
            console.log("Can't Edit Event : This event is Overlap!")
            await getListOverlap(editingEvent)
        }else{
            // false ไม่เข้า ส่งไป backend
            if(file !== null){
                const nameUploadFile = await uploadFile(file)
                console.log(nameUploadFile)
                if(nameUploadFile !== false){
                    editingEvent.attachmentName = nameUploadFile
                }
            }
            console.log(editingEvent)
            const dataTime = new Date(editingEvent.dateTime)
            if(editingEvent.notes !== null){
                if(editingEvent.notes.trim().length === 0){
                    editingEvent.notes = null
                }else{
                    editingEvent.notes = editingEvent.notes.trim()
                }
            }
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`, 
            {
                method:'PUT',
                headers:{
                'content-type':'application/json',
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
                },
                body: JSON.stringify({
                    id : editingEvent.id,
                    startTime: dataTime.toISOString().replace(".000Z", "Z"),
                    notes: editingEvent.notes,
                    eventAttachmentName : editingEvent.attachmentName
                })
            }).catch(error => console.log(error) );
            console.log(res.status)
            if(res.status===200){
                console.log('PUT This Event Updated')
                thisEvent.value = await res.json()
                console.log(thisEvent.value)         
                hideEditMode()
            }else if(res.status===400){
                alert("Cannot Edit This Event : The data is incorrect")
            }else if(res.status===414){
                alert("Cannot Edit This Event : The data length in the input field is too large. Please try again.")
            }else if(res.status===404){
                alert(`Cannot Edit This Event id : ${editingEvent.id}`)
            }else if(res.status===401){
                // alert('Please login')
                goToError401()
            }else if(res.status===403){
                // alert('Unauthorized access')
                goToError403()
            }else{
                alert("Error, Cannot Create New Event")
            }
        }
  }

  // --- Upload File ---
  const uploadFile = async (file) => {
        // console.log(inputFile.value.size)
            if(file !== null){
                if(file.size <= 10485760){
                var data = new FormData()
                data.append('file', file)
                const res = await fetch(`${import.meta.env.VITE_BASE_URL}/files`,{
                    method:'POST',
                    headers:{
                        'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
                    },
                    body: data
                    }
                    )
                    .catch(
                        error => console.log(error) 
                    )
                // input.value = null
                const nameUploadFile = await res.text()
                return nameUploadFile
                }else if(file.size > 10485760){
                    alert('ขนาดไฟล์ใหญ่เกิน')
                    return false
                }
            }
        }

//Check overlap----------------
    const overlapStatus = ref(true)
    const checkOverlap = async(editingEvent) => {
        const id = params.eventId
        const dateTime = new Date(editingEvent.dateTime).toISOString()
        console.log(dateTime)
        const dateFormat = dateTime.substring(0,10) + '-' + dateTime.substring(11,13) + '-' +dateTime.substring(14,16) + '-' + dateTime.substring(17,19) 
        console.log(dateFormat) //2022-05-26-04-00-00 (-7)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/edit/${id}/${dateFormat}`
        ,{
            method: "GET",
            headers:{
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }
        ).catch((error)=> {
                message.value = "Not Found Backend Server!!!"
                console.log(error)
            });
            console.log(res.status)
        if(res.status==200){
            overlapStatus.value = await res.json()
            console.log('--- Check Overlap Status ---')
            if(overlapStatus.value===false){alert(`--- Check Overlap Status --- \n ${overlapStatus.value} = This Event Overlap!`)}
        }else if(res.status===401){
            // console.log('Please login')
            goToError401()
        }else if(res.status===403){
            // console.log('Unauthorized access')
            goToError403()
        }else if(res.status===404){
            alert(`Not Found! This Event id: ${id}`)
        }
            return overlapStatus.value
    }
    
// --- Delete---
    const modalStatusDelete = ref(false)
    const showDeleteModal = () => {modalStatusDelete.value = true}
    const hideDeleteModal = () => {modalStatusDelete.value = false}

    const removeEvent = async () => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}` , {
            method: 'DELETE',
            headers:{
            'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch(error => console.log(error) );
        console.log(res.status)
        if (res.status===200) {
            console.log('DELETE successfully')
            goToViewEvent()
        }else if(res.status===404){
            console.log(`Not Found! This Event id: ${params.eventId}`)
        }else if(res.status===401){
            console.log('Please login')
        }else if(res.status===403){
            console.log('Unauthorized access')
        }else{
            console.log('Error, Cannot Delete This Event')
        }
    }

    const menuToggle = () => {
        const toggleMenu = document.getElementById("toggle-edit")
        toggleMenu.classList.toggle('showToggle')
    }

    const downloadFile = async (filename) => {
        console.log(filename)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/files/${filename}`,{
            method:'GET',
            headers:{
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch(
            error => console.log(error) 
        )
        //error 500
        

        console.log(res)
        console.log(res.url)
        // let blob = new Blob([res.url]);
        let downloadElement = document.createElement("a");
        // let href = window.URL.createObjectURL(blob); 
        downloadElement.href = res.url;
        downloadElement.download = filename; 
        document.body.appendChild(downloadElement);
        downloadElement.click(); 
        document.body.removeChild(downloadElement); 
        window.URL.revokeObjectURL(res.url); 
       
    }
</script> 

<template>

    <!-- <div style="margin-top: 10em;">
        <div class="thisEvent">
            <button @click="goBack" class="button-18" role="button">Back</button>&ensp;
            <div v-if="loading" class="subText" style="margin-top: 2em;">{{message}}</div>
            <div v-else-if="!showDetail" class="NotFoundText" style="margin-top: 2em;">
            -- Not Found Data --
            </div>

            <div v-else-if="!editMode">
                <div class="center">
                <div class="box">
                    <div class="header">
                        <div class="grid-container">
                            <span class="grid-item-pic">
                            <img :src="pathImg" alt="human">
                            </span>
                            <span class="grid-item">
                            <h3>Booking Name : {{thisEvent.bookingName}}</h3>
                            <b>Email :</b> {{thisEvent.bookingEmail}}
                            </span>
                        </div>
                    <hr>
                    </div>
                <table>
                    <tr>
                        <th>Category Name : </th>
                        <td>{{thisEvent.categoryName}}</td>
                        <th>Duration : </th>
                        <td>{{thisEvent.duration}} min.</td>
                    </tr> 
                    <tr>
                        <th>Date :</th>
                        <td>{{datetimeFormat.showDate(new Date(thisEvent.startTime))}} </td>
                        <th>Start Time :</th>
                        <td>{{datetimeFormat.showTime(new Date(thisEvent.startTime))}}</td>
                    </tr>
                    <tr>
                        <th></th>
                        <td></td>
                        <th>End Time : </th>
                        <td>{{datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(thisEvent.startTime),thisEvent.duration))}}</td>
                    </tr>
                </table>
                <div class="header">
                    <div v-if="thisEvent.notes === null">
                        <b>Notes :</b> -
                    </div>
                    <div v-else>
                        <b>Notes :</b> <br>{{thisEvent.notes}}
                    </div>
            </div>
            </div>            
            </div>
                <div class="button-right">
                    <button @click="showDeleteModal()" :class="['button-18','negative']" role="button"
                        v-show="signIn.user.role!=='lecturer'" >Delete</button>  &ensp;  
                    <button @click="showEditMode()" class="button-18" role="button"
                        v-show="signIn.user.role!=='lecturer'">Edit</button>
                </div>
            </div>

            <div v-else>
                <EditEvent :thisEvent="thisEvent"
                    :overlapStatus="overlapStatus"
                    :listOverlap="listOverlap"
                    :selectedCategory="selectedCategory"
                    :selectedDate="selectedDate"
                    @hideEditMode="hideEditMode"
                    @save="updateEvent"/>
            </div> -->

        <!-- Modal Delete -->
            <!-- <div class="modal-mask" v-show=modalStatusDelete style="display:block">
                <div class="modal-wrapper"> -->
                <!-- Modal content -->
                    <!-- <div class="modal-container">
                        <span class="close" @click="hideDeleteModal()" >&times;</span>
                        <div class="modal-header">
                            <h3>Do you want to delete this event ?</h3>
                        </div>
                        <div class="modal-button">
                            <button @click="removeEvent()" :class="['button-18', 'confirmbt']">Confirm</button>
                            &ensp;<button @click="hideDeleteModal()" class="button-18">Cancel</button>
                        </div>
                    </div>
                </div>
            </div> -->
        <!-- </div> -->
    <!-- </div> -->
    
    <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
    <div v-else-if="!showDetail" class="text-red-600 my-16 text-center">{{message}}</div>
    <div v-else class="mx-10 w-auto mt-5">
        <div class="mb-4 font-semibold"><button @click="goToViewEvent()" class="underline hover:text-yellow-300">Event</button> / Detail </div>
        <div class="bg-white rounded-2xl flex flex-row mx-auto px-10 py-5">
            <IconSchedule class="w-28 h-24 text-[#9F9FF9]"/>
            <div class="flex flex-col w-full mx-7">
                <div class="my-3">
                    <h4>booking name</h4>
                    <p>{{thisEvent.bookingName}}</p>
                </div>

                <div class="my-3">
                    <p><b>email : </b>{{thisEvent.bookingEmail}}</p>
                </div>

                <div class="flex flex-row my-3">
                    <div class="basis-1/2">
                        <h4>category Name</h4>
                        <p>{{thisEvent.categoryName}}</p>
                    </div>
                    <div class="basis-1/2">
                        <h4>duration</h4>
                        <p>{{thisEvent.duration}} min.</p>
                    </div>
                </div>
        <hr/>
                <div class="flex flex-row my-3">
                    <div class="basis-1/2">
                        <h4>date</h4>
                        <p>{{datetimeFormat.showDate(new Date(thisEvent.startTime))}}</p>
                    </div>
                    <div class="basis-1/2">
                        <h4>time</h4>
                        <p>{{datetimeFormat.showTime(new Date(thisEvent.startTime))}} - {{datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(thisEvent.startTime),thisEvent.duration))}}</p>
                    </div>
                </div>
        <hr/>
                <div class="my-3">
                    <h4>note</h4>
                    <p v-if="thisEvent.notes === null">-</p>
                    <p v-else>{{thisEvent.notes}}</p>
                </div>
                <div class="my-3">
                    <h4>file</h4>
                    <p v-if="thisEvent.eventAttachmentName === null">-</p>
                    <button v-else @click="downloadFile(thisEvent.eventAttachmentName)"
                            class="border-2 border-blue-500 px-3 py-1 rounded-lg text-blue-600 bg-[#E3ECFC]">{{thisEvent.eventAttachmentName}}</button>
                </div>
        
        <!-- Toggle edit and Delete -->
        </div>
            <IconMore v-show="signIn.user.role!=='lecturer'" @click="menuToggle()" class="w-7 h-7 float-right text-gray-400" />
            <span id="toggle-edit" class="absolute top-[170px] right-[70px] py-2 bg-[#E3ECFC] w-28 box-border drop-shadow-md rounded-xl text-gray-700 
                         transition duration-500 opacity-0 invisible">
                <div class="flex flex-col">
                    <div @click="showEditMode()" class="pr-4 py-2 inline hover:bg-white
                                active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                        <IconEdit class="w-5 h-5 ml-5 mr-2 inline align-top"/>Edit
                    </div>
                    <div @click="showDeleteModal()" class="pr-4 py-2 inline hover:bg-white
                                active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                        <IconDelete class="w-5 h-5 ml-5 mr-2 inline align-top"/>Delete
                </div>
                </div>
            </span>
        </div>
    </div>
    <!-- Modal Edit -->
    <div v-show="editMode">
    <modalEditEvent
        :thisEvent="thisEvent"
        :editingEvent="editingEvent"
        :overlapStatus="overlapStatus"
        :listOverlap="listOverlap"
        :selectedCategory="selectedCategory"
        :selectedDate="selectedDate"
        @hideEditMode="hideEditMode"
        @save="updateEvent"/>
    </div>
               
    <!-- Modal Delete -->
    <modalDeleteEvent :modalStatusDelete="modalStatusDelete"
        @hideDeleteModal="hideDeleteModal"
        @removeEvent="removeEvent"/>


</template>
 
<style scoped>
h4, b{
    font-weight: bolder;
    color: #3333A3;
}
p {
    font-size: 16px;
    margin-top: 0.25em;
}
.showToggle {
    visibility: visible;
    opacity: 1;
}
</style>