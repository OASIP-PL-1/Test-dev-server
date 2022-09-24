<script setup>
    import {ref, onMounted, computed} from 'vue'
    import {useRouter} from 'vue-router'
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    
    const datetimeFormat = useDatetimeFormat()

    // --- GET Event Category Name to drop down list ---
    const eventCategories = ref()
    const loading =ref()
    const message = ref()
    const getEventCategoryName = async () => {
        loading.value = true
        message.value = "loading..."
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories/name`
    //     ,{
    //     method: "GET",
    //     headers:{
    //       'Content-Type' : 'application/json',
    //       'Authorization' : 'Bearer '+localStorage.getItem('jwtToken')
    //     }
    //   }
      ).catch((error)=> {
            message.value = "Not Found Backend Server!!!"
            console.log(error)
            console.log(`GET List All CategoryName Fail`)
        });
        eventCategories.value = await res.json()
        loading.value = false
        console.log(res.status)
        if(res.status==200){
            console.log(`GET List All CategoryName OK`)
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
        //console.log(startTime)  //2022-05-26-04-00-00 (-7)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/book/${categoryId}/${startTime}`
    //     ,{
    //     method: "GET",
    //     headers:{
    //       'Content-Type' : 'application/json',
    //       'Authorization' : 'Bearer '+localStorage.getItem('jwtToken')
    //     }
    //   }
      ).catch((error)=> {
            message.value = "Not Found Backend Server!!!"
            console.log(error)
        });
        overlapStatus.value = await res.json()
        console.log(res.status)
        if(res.status==200){
            console.log(`--- Check Overlap Status ---`)
        }
        return overlapStatus.value
    }

    // --- GET List Overlap ---
    const listOverlap = ref([])
    const selectedCategory = ref('')
    const selectedDate = ref('')

    const getListOverlap = async (newEvent) => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/list-book-overlap/${newEvent.category.id}/${newEvent.dateTime.substring(0,10)}`
    //     ,{
    //     method: "GET",
    //     headers:{
    //       'Content-Type' : 'application/json',
    //       'Authorization' : 'Bearer '+localStorage.getItem('jwtToken')
    //     }
    //   }
      ).catch((error)=> console.log(error));
        listOverlap.value = await res.json()
        console.log(res.status)
        if(res.status==200){
            console.log(`-- Get List Overlap Times --`)
            console.log(listOverlap.value)
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
    const createNewEvent = async (newEvent)=>{
        console.log(newEvent)
        const status = await checkOverlap(newEvent)
        console.log(status)    // false = overlap = เพิ่มไม่ได้
        if(!status){
        // true จะเข้ามาในนี้ 
            console.log("Can't Create New Event : This event is Overlap!")
            await getListOverlap(newEvent)
        }else{
            const dataTime = new Date(newEvent.dateTime)
            console.log(dataTime.toISOString().replace(".000Z", "Z"))
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`,{
                method:'POST',
                headers:{
                'content-type':'application/json',
                // 'Authorization' : 'Bearer '+localStorage.getItem('jwtToken')
                },
                body: JSON.stringify({
                    bookingName: newEvent.bookingName.trim(),
                    bookingEmail: newEvent.email,
                    startTime: dataTime.toISOString().replace(".000Z", "Z"),
                    notes:newEvent.notes.length === 0 ? null : newEvent.notes.trim(),
                    eventCategoryId: newEvent.category.id
                })
            }).catch(error => console.log(error));
            console.log(res.status)
            if(res.status===200){
                const newId = await res.json()
                goThisEvent(newId)
                console.log('Create New Event OK')
            }else if(res.status===400){
                console.log("Cannot Create New Event : The data is incorrect")
            }else if(res.status===414){
                console.log("Cannot Create New Event : The data length in the input field is too large. Please try again.")
            }else if(res.status===404){
                console.log("Cannot Create New Event : Not Found! Category id")
            }else{
                console.log("Error, Cannot Create New Event")
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
    }
</script>
 
