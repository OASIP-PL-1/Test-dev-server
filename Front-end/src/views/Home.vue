<script setup>

</script>
 
<template>
    <!-- <h1>Appointment</h1>
    <h1>Booking</h1> -->
    <div class="rightall">
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
        <button @click="bookNow" class="button-18" role="button" style="font-size:1.5em">Book Now</button>
    </div>
</template>
 
<style scoped>
    .rightall {
        /* padding: 2% 1% 5% 5%; */
        float: right;
        margin: 5% 1% 0% 0%;
        text-align: center;
        width: 50%;
        /* margin-right: 4em; */
    } 
</style>