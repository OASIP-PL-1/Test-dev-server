<script setup>
import {ref, onMounted, computed} from 'vue'
import {useRouter} from 'vue-router'

// --- Get Event Category to drop down list ---
const eventCategories = ref()
const loading =ref()
const message = ref()
const getEventCategories = async () => {
    loading.value = true
    message.value = "loading..."
    // const res = await fetch('http://localhost:8080/api/eventCategories')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories`)
      .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    eventCategories.value = await res.json()
    loading.value = false
}

onMounted(async () => {
    await getEventCategories()
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
    return newEvent.value.bookingName === "" 
    || newEvent.value.email === "" || !emailValidation(newEvent.value.email)
    || Object.keys(newEvent.value.category).length === 0 
    || newEvent.value.dateTime === ""
    || new Date(newEvent.value.dateTime) < new Date()
    || newEvent.value.category.id === undefined
})

{{new Date()}}
// --- check overlap ---
const overlapStatus = ref(true)
const checkOverlap = async(newEvent) => {
    // console.log("checkOverlap")
    console.log(newEvent)
    const categoryId = newEvent.category.id
    const dateTime = new Date(newEvent.dateTime)
    const startTime = dateTime.getFullYear() + "-" + (dateTime.getMonth()+1) + "-" + dateTime.getDate() + "-" 
        + dateTime.getHours() + "-" + dateTime.getMinutes() + "-" + dateTime.getSeconds()
    // console.log(startTime)
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/book/${categoryId}/${startTime}`)
        .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    overlapStatus.value = await res.json()
    return overlapStatus.value
}

// --- Create New Event ---
const createNewEvent = async (newEvent)=>{
    console.log(newEvent)
    const status = await checkOverlap(newEvent)
    console.log(status)
    //false = ใส่ไม่ได้
    if(!status){
        // true จะเข้ามาในนี้
        console.log('error, cannot create this event')
    }else{
        const dataTime = new Date(newEvent.dateTime)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`,{
            method:'POST',
            headers:{
            'content-type':'application/json'
            },
            body: JSON.stringify({
                bookingName: newEvent.bookingName,
                bookingEmail: newEvent.email,
                startTime: dataTime.toISOString().replace(".000Z", "Z"),
                notes:newEvent.notes.length === 0 ? null : newEvent.notes,
                eventCategoryId: newEvent.category.id
            })
        }).catch(error => console.log(error));
        if(res.status===200){
            const newId = await res.json()       
            goThisEvent(newId)
        }else {
            console.log(res.status)
            console.log('error, cannot create')
        }
    }
}


// --- check validate Email ---
const emailValidation = (inputEmail) => {
    const mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/
    return inputEmail.match(mailformat) ? true : false
}

// --- get Today for min DateTime input --- ('2022-05-12T00:00')
const getToday = (currentDate) => {
    const date = currentDate.getDate() <= 9 ? '0'+ currentDate.getDate() : currentDate.getDate()
    const month = (currentDate.getMonth()+1) <= 9 ? '0'+ (currentDate.getMonth()+1) : (currentDate.getMonth()+1)
    const year = currentDate.getFullYear()
    console.log(year +'-'+ month +'-'+ date +'T00:00')
    return year +'-'+ month +'-'+ date +'T00:00'
}

// --- calculate End time --- 
const addMinutes = (date,duration) => {
    const changeDate = date
    changeDate.setMinutes(changeDate.getMinutes()+duration)
    return changeDate
}
// --- show End Time --- (16:30 AM)
const timeUnits = ['AM','PM']
const getEndTime = (givenDate) => {
    const hour = givenDate.getHours()%12 <= 9 ? '0'+ givenDate.getHours()%12 : givenDate.getHours()%12
    const minute = givenDate.getMinutes() <= 9 ? '0' + givenDate.getMinutes() : givenDate.getMinutes()
    const timeUnit = timeUnits[Math.floor(givenDate.getHours()/12)]
    return hour + ':' + minute + ' ' + timeUnit
}


