<script setup>
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    import {ref, computed} from 'vue'
    import IconSchedule from './icons/iconSchedule.vue'
    import IconCancel from './icons/iconCancel.vue'
    import IconError from './icons/iconError.vue'
    import IconDelete from './icons/iconDelete.vue'
    
    const datetimeFormat = useDatetimeFormat()
    defineEmits(['hideEditMode','save'])
    const props = defineProps({
        thisEvent:{
            type: Object,
            require: true
        },editingEvent:{
            type:Object,
            require:true
        },
        overlapStatus:{
            type: Boolean,
            require: true
        },
        listOverlap:{ type: Object , default:[]},
        selectedCategory:{ type: String},
        selectedDate:{ type: String }
    })
    console.log(props.thisEvent)
    console.log(props.editingEvent)
    
    // const editingEvent = ref({
    //         id : props.thisEvent.id,
    //         dateTime : props.thisEvent.startTime,
    //         date : props.thisEvent.startTime.substring(0, 10),
    //         time : props.thisEvent.startTime.substring(11, 16),
    //         notes : props.thisEvent.notes
    //     })

    // -- date past ? ---
    const checkDate = computed(()=>{ 
        //true = เวลาเป็นอดีต
        props.editingEvent.dateTime = props.editingEvent.date + 'T' + props.editingEvent.time
        return new Date(props.editingEvent.dateTime) < new Date()
    })

    const checkFile = computed(()=>{ 
        //true = ขนาดไฟล์ใหญ่เกิน
        if(inputFile.value !== null){
            return inputFile.value.size > 10485760
        }
    })

    const checkEdited = computed(()=>{
        //true = ยังไม่แก้ข้อมูล
        return String(props.thisEvent.startTime).substring(0,16) === props.editingEvent.dateTime 
                && props.thisEvent.notes === props.editingEvent.notes && props.thisEvent.attachmentName === props.editingEvent.attachmentName
    })


    // --- show Time for list Overlap --- (10:00 - 10:30)
    const showRangeTime = (eventOverlap) => {
    if(props.listOverlap.length > 0){
        return datetimeFormat.showTime(new Date(eventOverlap.startTime)) + ' - ' + datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(eventOverlap.startTime),eventOverlap.duration))
        }
    }
    // --- not show Time overlap of ThisEvent 
    const checkThisTime = (event) => {
        if(new Date(props.editingEvent.dateTime) !== new Date(String(props.thisEvent.startTime).substring(0,16))){
            return !(event.startTime.substring(11,16) === props.thisEvent.startTime.substring(11,16))
        }
    }
    const inputFile = ref(null)
    const newFile = (e)=>{
        const file = e.target.files[0]
        // console.log(file)
        inputFile.value = file
    }
    const clearFile = () => {
        let input = document.getElementById('eFile')
        input.value = null
        inputFile.value = null
        props.editingEvent.attachmentName = null
    }
   
</script>
 