<template>
    <div style="margin-top: 10em;">
    <div class="thisEvent">
        <button @click="goBack" class="button-18" role="button">Back</button>
        <div class="box">
        <h2>Create a reservation event</h2>
        <hr>
            <table>
                <tr>
                    <th><label for="bName">Booking name :</label></th>
                    <td>
                        <input type="text" id="bName" name="bName" v-model="newEvent.bookingName" maxlength="100" size="50">&ensp;
                        <span class="subText">{{newEvent.bookingName.trim().length}} / 100</span>
                    </td>
                </tr>
                <tr>
                    <th><label for="bEmail">Your email :</label></th>
                    <td>
                        <input type="email" id="bEmail" name="bEmail" v-model="newEvent.email" size="50" maxlength="50" @blur="emailValidation(newEvent.email)">&ensp;
                        <span class="subText">{{newEvent.email.trim().length}} / 50</span>
                    <br><span v-show="emailStatus" class="warning">Sorry! an invalid email!</span>
                    </td>
                </tr>
                <tr>
                    <th><label> Category :</label></th>
                    <td>
                        <select v-model="newEvent.category">
                            <option selected value={}>--Not selected--</option>
                            <option v-for="category in eventCategories" :value="category" >
                                {{ category.categoryName}}
                            </option>
                        </select>
                        &emsp;
                        <span v-show="newEvent.category.id > 0">
                        <b> Duration : </b>  &ensp; {{newEvent.category.duration}} min.
                        </span>
                    </td>
                </tr>
                <tr>
                    <th><label for="bDate">Start Time :</label></th>
                    <td>
                        <input type="datetime-local" id="bTime" name="bTime" v-model="newEvent.dateTime" :min="datetimeFormat.getTodayDatetime(new Date())">
                        &emsp;
                        <span v-show="newEvent.category.id > 0 && newEvent.dateTime !== ''"> 
                        <b for="bDate" >End Time : </b>&ensp;
                            <span>{{ datetimeFormat.getEndTime(datetimeFormat.addMinutes(new Date(newEvent.dateTime),newEvent.category.duration))}}</span>
                        </span> 
                        <br>
                        <span v-show="new Date(newEvent.dateTime) < new Date()" class="warning">
                            <span class="warning">&#9888;</span> The choosen time is in the past, choose again
                        </span>
                    </td>
                </tr>
                <tr>
                    <th><label for="bNotes">Notes :</label></th>
                    <td><textarea v-model="newEvent.notes" rows="7" cols="50" maxlength="500"></textarea>&ensp;
                        <span class="subText">{{newEvent.notes.trim().length}} / 500</span>
                    </td>
                </tr>
            </table>
            <div class="overlap-bar" v-show="!overlapStatus">
                <span class="warning" >It seems that you choose the time that overlap other previous events. These are the <strong>exist</strong> event in the day you choose.</span>
                <div class="overlap-detail">
                    <div v-show="listOverlap.length > 0"> 
                        <b>Category : </b> {{selectedCategory}} &ensp; <b> Date : </b> {{selectedDate}}
                    </div>
                    <br> 
                    <span v-for="(event,index) in listOverlap" :key="index" class="span-time">{{showRangeTime(event)}}</span>              
                </div>
            </div>
        </div>
        <div class="button-right">
            <button @click="clearForm" :class="['button-18','negative']" role="button">Clear</button> &ensp;
            <button @click="createNewEvent(newEvent)" :disabled="checkBeforeAdd" class="button-18" role="button" type="submit">Save</button>
        </div>
        </div>
    </div>
</template>

<style scoped>
    .subText {
        color:rgb(199, 199, 199);
        font-size: smaller;
    }
    h2 {
        color: #FFCB4C;
    }
    th, b, span {
        color: white;
    }
    .box {
        background-color: #3333A3;
        padding: 1em 2em 1em 2em;
        border-radius: 30px;
        margin: 1em 0em;
        margin-left: auto;
        margin-right: auto;
        text-align: center;
        -o-object-fit: cover;
        object-fit: cover;
        max-width: 800px;
        box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
    }
    .thisEvent {
        padding: 0 2em;
    }
    input, select, textarea {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0;
        text-rendering: auto;
        overflow: visible;
        -o-object-fit: cover;
        object-fit: cover;
        /* max-inline-size: 200em; */
    }
    .button-right {
        float: right;
        /* margin: 0 12% 2em 0; */
        margin-right: 27%;
    }
    .warning{
        color: orangered;
    }
    table {
        margin-left: auto;
        margin-right: auto;
        -o-object-fit: cover;
        object-fit: cover;
    }
    tr {
        padding: auto;
        -o-object-fit: cover;
        object-fit: cover;
    }
    th {
        vertical-align: top;
        text-align: right;
        width: 19%;
        padding: 5px 2px;
        -o-object-fit: cover;
        object-fit: cover;
    }
    td {
        text-align: left;
        width: 30%;
        padding: 5px 2px;
        -o-object-fit: cover;
        object-fit: cover;
    }

    .overlap-bar {
        text-align: center;
        padding: 1em;
        width: 80%;
        margin-left: auto;
        margin-right: auto;
    }
    .overlap-detail b{
        color: #FFCB4C;
        padding: 1em;
    }
    .overlap-detail {
        color: #FFCB4C;
        padding: 1em 1em 0em 1em;
        font-weight: 0;
    }
    .span-time {
        color: #FFA21A;
        margin: 4px 10px;
        padding: 4px 10px;
        border-radius: 10px;
        border-style: dashed;
        display: inline-block;
        word-wrap: break-word;
        overflow-wrap: break-word;
    }
</style>
