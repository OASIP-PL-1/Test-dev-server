<script setup>
    import {useRouter} from 'vue-router'
    const myRouter = useRouter()
    const goToAddNewEvent = () => myRouter.push({name: 'AddNewEvent'})
</script>
 
<template>
    <div style="margin-top: 10em;">
    <img class="object-cover w-6/12" src="../assets/phone.png" alt="phone" style="display:inline">
 
    <div class="rightall" style="display:inline">
        <img src="../assets/head.png" alt="head" style="max-width: 500px; min-width: 300px; width: 70%;">
        <h4 style="padding-left: 10%; padding-right: 10%;">Online Appointment Scheduling System for Integrated Project Clinics (OASIP) will make a reservation for your event easily</h4>
        <h4 style="padding-left: 20%;">
            <ul style="text-align: left;">
                <li>Booking : create / cancel events</li>
                <li>Checking reservation</li>
                <li>Categories's detail</li>
            </ul>    
        </h4>
        <h4 style="padding-left: 10%; padding-right: 10%;">This wil give user convenience for consulting with their Product Owner or the teachers</h4>
            <button @click="goToAddNewEvent" class="button-18" role="button" style="font-size:1.5em">Book Now</button>
    </div>
    </div>
</template>
 
<style scoped>
    .object-cover {
        -o-object-fit: cover;
        object-fit: cover;
        margin: -6% 1% 0% 1%;
        min-width: 250px;
    }
    .w-6\/12 {
        width: 40%;
    }
    .rightall {
        float: right;
        margin: 5% 1% 0% 0%;
        text-align: center;
        width: 50%;
        -o-object-fit: cover;
        object-fit: cover;
    } 
</style>