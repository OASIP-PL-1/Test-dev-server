<script setup>
    import {ref, onMounted, computed} from 'vue'
    import {useRouter} from 'vue-router'
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    import { useSignIn } from '../state/signIn.js';

    import IconLoading from '../components/icons/iconLoading.vue'
    import IconError from '../components/icons/iconError.vue'
    import IconDelete from '../components/icons/iconDelete.vue'

    const datetimeFormat = useDatetimeFormat()
    const signIn = useSignIn()

    const myRouter = useRouter()
    const goThisEvent = (newId) => myRouter.push({name: 'ThisEvent', params:{eventId:newId}})
    const goToError401 = () => myRouter.push({name: 'Error401'}) 
    const goToError403 = () => myRouter.push({name: 'Error403'})
    const goToError500 = () => myRouter.push({name: 'Error500'})

    // --- GET Event Category Name to drop down list ---
    const eventCategories = ref()
    const loading =ref()
    const loading2 = ref()
    const message = ref()
    const getEventCategoryName = async () => {
        loading.value = true
        message.value = "loading..."
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories/name`
        ,{  method: "GET",
            headers:{
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
      }
      ).catch((error)=> {
            // message.value = "Not Found Backend Server!!!"
            console.log(error)
            alert(`GET List All CategoryName Fail`)
            goToError500()
        });
        if(res.status==200){
            eventCategories.value = await res.json()
            loading.value = false
        }else if(res.status===401){
            let errorText = await res.text()
            if(errorText==="Token is expired."){
                await signIn.sendRefreshToken()
            }else{
                message.value = "Please login"
            }
            alert('Please login')
            goToError401()
        }else if(res.status===403){
            alert('Unauthorized access')
            goToError403()
        }
    }

    onMounted(async () => {
        await getEventCategoryName()
    })

    const newEvent = ref({
        bookingName : "",
        email : "",
        category : {},
        dateTime : "",
        notes : ""
    }) 

    // --- check if the information is filled out ---
    const checkBeforeAdd = computed(()=>{ 
        return newEvent.value.bookingName.trim() === "" 
        || newEvent.value.email === "" 
        || emailStatus.value
        || Object.keys(newEvent.value.category).length === 0 
        || newEvent.value.dateTime === ""
        || new Date(newEvent.value.dateTime) < new Date()
        || newEvent.value.category.id === undefined
    })

    // --- check overlap ---
    const overlapStatus = ref(true)
    const checkOverlap = async(newEvent) => {
        const categoryId = newEvent.category.id
        const dateTime = new Date(newEvent.dateTime).toISOString()
        const startTime = dateTime.substring(0,10) + '-' + dateTime.substring(11,13) + '-' +dateTime.substring(14,16) + '-' + dateTime.substring(17,19)  
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/book/${categoryId}/${startTime}`
        ,{  method: "GET",
            headers:{
             'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch((error)=> {
            console.log(error)
            alert("Not Found Backend Server!!!")
        });
        if(res.status==200){
            overlapStatus.value = await res.json()
        }else if(res.status===401){
            alert('Please login')
            goToError401()
        }else if(res.status===403){
            alert('Unauthorized access')
            goToError403()
        }
        return overlapStatus.value
    }

    // --- if check overlap is false --> GET List Overlap ---
    const listOverlap = ref([])
    const selectedCategory = ref('')
    const selectedDate = ref('')

    const getListOverlap = async (newEvent) => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/list-book-overlap/${newEvent.category.id}/${newEvent.dateTime.substring(0,10)}`
        ,{  method: "GET",
            headers:{
                'Content-Type' : 'application/json',
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
      }).catch((error)=> console.log(error));
      if(res.status==200){
            listOverlap.value = await res.json()
        }else if(res.status===401){
            alert('Please login')
            goToError401()
        }else if(res.status===403){
            alert('Unauthorized access')
            goToError403()
        }
        selectedCategory.value = (eventCategories.value.find((category)=> category.id === newEvent.category.id)).categoryName
        selectedDate.value = datetimeFormat.showDate(new Date(newEvent.dateTime)).substring(4,15)
    }

    // --- show Time for list Overlap --- (10:00 - 10:30)
    const showRangeTime = (eventOverlap) => {
        if(listOverlap.value.length > 0){
            return datetimeFormat.showTime(new Date(eventOverlap.startTime)) + ' - ' + datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(eventOverlap.startTime),eventOverlap.duration))
        }
    }

    // --- CREATE Event ---
    const createNewEvent = async (newEvent) => {
        const status = await checkOverlap(newEvent)
        // ถ้า status เป็น false = overlap = เพิ่มไม่ได้
        if(!status){
        // true จะเข้ามาในนี้ 
            alert(`--- Check Overlap Status --- \n ${overlapStatus.value} = This Event Overlap!`)
            await getListOverlap(newEvent)
        }else{
            const nameUploadFile = await uploadFile()
            console.log(nameUploadFile)
                const dataTime = new Date(newEvent.dateTime)
                let newEmail = ""
                //ถ้าเป็น guest หรือ admin จะใช้ input email ได้ 
                //แต่ถ้าไม่ใช่ จะใช้ fix email ตาม user.email แทน
                if(signIn.user.role==='admin'||signIn.statusLogin== false){
                    newEmail = newEvent.email
                }else{
                    newEmail = signIn.user.email
                }
                loading2.value = true
                const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`,{
                    method:'POST',
                    headers:{
                    'content-type':'application/json',
                    'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
                    },
                    body: JSON.stringify({
                        bookingName: newEvent.bookingName.trim(),
                        bookingEmail: newEmail,
                        startTime: dataTime.toISOString().replace(".000Z", "Z"),
                        notes:newEvent.notes.length === 0 ? null : newEvent.notes.trim(),
                        eventCategoryId: newEvent.category.id,
                        eventAttachmentName: nameUploadFile
                    })
                }).catch(error => console.log(error));
                loading2.value =false
                if(res.status===200){
                    const newId = await res.json()
                    goThisEvent(newId)
                }else if(res.status===400){
                    removeFile(nameUploadFile)
                    alert("Cannot Create New Event : The data is incorrect")
                }else if(res.status===414){
                    removeFile(nameUploadFile)
                    alert("Cannot Create New Event : The data length in the input field is too large. Please try again.")
                }else if(res.status===404){
                    removeFile(nameUploadFile)
                    alert("Cannot Create New Event : Not Found! Category id")
                }else if(res.status===401){
                    removeFile(nameUploadFile)
                    goToError401()
                }else if(res.status===403){
                    removeFile(nameUploadFile)
                    alert('Unauthorized access')
                }else{
                    removeFile(nameUploadFile)
                    alert("Error, Cannot Create New Event")
                }
        }
    }

    // --- Check Validate Email ---
    const emailStatus = ref(false)
    const emailValidation = (inputEmail) => {
        if(inputEmail !== ""){
            const mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
            emailStatus.value =  inputEmail.match(mailformat) ? false : true
        }else{
            emailStatus.value = false
        }
    }

    // -- File --
    const clearForm = () => {
        newEvent.value = { bookingName : "", email : "", category : {}, dateTime : "", notes : ""} 
        overlapStatus.value = true
        let input = document.getElementById('bFile')
        input.value = null
        inputFile.value = null
        fileOK.value = null
    }

    const inputFile = ref(null)
    const fileOK = ref(null)
    const newFile = (e)=>{
        const file = e.target.files[0]
        inputFile.value = file
        checkFile()
    }
    const clearFile = () => {
        let input = document.getElementById('bFile')
        input.value = null
        inputFile.value = null
    }

    const uploadFile = async () => {
        let file
        checkFile() === false ? file = fileOK.value : file = inputFile.value
        if(file !== null){
            var data = new FormData()
            data.append('file', file)
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/files`,{
                method:'POST',
                headers:{
                    'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
                },
                body: data
            }).catch(error => console.log(error) )
            const nameUploadFile = await res.text()
            return nameUploadFile
        }else{
            return null
        }
        
    } 
    // -- check validate file --
    const errorFileText = ref('')
    const checkFile = () => {
        if(inputFile.value !== null){
            if(inputFile.value.size > 10485760){
                errorFileText.value = "The file size cannot be larger than 10 MB"
                return false
            }else if(inputFile.value.name.includes("/")){
                errorFileText.value = "The file name is invalid."
                return false
            }else if(inputFile.value.name.length > 85){
                errorFileText.value = "The file name is too long."
                return false
            }else{
                errorFileText.value = ''
                fileOK.value = inputFile.value
                return true
            }
        }
    }
    // -- DELETE FILE --
    const removeFile = async (filename) => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/files/${filename}` , {
            method: 'DELETE',
            headers:{
            'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch(error => console.log(error));
    }
    
</script>
 
<template>
    <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
    <div v-else class="m-10 w-auto mb-10">
        <div class="bg-white rounded-lg flex flex-col mx-auto px-10 py-6">
            <h2 class="text-xl font-semibold pb-2 border-b-2 border-gray-400">
                Create a reserved event
            </h2>
            <div class="flex flex-row border-b-2 mx-7 pb-1.5">
                <h3 class="basis-1/5 pt-2">Event</h3>
                <div class="basis-4/5 flex flex-col">
                    <div class="mt-2">
                        <label for="bName">Booking name</label>
                        <input type="text" id="bName" name="bName" v-model="newEvent.bookingName" maxlength="100" size="50"
                                class="bg-gray-50 border border-gray-300 rounded-lg w-11/12 p-1 mt-1">&ensp;
                        <span class="text-[12px] text-gray-400 inline">{{newEvent.bookingName.trim().length}} / 100</span>
                    </div>
                    <div class="my-2">
                        <label for="bEmail">Assign using email</label>
                        <span v-if="signIn.statusLogin == false || signIn.user.role==='admin'">
                            <input type="email" id="bEmail" name="bEmail" v-model="newEvent.email" size="50" maxlength="50" @blur="emailValidation(newEvent.email)"
                                    class="bg-gray-50 border border-gray-300 rounded-lg w-5/12 p-1 mt-1">&ensp;
                            <span class="text-[12px] text-gray-400">{{newEvent.email.trim().length}} / 50</span>
                        </span>
                        <span v-else>{{signIn.user.email}}</span><br/>
                        <span v-show="emailStatus" class="text-red-400">Sorry! an invalid email!</span>
                    </div>
                </div>
            </div>

            <div class="flex flex-row border-b-2 mx-7 pb-1.5">
                <h3 class="basis-1/5 pt-2">Category</h3>
                <div class="basis-4/5 flex flex-row">
                    <div class="basis-1/2 my-2">
                        <label for="bCategory">Event category</label>
                        <select id="bCategory" v-model="newEvent.category" 
                                class="bg-gray-50 border border-gray-300 rounded-lg w-6/12 p-1 mt-1">
                            <option selected value={}>--Not selected--</option>
                            <option v-for="category in eventCategories" :value="category" >
                                {{ category.categoryName}}
                            </option>
                        </select>
                    </div>
                    <div class="basis-1/2 my-2" v-show="newEvent.category.id > 0">
                        <h4>Duration</h4>
                        <p>{{newEvent.category.duration}} min.</p>
                    </div>
                </div>
            </div>

            <div class="flex flex-row border-b-2 mx-7 pb-1.5">
                <h3 class="basis-1/5 pt-2">Appointment</h3>
                <div class="basis-4/5 flex flex-row">
                    <div class="basis-1/2 my-2">
                        <label for="bTime">Start date-time</label>
                        <input type="datetime-local" id="bTime" name="bTime" v-model="newEvent.dateTime" :min="datetimeFormat.getTodayDatetime(new Date())" 
                                class="bg-gray-50 border border-gray-300 rounded-lg w-6/12 p-1 mt-1"/>
                        <span v-show="new Date(newEvent.dateTime) < new Date()" class="text-red-400 block">
                            &#9888; The choosen time is in the past, please choose again
                        </span>
                    </div>
                    <div class="basis-1/4 my-2" v-show="newEvent.category.id > 0 && newEvent.dateTime !== ''">
                        <h4>Date</h4>
                        <p>{{datetimeFormat.showDateNoDay(new Date(newEvent.dateTime))}}</p>
                    </div>
                    <div class="basis-1/4 my-2" v-show="newEvent.category.id > 0 && newEvent.dateTime !== ''">
                        <h4>Time</h4>
                        <p>{{datetimeFormat.showTime(new Date(newEvent.dateTime))}} - {{datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(newEvent.dateTime),newEvent.category.duration))}}</p>
                    </div>
                </div>
            </div>

            <div class="flex flex-row border-b-0 mx-7 pb-1">
                <h3 class="basis-1/5 pt-2">Detail</h3>
                <div class="basis-4/5 flex flex-col">
                    <div class="mt-2">
                        <label for="bNote">Note</label>
                        <textarea maxlength="500" v-model="newEvent.notes"
                            class="bg-gray-50 border border-gray-300 rounded-lg h-16 w-9/12 p-1 mt-1"></textarea>
                            &ensp;<span class="text-[12px] text-gray-400">{{newEvent.notes.trim().length}} / 500</span>
                    </div>
                    <div class="my-1">
                        <label for="bFile">file</label>
                        <input type="file" id="bFile" name="bFile" @change="newFile">
                        <IconDelete class="w-4 h-4 inline text-red-500" @click="clearFile()" v-show="(inputFile!==null)"/>
                        <br/>
                        <span v-show="errorFileText !=='' " class="text-red-500">&#9888; {{errorFileText}}</span>
                    </div>
                </div>
            </div>
        
            <div class="text-right">
                <button @click="clearForm()" class="bg-red-100 text-red-500 py-1.5 px-4 rounded-full 
                           hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Clear</button>
                &ensp;
                <button @click="createNewEvent(newEvent)" :disabled="checkBeforeAdd" type="submit"
                        class="bg-[#5C5CFF] text-white py-1.5 px-4 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300 disabled:bg-gray-300">Save</button>
            </div>

            <div v-show="!overlapStatus" class="bg-red-200 m-auto w-4/5 px-5 py-2 text-center text-red-600 rounded-lg" >
                <IconError class="w-5 h-5 inline"/>
                It seems that you choose the time that overlap other previous events. These are the <strong>exist</strong> event in the day you choose.
                <div class="my-2" v-show="listOverlap.length > 0">
                    <div class="border-t-[1px] border-red-500 py-2"><strong> Category : </strong> {{selectedCategory}} &ensp; <strong> Date : </strong> {{selectedDate}}</div>
                    <div class="my-3">
                        <span v-for="(event,index) in listOverlap" :key="index" class="m-2 px-2 py-1 border-2 border-red-500">
                            {{showRangeTime(event)}}
                        </span> 
                    </div>
                </div>
            </div>
        </div>
        <div v-show="loading2" class="bg-black w-full h-full bg-opacity-10 fixed top-0 left-0 block">
            <div class="w-full py-5 px-7 m-auto mt-32 text-center">
                <IconLoading class="text-blue-200"/>
            </div>
        </div>
    </div>

</template>

<style scoped>
h3{
    font-size: 16px;
    font-weight: bolder;
}
h4, label{
    display: block;
    font-size: 14px;
    font-weight: bold;
    color: #3333A3;
    
}
</style>
