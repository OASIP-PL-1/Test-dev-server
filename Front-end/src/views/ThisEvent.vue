<script setup>
    import {ref, onBeforeMount} from 'vue'
    import {useRoute, useRouter} from 'vue-router'

    const {params} = useRoute()

// --- Get all list --- 
    const thisEvent = ref({})
    const showDetail = ref({})
    const loading =ref()
    const message = ref()

    const getThisEvent = async () => {
        loading.value = true
        message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}`)
    .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
        thisEvent.value = await res.json()
        console.log(res.status)
        if(res.status===200){
            showDetail.value = true
        }else if(res.status===404){
            showDetail.value = false
        }
        loading.value = false
    }

    onBeforeMount(async () => {
      await getThisEvent()
    })

// --- Go back --- 
    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    // const goNext = () => {
    //     myRouter.push({ name: 'ThisEvent', params:{eventId:params.eventId++}})
    //     getThisEvent()
    // }

// --- show Date --- 
    const days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
    const months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sep','Oct','Nov','Dec']
    const a = new Date('2022-05-04')
    const getDate = (givenDate) => {
        console.log(givenDate)
        const day = days[givenDate.getDay()]
        const date = givenDate.getDate()
        const month = months[givenDate.getMonth()]
        const year = givenDate.getFullYear()
        return day + ' ' + date + ' ' + month + ' ' + year
    }

// --- calculate Finished time --- 
    const addMinutes = (date,duration) => {
        const changeDate = date
        changeDate.setMinutes(changeDate.getMinutes()+duration)
        // console.log(changeDate)
        return changeDate
    }

// --- Edit---
    
// --- Delete---
    const removeEvent = async () => {
        let isExecuted = confirm(`Do you want to delete event :  ${params.eventId}`)
        if (isExecuted){
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}` , {
                method: 'DELETE'
            })
            if (res.status===200) {
                console.log('deleted successfully')
                goBack()
            } else console.log('error, cannot delete')
        }
    }

</script> 
 
<template>
    <div class="thisEvent">
        <button @click="goBack" class="button-18" role="button">Back</button>&ensp;
        <!-- <button @click="goNext()" class="button-18" role="button">Next</button> -->
        <div v-if="loading" class="subText" style="margin-top: 2em;">{{message}}</div>
        <div v-else-if="!showDetail" class="NotFoundText" style="margin-top: 2em;">
        -- Not Found Data --
        </div>
        <div v-else>
           
            <div class="box">
                <h3>Booking Name : {{thisEvent.bookingName}}</h3>   
                    <b>Email :</b> {{thisEvent.bookingEmail}}
                <hr>
                    <b>Category Name :</b> {{thisEvent.categoryName}}
                <br>
                    <b>Date :</b> {{ getDate(new Date(thisEvent.startTime))}}
                <br>
                    <b>Start Time :</b> {{new Date(thisEvent.startTime).toLocaleTimeString('th-TH')}}
                    
                <br>
                    <b>Duration :</b> {{thisEvent.duration}} min  
                <br>
                    <b>Finished time : </b> {{ addMinutes(new Date(thisEvent.startTime),thisEvent.duration).toLocaleTimeString('th-TH') }}
                <br>
                <div v-if="thisEvent.notes === null">
                    <b>Notes :</b> -
                </div>
                <div v-else>
                    <b>Notes :</b> {{thisEvent.notes}}
                </div>

            </div>
            <div class="button-right">
                <!-- <button @click="$emit('editEvent', thisEvent.bookingName)" class="button-18" role="button" >Edit</button> &ensp; -->
                <button @click="removeEvent()" class="button-18" role="button" >Delete</button>     
            </div>
        </div>
    </div>
</template>
 
<style scoped>
    h3 {
        color: #FFA21A;
    }
    b {
        color: #FFCB4C;
    }
    .box {
        background-color: #3333A3;
        padding: 1em 2em 3em 2em;
        border-radius: 10px;
        margin: 1em 2em;
        min-width: 600px;
        color: white;
    }
    .NotFoundText{
        color: gray;
        text-align: center;
    }
    .button-right {
        float: right;
        margin-right: 2em;
    }
    .thisEvent { 
        margin-left: 2em;
        margin-right: 2em;
        padding-left: 20px;
        padding-right: 20px;
    }
</style>