<script setup>
    import {ref, computed} from 'vue'
    defineEmits(['hideEditMode','save'])
    const props = defineProps({
        thisEvent:{
            type: Object,
            require: true
        },
        overlapStatus:{
            type: Boolean,
            require: true
        }
    })
    console.log(props.thisEvent)

    const editingEvent = ref({
            id : props.thisEvent.id,
            dateTime : props.thisEvent.startTime,
            date : props.thisEvent.startTime.substring(0, 10),
            time : props.thisEvent.startTime.substring(11, 16),
            notes : props.thisEvent.notes
        })

    // --- get Today for min Date input --- 2022-05-12
    const getToday = (currentDate) => {
        const date = currentDate.getDate() <= 9 ? '0'+ currentDate.getDate() : currentDate.getDate()
        const month = (currentDate.getMonth()+1) <= 9 ? '0'+ (currentDate.getMonth()+1) : (currentDate.getMonth()+1)
        const year = currentDate.getFullYear()
        // console.log(year +'-'+ month +'-'+ date)
        return year +'-'+ month +'-'+ date
    }

    // -- date past ? ---
    const checkDate = computed(()=>{ 
        //true = เวลาเป็นอดีต
        editingEvent.value.dateTime = editingEvent.value.date + 'T' + editingEvent.value.time
        return new Date(editingEvent.value.dateTime) < new Date()
    })
    const checkEdited = computed(()=>{
        //true = ยังไม่แก้ข้อมูล
        return String(props.thisEvent.startTime).substring(0,16) === editingEvent.value.dateTime && props.thisEvent.notes === editingEvent.value.notes
    })

    // -- calculate End time ---
    const addMinutes = (date,duration) => {
        const changeDate = date
        changeDate.setMinutes(changeDate.getMinutes()+duration)
        return changeDate
    }
    // --- show End Time --- 
    const timeUnits = ['AM','PM']
    const getEndTime = (givenDate) => {
        if(givenDate != 'Invalid Date'){
            // const hour = givenDate.getHours()%12 <= 9 ? '0'+ givenDate.getHours()%12 : givenDate.getHours()%12
            let hour = givenDate.getHours()%12
            if(hour == 0){
                hour = 12
            } else if(hour <= 9){
                hour = '0' + hour
            }
            const minute = givenDate.getMinutes() <= 9 ? '0' + givenDate.getMinutes() : givenDate.getMinutes()
            const timeUnit = timeUnits[Math.floor(givenDate.getHours()/12)]
            return hour + ':' + minute + ' ' + timeUnit
        }
    }

    // --- show Date Time ---
    const days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
    const monthsName = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sep','Oct','Nov','Dec']
    const a = new Date('2022-05-04')
    const showDateTime = (givenDate) => {
        const day = days[givenDate.getDay()]
        const date = givenDate.getDate()
        const month = monthsName[givenDate.getMonth()]
        const year = givenDate.getFullYear()
        // const hour = givenDate.getHours()%12 <= 9 ? '0'+ givenDate.getHours()%12 : givenDate.getHours()%12
        // const minute = givenDate.getMinutes() <= 9 ? '0' + givenDate.getMinutes() : givenDate.getMinutes()
        // const timeUnit = timeUnits[Math.floor(givenDate.getHours()/12)]
        return day + ' ' + date + ' ' + month + ' ' + year + ' | ' + givenDate.toLocaleTimeString('th-TH').substring(0,5)
    }

</script>
 
<template>
    <h2 style="text-align: center;"> -- Editing Mode -- </h2>
    <div class="center">
    <div class="box">
        <div class="header">
            <h3>Booking Name : {{thisEvent.bookingName}}</h3>   
            <b>Email :</b> {{thisEvent.bookingEmail}}
            <hr>
        </div>
        
        <table>
            <tr>
                <th>Category Name : </th>
                <td>{{thisEvent.categoryName}}</td>
                <th>Duration : </th>
                <td>{{thisEvent.duration}} min</td>
            </tr>
            <tr class="subText">
                <th>Same Date-Time : </th>
                <td colspan="2">{{showDateTime(new Date(String(thisEvent.startTime).substring(0,16)))}}</td>
            </tr>
            <tr>
                <th>New Date-Time : </th>
                <td colspan="3">
                    {{showDateTime(new Date(editingEvent.dateTime))}}  &ensp;
                    <span v-show="checkDate" class="warning">
                        <span class="warning">&#9888;</span> The choosen time is in the past, choose again
                    </span>
                </td>
            </tr>
            <tr>
                <th>Date :</th>
                <td><input type="date" v-model="editingEvent.date" :min="getToday(new Date())"> </td>
                <th>Start Time :</th>
                <td><input type="time" v-model="editingEvent.time"></td>
            </tr>
            <tr>
                <th></th>
                <td></td>
                <th>End Time : </th>
                <td>{{ getEndTime(addMinutes(new Date(editingEvent.dateTime),thisEvent.duration)) }}</td>
            </tr>
        </table>   
        <div class="header">
            <b>Notes : </b>
            <br>
            <textarea v-model="editingEvent.notes" maxlength="500" >{{editingEvent.notes}}</textarea>
        </div>
        <div style="text-align: center;" class="warning" v-show="!overlapStatus">It seems that you choose the time that overlap other previous events. Please choose another time.</div>
    </div>
    </div>

        <div class="button-right">
            <button @click="$emit('hideEditMode')" :class="['button-18','negative']" role="button">Cancal</button> &ensp;
            <button @click="$emit('save',editingEvent)" class="button-18" role="button" :disabled="checkDate || checkEdited">Save</button>
        </div>    
</template>
 
<style scoped>
    h3 {
        color: #FFA21A;
    }
    b {
        color: #FFCB4C;
    }
    table {
       width: 70%;
       margin: 1em 3em;
    }
    th {
        width: 30%;
        min-width: 125px;
        text-align: right;
        padding: 0.25em 1em;
    }
    td {
        width: 30%;
       
    }
    .center{
        margin: auto;
        width: 80%;
    }
    .box {
        background-color: #3333A3;
        padding: 1em 2%;
        border-radius: 30px;
        margin: 1em 1%;
        /* min-width: 600px; */
        color: white;
        -o-object-fit: cover;
        object-fit: cover;
    }
    .header{
        margin: 1em 3em;
    }
    .warning{
        color: orangered;
    }
    .button-right {
        float: right;
        margin: 0 10% 2em 0;
    }
    .subText{
        color: rgb(129, 111, 143);
    }
    textarea {
        width: 100%;
        height: 7em; 
        border-radius: 10px;
    }
    input, select {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0;
        text-rendering: auto;
        overflow: visible;
    }
</style>