<template>
    <!-- <h2>-- Editing Mode --</h2>
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
                <td colspan="2">{{datetimeFormat.showDateTime(new Date(String(thisEvent.startTime).substring(0,16)))}}</td>
            </tr>
            <tr>
                <th>New Date-Time : </th>
                <td colspan="3">
                    {{datetimeFormat.showDateTime(new Date(editingEvent.dateTime))}}  &ensp;
                    <span v-show="checkDate" class="warning">
                        <span class="warning">&#9888;</span> The choosen time is in the past, choose again
                    </span>
                </td>
            </tr>
            <tr>
                <th>Date :</th>
                <td><input type="date" v-model="editingEvent.date" :min="datetimeFormat.getTodayDate(new Date())"> </td>
                <th>Start Time :</th>
                <td><input type="time" v-model="editingEvent.time"></td>
            </tr>
            <tr>
                <th></th>
                <td></td>
                <th>End Time : </th>
                <td>{{ datetimeFormat.getEndTime(datetimeFormat.addMinutes(new Date(editingEvent.dateTime),thisEvent.duration)) }}</td>
            </tr>
        </table>   
        <div class="header">
            <b>Notes : </b>
            <br>
            <textarea v-model="editingEvent.notes" maxlength="500" >{{editingEvent.notes}}</textarea>
        </div>
        <div class="overlap-bar" v-show="!overlapStatus">
            <div class="warning">It seems that you choose the time that overlap other previous events. These are the <strong>exist</strong> event in the day you choose.</div>
            <div class="overlap-detail" v-show="listOverlap.length > 0">
                <div> 
                    <b>Category : </b> {{selectedCategory}} &ensp; <b> Date : </b> {{selectedDate}}
                </div>
                <br>
                <span v-for="(event,index) in listOverlap" :key="index" class="span-time"
                      v-show="checkThisTime(event)">
                    {{showRangeTime(event)}}
                </span> 
            </div>
        </div>

    </div>
    </div>

        <div class="button-right">
            <button @click="$emit('hideEditMode')" :class="['button-18','negative']" role="button">Cancal</button> &ensp;
            <button @click="$emit('save',editingEvent)" class="button-18" role="button" :disabled="checkDate || checkEdited">Save</button>
        </div>     -->

        <div class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
        <div class="bg-white w-[1000px] m-auto mt-5 py-3 px-5 text-left rounded-xl">
            <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
                Edit Event
                <button class="float-right" @click="$emit('hideEditMode'), clearFile()"><IconCancel class="w-5 h-5"/></button>
            </h2>
            <div class="flex flex-col m-3">
                <div class="flex flex-row mx-5 pb-5 border-b-2">
                    <IconSchedule class="basis-1/5 w-24 h-24 text-[#9F9FF9]"/>
                    <div class="basis-4/5 flex flex-col w-full ml-5">
                        <div class="my-1">
                            <h4>booking name</h4>
                            <p>{{thisEvent.bookingName}}</p>
                        </div>
                        <div class="my-1">
                            <p><b>email : </b>{{thisEvent.bookingEmail}}</p>
                        </div>
                        <div class="flex flex-row my-1">
                            <div class="basis-1/2">
                                <h4>category Name</h4>
                                <p>{{thisEvent.categoryName}}</p>
                            </div>
                            <div class="basis-1/2">
                                <h4>duration</h4>
                                <p>{{thisEvent.duration}} min.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="flex flex-row mx-5 my-2 pb-5 border-b-2">
                    <h3 class="basis-1/5 text-[16px] font-semibold">Appointment</h3>
                    <div class="basis-4/5 flex flex-col w-full ml-5">
                        <div v-show="new Date(editingEvent.dateTime)!='Invalid Date'" class="flex flex-row">
                            <p class="basis-1/2">{{datetimeFormat.showDate(new Date(editingEvent.dateTime))}}</p>
                            <p class="basis-1/2">{{datetimeFormat.showTime(new Date(editingEvent.dateTime))}} - {{datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(editingEvent.dateTime),thisEvent.duration))}}</p>
                        </div>
                        <span v-show="checkDate" class="text-red-500">
                            &#9888; The choosen time is in the past, please choose again
                        </span>
                        <div class="flex flex-row my-1">
                            <div class="basis-1/2">
                                <h3>date</h3>
                                <input type="date" v-model="editingEvent.date" :min="datetimeFormat.getTodayDate(new Date())"
                                    class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3]"/>
                            </div>
                            <div class="basis-1/2">
                                <h3>time</h3>
                                <input type="time" v-model="editingEvent.time"
                                       class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3]"/>
                            </div>
                        </div>

                    </div>
                </div>
                <div class="flex flex-row mx-5 my-2">
                    <h3 class="basis-1/5 text-[16px] font-semibold my-2 text-[#3333A3]">More details</h3>
                    <div class="basis-4/5 flex flex-col w-full ml-5">
                        <div class="flex flex-row my-1">
                            <div class="basis-1/2 pr-5">
                                <h3>note</h3>
                                <textarea maxlength="500" v-model="editingEvent.notes"
                                    class="bg-[#E3ECFC] p-1 text-[#3333A3] rounded-sm h-20 w-full">{{editingEvent.notes}}</textarea>
                            </div>
                            <div class="basis-1/2">
                                <h3>file</h3>
                                <span v-if="editingEvent.attachmentName === null && inputFile === null">-</span>
                                <span v-else-if="editingEvent.attachmentName !== null && inputFile === null">
                                    {{editingEvent.attachmentName}} <IconDelete class="w-4 h-4 inline text-red-400" @click="clearFile()"/></span>
                                <span v-else-if="inputFile !== null">
                                    {{inputFile.name}} <IconDelete class="w-4 h-4 inline text-red-500" @click="clearFile()"/></span>
                                <span v-show="checkFile" class="text-red-500">&#9888; ขนาดไฟล์ใหญ่เกิน</span>
                                <input type="file" id="eFile" name="eFile" @change="newFile" class="text-white block"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="text-right mb-3">
                <button @click="$emit('hideEditMode'), clearFile()" class="bg-red-100 text-red-500 py-1.5 px-4 rounded-full 
                           hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                &ensp;
                <button @click="$emit('save',editingEvent, inputFile)" :disabled="checkDate || checkFile || checkEdited"
                        class="bg-[#5C5CFF] text-white py-1.5 px-4 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300 disabled:bg-gray-300">Save</button>
            </div>

            <div v-show="!overlapStatus" class="bg-red-200 m-auto px-5 py-2 text-center text-red-600 rounded-lg" >
                <IconError class="w-5 h-5 inline"/>
                It seems that you choose the time that overlap other previous events. <br/>These are the <strong>exist</strong> event in the day you choose.
                <div class="my-2" v-show="listOverlap.length > 0">
                    <div class="border-t-[1px] border-red-500 py-2"><strong> Category : </strong> {{selectedCategory}} &ensp; <strong> Date : </strong> {{selectedDate}}</div>
                    <div class="my-3">
                        <span v-for="(event,index) in listOverlap" :key="index" class="m-2 px-2 py-1 border-2 border-red-500"
                            v-show="checkThisTime(event)">
                            {{showRangeTime(event)}}
                        </span> 
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>
 
<style scoped>
h4, b{
    font-weight: bolder;
    color: #8181FA;
}
h3{
    font-weight: bolder;
    color: #3333A3;
}
p {
    font-size: 14px;
    margin-top: 0em;
}
    /* h2 {
        text-align: center;
    }
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
    } */
</style>