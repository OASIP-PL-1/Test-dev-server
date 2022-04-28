<script setup>
    import {ref, onBeforeMount} from 'vue'
    import {useRoute, useRouter} from 'vue-router'

    const {params} = useRoute()
    const thisEvent = ref({})
    const showDetail = ref({})
    const getThisEvent = async () => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}`)
        thisEvent.value = await res.json()
        showDetail.value = checkStatus(res.status)
    }
    const checkStatus = (resStatus) => {return resStatus === 200 ? true : false}

    onBeforeMount(async () => {
      await getThisEvent()
    })

    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)

    const addMinutes = (date,duration) => {
        const changeDate = date
        changeDate.setMinutes(changeDate.getMinutes()+duration)
        // console.log(changeDate)
        return changeDate
    }

    

</script>
 
 
<template>
    <button @click="goBack">Back</button>
    <div v-show="showDetail" class="box">
        <h3>Booking Name : {{thisEvent.bookingName}}</h3>
            <b>bookingEmail :</b> {{thisEvent.bookingEmail}}
        <hr>
            <b>Category Name :</b> {{thisEvent.categoryName}}
        <br>
            <b>Date :</b> {{new Date(thisEvent.startTime).toLocaleDateString('th-TH')}}
        <br>
            <b>Start Time :</b> {{new Date(thisEvent.startTime).toLocaleTimeString('th-TH')}}
        <br>
            <b>Duration :</b> {{thisEvent.duration}} min  
        <br>
            <b>Finished time : </b> {{ addMinutes(new Date(thisEvent.startTime),thisEvent.duration).toLocaleTimeString('th-TH') }}
        <br>
        <div v-if="thisEvent.notes !== null">
            <b>Notes :</b> {{thisEvent.notes}}
        </div>
        <div v-else>
            <b>Notes :</b> - 
        </div>
    </div>
    <div v-show="!showDetail" class="NotFoundText">
        -- ไม่พบข้อมูล --
    </div>
    <div class="button-right">
        <!-- <button @click="">Edit</button>
        <button @click="">Delete</button>      -->
    </div>
</template>
 
<style scoped>
    b {
        color: #000000;
    }
    .box {
        background-color: #DBE7E4;
        padding: 0px 20px 20px 20px;
        border-radius: 10px;
    }
    .NotFoundText{
        color: gray;
        text-align: center;
    }
    .button-right {
        float: right;
    }
</style>