<script setup>
    import {ref, onMounted, computed} from 'vue'
    import {useRouter} from 'vue-router'
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    import { useSignIn } from '../state/signIn.js';

    import IconLoading from '../components/icons/iconLoading.vue'
    import IconError from '../components/icons/iconError.vue'

    const datetimeFormat = useDatetimeFormat()
    const signIn = useSignIn()

    // --- GET Event Category Name to drop down list ---
    const eventCategories = ref()
    const loading =ref()
    const loading2 = ref()
    const message = ref()
    const getEventCategoryName = async () => {
        loading.value = true
        message.value = "loading..."
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories/name`
        ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
      ).catch((error)=> {
            message.value = "Not Found Backend Server!!!"
            console.log(error)
            console.log(`GET List All CategoryName Fail`)
        });
        console.log(res.status)
        if(res.status==200){
            eventCategories.value = await res.json()
            console.log(`GET List All CategoryName OK`)
            loading.value = false
        }else if(res.status===401){
            let errorText = await res.text()
            console.log(errorText)
            if(errorText==="Token is expired."){
                await signIn.sendRefreshToken()
            }else{
                message.value = "Please login"
            }
            console.log('Please login')
        }else if(res.status===403){
            console.log('Unauthorized access')
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
        // || newEvent.value.email === "" 
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
        //console.log(startTime)  //2022-05-26-04-00-00 (-7)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/book/${categoryId}/${startTime}`
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
            console.log('Please login')
        }else if(res.status===403){
            console.log('Unauthorized access')
        }
        return overlapStatus.value
    }

    // --- GET List Overlap ---
    const listOverlap = ref([])
    const selectedCategory = ref('')
    const selectedDate = ref('')

    const getListOverlap = async (newEvent) => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/list-book-overlap/${newEvent.category.id}/${newEvent.dateTime.substring(0,10)}`
        ,{
        method: "GET",
        headers:{
          'Content-Type' : 'application/json',
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
      ).catch((error)=> console.log(error));
      console.log(res.status)
      if(res.status==200){
            listOverlap.value = await res.json()
            console.log(`-- Get List Overlap Times --`)
            console.log(listOverlap.value)
        }else if(res.status===401){
            console.log('Please login')
        }else if(res.status===403){
            console.log('Unauthorized access')
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

    // --- Create New Event ---
    const createNewEvent = async (newEvent) => {
        console.log(newEvent)
        const status = await checkOverlap(newEvent)
        console.log(status)    // false = overlap = เพิ่มไม่ได้
        if(!status){
        // true จะเข้ามาในนี้ 
            console.log("Can't Create New Event : This event is Overlap!")
            await getListOverlap(newEvent)
        }else{
            const nameUploadFile = await uploadFile()
            console.log(nameUploadFile)
            if(nameUploadFile !== false){
                const dataTime = new Date(newEvent.dateTime)
                console.log(dataTime.toISOString().replace(".000Z", "Z"))
                console.log(signIn.user.role)
                let newEmail = ""
                //ถ้าเป็น guest หรือ admin จะใช้ input email ได้
                if(signIn.user.role==='admin'||signIn.statusLogin== false){
                    newEmail = newEvent.email
                }else{
                    //แต่ถ้าไม่ใช่ จะใช้ fix email ตาม user.email แทน
                    newEmail = signIn.user.email
                }
                console.log(newEmail)
                loading2.value = true
                const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`,{
                    method:'POST',
                    headers:{
                    'content-type':'application/json',
                    'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
                    },
                    body: JSON.stringify({
                        bookingName: newEvent.bookingName.trim(),
                        // bookingEmail: newEvent.email,
                        // bookingEmail: signIn.user.email,
                        bookingEmail: newEmail,
                        startTime: dataTime.toISOString().replace(".000Z", "Z"),
                        notes:newEvent.notes.length === 0 ? null : newEvent.notes.trim(),
                        eventCategoryId: newEvent.category.id,
                        eventAttachmentName: nameUploadFile
                    })
                }).catch(error => console.log(error));
                loading2.value =false
                console.log(res.status)
                //ถ้าสร้าง event ไม่ได้ ให้ delete file ที่พึ่ง upload ไป


                if(res.status===200){
                    const newId = await res.json()
                    if(signIn.statusLogin===true){
                        goThisEvent(newId)
                        console.log('Create New Event OK')
                    }else{
                        alert(`Create New Event OK \n your event id : ${newId}`)
                        clearForm()
                    }
                }else if(res.status===400){
                    console.log("Cannot Create New Event : The data is incorrect")
                }else if(res.status===414){
                    console.log("Cannot Create New Event : The data length in the input field is too large. Please try again.")
                }else if(res.status===404){
                    console.log("Cannot Create New Event : Not Found! Category id")
                }else if(res.status===401){
                    console.log('Please login')
                }else if(res.status===403){
                    console.log('Unauthorized access')
                }else{
                    console.log("Error, Cannot Create New Event")
                }
            }else{
                alert('ขนาดไฟล์ใหญ่เกิน')
            }
            
        }
    }

    // --- check validate Email ---
    const emailStatus = ref(false)
    const emailValidation = (inputEmail) => {
        if(inputEmail !== ""){
            const mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
            emailStatus.value =  inputEmail.match(mailformat) ? false : true
        }else{
            emailStatus.value = false
        }
    }

    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    const goThisEvent = (newId) => myRouter.push({name: 'ThisEvent', params:{eventId:newId}})

    const clearForm = () => {
        newEvent.value = { bookingName : "", email : "", category : {}, dateTime : "", notes : ""} 
        overlapStatus.value = true
        let input = document.getElementById('bFile')
        input.value = null
        inputFile.value = null
    }


    // -- Test add File --

    const inputFile = ref(null)
    const newFile = (e)=>{
        const file = e.target.files[0]
        // console.log(file)
        inputFile.value = file
    }
    const uploadFile = async () => {
        // const input = document.getElementById('bFile');
        // console.log(inputFile.value.size)
            if(inputFile.value !== null){
                if(inputFile.value.size <= 10485760){
                var data = new FormData()
                data.append('file', inputFile.value)
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
            }else if(inputFile.value.size > 10485760){
                console.log('ขนาดไฟล์ใหญ่เกิน')
                return false
            }else if(inputFile.value === null){
                const nameUploadFile = null
                return nameUploadFile
            }
        } 
            
    }
    // Validate File
    const checkFileSize = computed(()=>{ 
        //true = ขนาดไฟล์ใหญ่เกิน
        if(inputFile.value !== null){
            console.log(inputFile.value.size)
            return inputFile.value.size > 10485760
        }
    })

    const checkFileName = computed(()=>{ 
        //true = ชื่อผิด(มี .. / ในชื่อ)
        if(inputFile.value !== null){
            return inputFile.value.name.includes("/") 
        }
    })
    const checkFileLength = computed(()=>{ 
        //true = ชื่อยาวเกิน (ชื่อไฟล์-datetime ไม่เกิน 100)
        if(inputFile.value !== null){
            return inputFile.value.name.length > 85
        }
    })
    // const downloadFile = async (filename) => {
    //     console.log(filename)
    //     const res = await fetch(`${import.meta.env.VITE_BASE_URL}/files/${filename}`,{
    //         method:'GET',
    //         headers:{
    //             'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
    //         }
    //     }).catch(
    //         error => console.log(error) 
    //     )
    //     console.log(res)
    //     console.log(res.url)
    //     // let blob = new Blob([res.url]);
    //     let downloadElement = document.createElement("a");
    //     // let href = window.URL.createObjectURL(blob); //create the download url
    //     downloadElement.href = res.url;
    //     downloadElement.download = filename; //the name of the downloaded file
    //     document.body.appendChild(downloadElement);
    //     downloadElement.click(); //click to file
    //     document.body.removeChild(downloadElement); //remove the element 
    //     window.URL.revokeObjectURL(res.url); //release the object  of the blob
       
    // }
    
