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
const checkData = computed(()=>{ 
    return newEvent.value.bookingName === "" 
    || newEvent.value.email === "" 
    || Object.keys(newEvent.value.category).length === 0 
    || newEvent.value.dateTime === ""
    || new Date(newEvent.value.dateTime) < new Date()
    ||  newEvent.value.category.id === undefined
})

// --- Create New Event ---
const createNewNote = async (newEvent)=>{
        const dataTime = new Date(newEvent.dateTime)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`,{
        method:'POST',
        headers:{
        'content-type':'application/json'
        },
        body: JSON.stringify({
            bookingName: newEvent.bookingName,
            bookingEmail: newEvent.email,
            startTime: dataTime.toISOString(),
            notes:newEvent.notes.length === 0 ? null : newEvent.notes,
            eventCategoryId: newEvent.category.id
        })
    }).catch(error => console.log(error) );
        console.log(res.status)
    if(res.status===200){
        const addedEvent = await res.json()           
        console.log(addedEvent)
        goBack()
    }else {
        console.log('error, cannot create')
    }
}

// --- show Date --- 
// const days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
// const months = ['01','02','03','04','05','06','07','08','09','10','11','12']
// const a = new Date('2022-05-04')
const timeUnits = ['AM','PM']
const getDate = (givenDate) => {
        // console.log(givenDate)
        // const day = days[givenDate.getDay()]
        // const date = givenDate.getDate()
        // const month = givenDate.getMonth()+1
        // const year = givenDate.getFullYear()
    const hour = givenDate.getHours()%12 <= 9 ? '0'+ givenDate.getHours()%12 : givenDate.getHours()%12
    const minute = givenDate.getMinutes() <= 9 ? '0' + givenDate.getMinutes() : givenDate.getMinutes()
    const timeUnit = timeUnits[Math.floor(givenDate.getHours()/12)]
    return hour + ':' + minute + ' ' + timeUnit
}

// --- calculate End time --- 
const addMinutes = (date,duration) => {
    const changeDate = date
    changeDate.setMinutes(changeDate.getMinutes()+duration)
    // console.log(changeDate)
    return changeDate
}


    
const myRouter = useRouter()
const goBack = () => myRouter.go(-1)

const clearForm = () => {
    newEvent.value = { bookingName : "", email : "", category : {}, dateTime : "", notes : ""} 
}
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
                    <td><input type="text" id="bName" name="bName" v-model="newEvent.bookingName" maxlength="100"></td>
                </tr>
                <tr>
                    <th><label for="bEmail">Your email :</label></th>
                    <td><input type="email" id="bEmail" name="bEmail" v-model="newEvent.email" pattern=".+@globex\.com" required></td>
                </tr>
                <tr>
                    <th><label> Category :</label></th>
                    <td>
                        <select v-model="newEvent.category">
                            <option selected>--Not selected--</option>
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
                <!-- <tr v-show="newEvent.category.id > 0">
                    <th><label> Duration: </label></th>
                    <td><span>{{newEvent.category.duration}}</span></td>
                </tr> -->
                <tr>
                    <th><label for="bDate">Start Time :</label></th>
                    <td>
                        <input type="datetime-local" id="bTime" name="bTime" v-model="newEvent.dateTime">
                        &emsp;
                        <span v-show="newEvent.category.id > 0 && newEvent.dateTime !== ''"> 
                        <b for="bDate" >End Time : </b>&ensp;
                            <span>{{ getDate(addMinutes(new Date(newEvent.dateTime),newEvent.category.duration)) }}</span>
                        </span> 
                        <br>
                        <span v-show="new Date(newEvent.dateTime) < new Date()" class="warning">
                            <span class="warning">&#9888;</span> The choosen date is in the past, choose again
                        </span>
                        <!-- <span v-show="checkFuture">The choosen date is in the past, choose again</span> -->
                    </td>
                </tr>
                <tr>
                    <th><label for="bNotes">Notes :</label></th>
                    <td><textarea v-model="newEvent.notes" rows="7" cols="50" maxlength="500"></textarea></td>
                </tr>
            </table>
        </div>
    </div>
        <div class="button-right">
            <button @click="createNewNote(newEvent)" :disabled="checkData" class="button-18" role="button" type="submit">Save</button> &ensp;
            <button @click="clearForm" class="button-18" role="button" >Clear</button>
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
        margin: 1em 2em;
        text-align: center;
        min-width: 600px;
    }
    .thisEvent{
        padding-left: 2em;
        padding-right: 2em;
    }
    input, select, textarea {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0;
    }
    .button-right {
        float: right;
        margin-right: 2em;
    }
    .warning{
        color:red
    }
    table {
        margin-left: auto;
        margin-right: auto;
    }
    tr {
        padding: auto;
    }
    th {
        vertical-align: top;
        text-align: right;
        width: 20%;
    }
    td {
        text-align: left;
        width: 50%;
    }
    .bt-save {
        background-color: rgb(173, 67, 169);
    }

    

</style>
