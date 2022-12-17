<script setup>
    import {useRouter} from 'vue-router'
    import { useSignIn } from '../state/signIn';

    const signIn = useSignIn()
    const myRouter = useRouter()
    const goToLogIn = () => myRouter.push({name: 'LogIn'})
    const gotoCreateEvent = () => myRouter.push({name: 'CreateEvent'})
</script>
 
<template>
    <div class="flex flex-row h-full pb-10">
        <div class="w-1/2 ml-10 mr-0 mb-10 p-2">
            <img class="object-cover h-full" src="../assets/phone.png" alt="phone"/>
        </div>
        <div class="w-1/2 place-content-center my-auto pb-5 ml-0 mr-10">
            <img class="w-4/5 mx-auto"
            src="../assets/head.png" alt="head" />
            <div class="w-full text-sm">
                <h4 class="px-10">Online Appointment Scheduling System for Integrated Project Clinics (OASIP) will make a reservation for your event easily</h4>
                <p>
                    <ul class="list-disc pl-32">
                        <li>Booking : create / cancel events</li>
                        <li>Checking reservation</li>
                        <li>Categories's detail</li>
                    </ul>    
                </p>
                <h4 class="px-10">This wil give user convenience for consulting with their Product Owner or the teachers</h4>
            </div>
            <div class="w-full my-5 text-center">
                <button @click="goToLogIn()" v-if="signIn.statusLogin === false"
                        class="bg-[#5C5CFF] text-white text-[18px] font-bold py-2 px-5 rounded-full 
                               hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300" 
                        role="button">Book Now</button>
                <button @click="gotoCreateEvent()" v-else
                        class="bg-[#5C5CFF] text-white text-[18px] font-bold py-2 px-5 rounded-full 
                               hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300" 
                        role="button">Book Now</button>
            </div>
        </div>
    </div>
</template>
 
<style scoped>

</style>