</script>
 
<template>
    <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
    <div v-else class="mx-10 w-auto mb-10 mt-5">
        <div class="bg-white rounded-2xl flex flex-col mx-auto px-10 py-3">
            <h2 class="text-xl font-semibold pb-2 border-b-2 border-gray-400">
                Create a reserved event
            </h2>
            <div class="flex flex-row border-b-2 mx-7 pb-1.5">
                <h3 class="basis-1/5 pt-2">Event</h3>
                <div class="basis-4/5 flex flex-col">
                    <div class="mt-2">
                        <label for="bName">booking name</label>
                        <input type="text" id="bName" name="bName" v-model="newEvent.bookingName" maxlength="100" size="50"
                                class="bg-[#E3ECFC] p-1 rounded-sm">&ensp;
                        <span class="text-[12px] text-gray-400">{{newEvent.bookingName.trim().length}} / 100</span>
                    </div>
                    <div class="my-2">
                        <label for="bEmail">assign using email</label>
                        <span v-if="signIn.statusLogin == false || signIn.user.role==='admin'">
                            <input type="email" id="bEmail" name="bEmail" v-model="newEvent.email" size="50" maxlength="50" @blur="emailValidation(newEvent.email)"
                                    class="bg-[#E3ECFC] p-1 rounded-sm">&ensp;
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
                        <label for="bCategory">event category</label>
                        <select id="bCategory" v-model="newEvent.category" class="bg-[#E3ECFC] w-[250px] p-1 rounded">
                            <option selected value={}>--Not selected--</option>
                            <option v-for="category in eventCategories" :value="category" >
                                {{ category.categoryName}}
                            </option>
                        </select>
                    </div>
                    <div class="basis-1/2 my-2" v-show="newEvent.category.id > 0">
                        <h4>duration</h4>
                        <p>{{newEvent.category.duration}} min.</p>
                    </div>
                </div>
            </div>

            <div class="flex flex-row border-b-2 mx-7 pb-1.5">
                <h3 class="basis-1/5 pt-2">Appointment</h3>
                <div class="basis-4/5 flex flex-row">
                    <div class="basis-1/2 my-2">
                        <label for="bTime">start date-time</label>
                        <input type="datetime-local" id="bTime" name="bTime" v-model="newEvent.dateTime" :min="datetimeFormat.getTodayDatetime(new Date())" 
                                class="bg-[#E3ECFC] p-1 rounded-sm"/>
                        <span v-show="new Date(newEvent.dateTime) < new Date()" class="text-red-400 block">
                            &#9888; The choosen time is in the past, please choose again
                        </span>
                    </div>
                    <div class="basis-1/4 my-2" v-show="newEvent.category.id > 0 && newEvent.dateTime !== ''">
                        <h4>date</h4>
                        <p>{{datetimeFormat.showDateNoDay(new Date(newEvent.dateTime))}}</p>
                    </div>
                    <div class="basis-1/4 my-2" v-show="newEvent.category.id > 0 && newEvent.dateTime !== ''">
                        <h4>time</h4>
                        <p>{{datetimeFormat.showTime(new Date(newEvent.dateTime))}} - {{datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(newEvent.dateTime),newEvent.category.duration))}}</p>
                    </div>
                </div>
            </div>

            <div class="flex flex-row border-b-0 mx-7 pb-1">
                <h3 class="basis-1/5 pt-2">Detail</h3>
                <div class="basis-4/5 flex flex-col">
                    <div class="mt-2">
                        <label for="bNote">note</label>
                        <textarea maxlength="500" v-model="newEvent.notes"
                            class="bg-[#E3ECFC] p-1 rounded-sm h-16 w-4/5"></textarea>
                            &ensp;<span class="text-[12px] text-gray-400">{{newEvent.notes.trim().length}} / 500</span>
                    </div>
                    <div class="my-1">
                        <label for="bFile">file</label>
                        <input type="file" id="bFile" name="bFile" @change="newFile">
                        <span v-show="checkFileName || checkFileSize" class="text-red-500">&ensp; &#9888;</span> 
                        <span v-show="checkFileSize" class="text-red-500">ขนาดไฟล์ใหญ่เกิน</span>
                        &nbsp;
                        <span v-show="checkFileName" class="text-red-500">ชื่อไฟล์ไม่ถูกต้อง</span>
                        <span v-show="checkFileLength" class="text-red-500">ชื่อไฟล์ยาวเกินไป</span>
                    </div>
                </div>
            </div>
        
            <div class="text-right mb-3">
                <!-- <button @click="downloadFile('z2.png')"
                        class="bg-yellow-400 text-black py-1.5 px-4 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300 disabled:bg-gray-300">get file </button>
                &ensp; -->
                <!-- <button @click="uplaodFile()" class="bg-yellow-400 text-black py-1.5 px-4 rounded-full 
                           hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Test Add File</button>
                &ensp; -->
                <button @click="clearForm()" class="bg-red-100 text-red-500 py-1.5 px-4 rounded-full 
                           hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Clear</button>
                &ensp;
                <button @click="createNewEvent(newEvent)" :disabled="checkBeforeAdd || checkFileSize || checkFileName || checkFileLength" type="submit"
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