const myRouter = useRouter()
const goBack = () => myRouter.go(-1)
const goThisEvent = (newId) => myRouter.push({name: 'ThisEvent', params:{eventId:newId}})

const clearForm = () => newEvent.value = { bookingName : "", email : "", category : {}, dateTime : "", notes : ""} 



</script>
 
<template>
    <div class="thisEvent">
        <button @click="goBack" class="button-18" role="button">Back</button>
        <div class="box">
        <h2>Create a reservation event</h2>
        <hr>
            <table>
                <tr>
                    <th><label for="bName">Booking name :</label></th>
                    <td><input type="text" id="bName" name="bName" v-model="newEvent.bookingName" maxlength="100" size="50"></td>
                </tr>
                <tr>
                    <th><label for="bEmail">Your email :</label></th>
                    <td>
                        <input type="email" id="bEmail" name="bEmail" v-model="newEvent.email" pattern=".+@globex\.com" required size="50">
                    <br><span v-show="!emailValidation(newEvent.email) && newEvent.email != ''" class="warning">Sorry! an invalid email!</span>
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
                        <b> Duration : </b>  &ensp; {{newEvent.category.duration}} mins
                        </span>
                    </td>
                </tr>
                <tr>
                    <th><label for="bDate">Start Time :</label></th>
                    <td>
                        <input type="datetime-local" id="bTime" name="bTime" v-model="newEvent.dateTime" :min="getToday(new Date())">
                        &emsp;
                        <span v-show="newEvent.category.id > 0 && newEvent.dateTime !== ''"> 
                        <b for="bDate" >End Time : </b>&ensp;
                            <span>{{ getEndTime(addMinutes(new Date(newEvent.dateTime),newEvent.category.duration))}}</span>
                        </span> 
                        <br>
                        <span v-show="new Date(newEvent.dateTime) < new Date()" class="warning">
                            <span class="warning">&#9888;</span> The choosen time is in the past, choose again
                        </span>
                        <span v-show="false">Overlap นะจ๊ะ</span>
                        <!-- <span v-show="checkFuture">The choosen date is in the past, choose again</span> -->
                    </td>
                    <!-- <td style="width: 20%;">
                        <button class="button-18" :disabled="checkButton" @click="checkOverlap">Check</button>
                    </td> -->
                </tr>
                <tr>
                    <th><label for="bNotes">Notes :</label></th>
                    <td><textarea v-model="newEvent.notes" rows="7" cols="50" maxlength="500"></textarea></td>
                </tr>
            </table>
            <span style="color:red" v-show="!overlapStatus">It seems that you choose the time that overlap other previous events. Please choose another time.</span>
        </div>
    </div>
        <div class="button-right">
            <button @click="createNewEvent(newEvent)" :disabled="checkBeforeAdd" class="button-18" role="button" type="submit">Save</button> &ensp;
            <button @click="clearForm" class="button-18" role="button" >Clear</button>
            <br>
        </div>

</template>

<style scoped>
    h2 {
        color: #FFCB4C;
    }
    th, b, span {
        color: white;
    }
    .box {
        background-color: #3333A3;
        padding: 1em 2em 3em 2em;
        border-radius: 30px;
        margin: 1em 10em;
        text-align: center;
        -o-object-fit: cover;
        object-fit: cover;
    }
    .thisEvent{
        padding-left: 2em;
        padding-right: 2em;
    }
    input, select, textarea {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0;
        text-rendering: auto;
        overflow: visible;
        -o-object-fit: cover;
        object-fit: cover;
    }
    .button-right {
        float: right;
        margin-right: 2em;
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
        -o-object-fit: cover;
        object-fit: cover;
    }
    td {
        text-align: left;
        width: 30%;
        -o-object-fit: cover;
        object-fit: cover;
    }
    .bt-save {
        background-color: rgb(173, 67, 169);
    }
</style>
