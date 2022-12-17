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
    const fileOK = ref(null)
    const newFile = (e)=>{
        const file = e.target.files[0]
        inputFile.value = file
        checkFile()
    }
    const errorFileText = ref('')
    const checkFile = () => {
        if(inputFile.value !== null){
            if(inputFile.value.size > 10485760){
                errorFileText.value = "The file size cannot be larger than 10 MB."
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
    const clearFile = () => {
        let text = 'Are you sure you want to delete this file?'
        if (confirm(text) == true) {
            let input = document.getElementById('eFile')
            input.value = null
            inputFile.value = null
            fileOK.value = null
            props.editingEvent.attachmentName = null
            errorFileText.value = ''
        }
    }
    const resetFile = () => {
        inputFile.value = null
        fileOK.value = null
        errorFileText.value = ''
    }
   
</script>
 
<template>
        <div class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
        <div class="bg-white w-[1000px] m-auto mt-5 py-3 px-5 text-left rounded-xl">
            <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
                Edit Event
                <button class="float-right" @click="$emit('hideEditMode'), resetFile()"><IconCancel class="w-5 h-5"/></button>
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
                                    <span v-show="errorFileText !=='' " class="text-red-500">&#9888; {{errorFileText}}</span>
                                <input type="file" id="eFile" name="eFile" @change="newFile" class="text-white block"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="text-right mb-3">
                <button @click="$emit('hideEditMode'), resetFile()" class="bg-red-100 text-red-500 py-1.5 px-4 rounded-full 
                           hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                &ensp;
                <button @click="$emit('save',editingEvent, fileOK), resetFile()" :disabled="checkDate"
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
   
</style>