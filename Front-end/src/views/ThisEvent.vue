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

// --- routerlink --- 
    const myRouter = useRouter()
    const goToViewEvent= () => myRouter.push({ name: 'ViewEvent'})
    const goToError401 = () => myRouter.push({ name: 'Error401'})
    const goToError403 = () => myRouter.push({ name: 'Error403'})
    const goToError500 = () => myRouter.push({ name: 'Error500'})
    
// --- Get all list --- 
    const thisEvent = ref({})
    const showDetail = ref({})
    const loading =ref()
    const message = ref()

    const getThisEvent = async () => {
        loading.value = true
        showDetail.value = false
        message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}`
        ,{  method: "GET",
            headers:{
            'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch((error)=> {
            console.log(error)
            // message.value = "Not Found Backend Server!!!"
            goToError500()
        });
        if(res.status===200){
            thisEvent.value = await res.json()
            loading.value = false
            showDetail.value = true
            edit()
        }else if(res.status===404){
            alert(`Not Found! This Event id: ${params.eventId}`)
            loading.value = false
            showDetail.value = false
            message.value = `Not Found! This Event id: ${params.eventId}`
        }else if(res.status===401){
            let errorText = await res.text()
            if(errorText==="Token is expired."){
                await signIn.sendRefreshToken()
            }else{
                message.value = "Please login"
                goToError401()
            }
        }else if(res.status===403){
            goToError403()
        }
        loading.value = false
    }

    onBeforeMount(async () => {
      await getThisEvent()
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
    ).catch((error)=> {console.log(error); goToError500()})
    if(res.status==200){
        listOverlap.value = await res.json()
    }else if(res.status===404){
        alert(`Not Found This Event id: ${editingEvent.eventId}`)
    }else if(res.status===401){
        goToError401()
    }else if(res.status===403){
        goToError403()
    }
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
        if(!status){ // !status = true เข้ามาในนี้ แปลว่า overlap ใส่ไม่ได้
            alert(`--- Check Overlap Status --- \n ${overlapStatus.value} = This Event Overlap!`)
            await getListOverlap(editingEvent)
        }else{// !status = false = ไม่ overlap แก้ไข event ได้
            // check event edited ?
            if(editingEvent.notes === thisEvent.value.notes && 
                editingEvent.dateTime === thisEvent.value.startTime.substring(0,16) &&
                editingEvent.attachmentName === thisEvent.value.eventAttachmentName && 
                file === null){
                //ถ้าไม่มีการแก้ไขไฟล์ ไม่ต้อง PUT ให้ backend
                alert('This event is not modified')
                hideEditMode()
            }else{
                // check edit file
                if(file !== null){
                    const nameUploadFile = await uploadFile(file)
                    if(nameUploadFile !== null){
                        editingEvent.attachmentName = nameUploadFile
                    }
                }else{
                    editingEvent.attachmentName = null
                }
                // check edit note
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
            }).catch((error)=> {console.log(error); goToError500()})
            if(res.status===200){
                thisEvent.value = await res.json()       
                hideEditMode()
            }else if(res.status===400){
                alert("Cannot Edit This Event : The data is incorrect")
                removeFile(editingEvent.attachmentName)
            }else if(res.status===414){
                alert("Cannot Edit This Event : The data length in the input field is too large. Please try again.")
                removeFile(editingEvent.attachmentName)
            }else if(res.status===404){
                alert(`Cannot Edit This Event id : ${editingEvent.id}`)
                removeFile(editingEvent.attachmentName)
            }else if(res.status===401){
                removeFile(editingEvent.attachmentName)
                goToError401()
            }else if(res.status===403){
                removeFile(editingEvent.attachmentName)
                goToError403()
            }else{
                alert("Error, Cannot Create New Event")
            }
        }
    }          
  }

// --- Upload File ---
const uploadFile = async (file) => {
    if(file !== null){
        var data = new FormData()
        data.append('file', file)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/files`,{
            method:'POST',
            headers:{
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            },
            body: data
        }
        ).catch((error)=> {console.log(error); goToError500()})
        const nameUploadFile = await res.text()
        return nameUploadFile
    }else{
        return null
    }
}
// -- DELETE FILE --
const removeFile = async (filename) => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/files/${filename}` , {
            method: 'DELETE',
            headers:{
            'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch((error)=> {console.log(error); goToError500()})
    }

//Check overlap----------------
    const overlapStatus = ref(true)
    const checkOverlap = async(editingEvent) => {
        const id = params.eventId
        const dateTime = new Date(editingEvent.dateTime).toISOString()
        const dateFormat = dateTime.substring(0,10) + '-' + dateTime.substring(11,13) + '-' +dateTime.substring(14,16) + '-' + dateTime.substring(17,19) 
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
        if(res.status==200){
            overlapStatus.value = await res.json()
        }else if(res.status===401){
            alert('Please login')
            goToError401()
        }else if(res.status===403){
            alert('Unauthorized access')
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
        if (res.status===200) {
            goToViewEvent()
        }else if(res.status===404){
            alert(`Not Found! This Event id: ${params.eventId}`)
        }else if(res.status===401){
            goToError401()
        }else if(res.status===403){
            goToError403()
        }else{
            alert('Error, Cannot Delete This Event')
        }
    }

    const menuToggle = () => {
        const toggleMenu = document.getElementById("toggle-edit")
        toggleMenu.classList.toggle('showToggle')
    }

    const downloadFile = async (filename) => {
        await fetch(`${import.meta.env.VITE_BASE_URL}/files/${filename}`,{
            method:'GET'
        }).then( async (response) => {
            if(response.status === 200){
                // let blob = new Blob([res.url]);
                let downloadElement = document.createElement("a");
                // let href = window.URL.createObjectURL(blob); 
                downloadElement.href = response.url;
                downloadElement.download = filename; 
                document.body.appendChild(downloadElement);
                downloadElement.click(); 
                // document.body.removeChild(downloadElement); 
                // window.URL.revokeObjectURL(res.url); 
            }else if(response.status === 404){
                let errorText = await response.text()
                alert(errorText)
            }
          
        }).catch(error => console.log(error))
    }
</script> 

<template>
    <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
    <div v-else-if="!showDetail" class="text-red-600 my-16 text-center">{{message}}</div>
    <div v-else class="mx-10 w-auto mt-5">
        <div class="mb-4 font-semibold"><button @click="goToViewEvent()" class="underline hover:text-yellow-300">Event</button> / Detail </div>
        <div class="bg-white rounded-lg flex flex-row mx-auto py-5 px-7 w-4/5 text-[16px]">
            <IconSchedule class="w-28 h-24 text-[#9F9FF9]"/>
            <div class="flex flex-col w-full ml-7">
                <div class="mb-2">
                    <h4>Booking name</h4>
                    <p>{{thisEvent.bookingName}}</p>
                </div>

                <div class="mb-2">
                    <p><b>Email : </b>{{thisEvent.bookingEmail}}</p>
                </div>

                <div class="flex flex-row border-b-1 pb-1.5">
                    <div class="basis-1/2 pt-2">
                        <h4>Category Name</h4>
                        <p>{{thisEvent.categoryName}}</p>
                    </div>
                    <div class="basis-1/2 pt-2">
                        <h4>Duration</h4>
                        <p>{{thisEvent.duration}} min.</p>
                    </div>
                </div>
        <hr/>
                <div class="flex flex-row border-b-1 pb-1.5 mt-2">
                    <div class="basis-1/2 pt-2">
                        <h4>Date</h4>
                        <p>{{datetimeFormat.showDate(new Date(thisEvent.startTime))}}</p>
                    </div>
                    <div class="basis-1/2 pt-2">
                        <h4>Time</h4>
                        <p>{{datetimeFormat.showTime(new Date(thisEvent.startTime))}} - {{datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(thisEvent.startTime),thisEvent.duration))}}</p>
                    </div>
                </div>
        <hr/>
                <div class="mt-4">
                    <h4>Note</h4>
                    <p v-if="thisEvent.notes === null">-</p>
                    <p v-else>{{thisEvent.notes}}</p>
                </div>
                <div class="mt-2">
                    <h4>File</h4>
                    <p v-if="thisEvent.eventAttachmentName === null">-</p>
                    <button v-else @click="downloadFile(thisEvent.eventAttachmentName)"
                            class="border border-gray-600 px-1 py-1 rounded text-black bg-gray-100 text-[14px]">{{thisEvent.eventAttachmentName}}</button>
                </div>
        <!-- Toggle edit and Delete -->
        </div>
            <IconMore v-show="signIn.user.role!=='lecturer'" @click="menuToggle()" class="w-7 h-7 float-right text-gray-400" />
            <span id="toggle-edit" class="absolute top-[175px] right-[150px] py-2 bg-[#FFF] w-28 box-border drop-shadow-md rounded-lg text-gray-700 
                         transition duration-500 opacity-0 invisible">
                <div class="flex flex-col">
                    <div @click="showEditMode()" class="pr-4 py-1 inline hover:bg-[#ECECFE]
                                    active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                        <IconEdit class="w-5 h-5 ml-5 mr-2 inline align-top"/>Edit
                    </div>
                    <div @click="showDeleteModal()" class="pr-4 py-2 inline hover:bg-[#ECECFE]
                                active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                        <IconDelete class="w-5 h-5 ml-5 mr-2 inline align-top"/>Delete
                </div>
                <!-- 
                    <IconMore @click="menuToggle()" class="w-7 h-7 float-right text-gray-400" />
                <span id="toggle-edit" class="absolute top-[175px] right-[150px] py-2 bg-[#FFF] w-28 box-border drop-shadow-md rounded-lg text-gray-700 
                            transition duration-500 opacity-0 invisible">
                    <div class="flex flex-col">
                        <div @click="showEditMode()" class="pr-4 py-1 inline hover:bg-[#ECECFE]
                                    active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                            <IconEdit class="w-5 h-5 ml-5 mr-2 inline align-top"/>Edit
                        </div>
                        <div @click="showDeleteModal()" class="pr-4 py-1 inline hover:bg-[#ECECFE]
                                    active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                            <IconDelete class="w-5 h-5 ml-5 mr-2 inline align-top"/>Delete
                    </div>
                    </div>
                </span>
                 -->
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