<script setup>
    import {ref, computed, onUpdated, onMounted} from 'vue'
    import {useRouter} from 'vue-router'
    import {useSignIn} from './state/signIn.js'

    //-- insert icons --
    import IconBooking from './components/icons/iconBooking.vue'
    import IconHome from './components/icons/iconHome.vue'
    import IconEvent from './components/icons/iconEvent.vue'
    import IconCategory from './components/icons/iconCategory.vue'
    import IconAccount from './components/icons/iconAccount.vue'
    import IconAbout from './components/icons/iconAbout.vue'
    import IconLogout from './components/icons/iconLogout.vue'
    import IconLogin from './components/icons/icoonLogin.vue'
    import IconKeyCheck from './components/icons/iconKeyCheck.vue'

    
    const signIn = useSignIn()
    signIn.checkLogin()

    const myRouter = useRouter()
    const gotoHome = () => myRouter.push({name: 'Home'})

    const menuToggle = () => {
        const toggleMenu = document.getElementById("toggle")
        toggleMenu.classList.toggle('showToggle')
    }

    // ลองซ่อน sidebar
    const showMenuSide = (namePath) =>{
        console.log(namePath)
        namePath==='/log-in'? closeMenu() : openMenu()
        // myRouter.currentRoute._rawValue.fullPath==='/log-in'? closeMenu() : openMenu()
    }

    const openMenu = ()=>{
        const sideMenu = document.getElementById('side-menu');
        sideMenu.classList.remove('hidden');
    }
    const closeMenu = ()=>{
        const sideMenu = document.getElementById('side-menu');
        sideMenu.classList.add('hidden');
    }
    onMounted(()=>{ showMenuSide(myRouter.currentRoute._rawValue.fullPath)})
</script>
 
<template>
<div>
    <nav class="flex items-center justify-between bg-[#3333A3] w-full px-2 py-3">
    <div class="flex-auto mx-5">
        <button @click=gotoHome()>
            <img class="w-[250px] inline"
                src="./assets/logo.png" alt="logo"/>
            <span class="text-[#ffcb4c] text-[10px] align-text-top">
                v2.3.35</span>
        </button>
    </div>
    <div class="flex-auto mx-5 text-right"
        v-if="signIn.statusLogin===true">
        <span class="text-white font-semibold px-4">{{signIn.user.name}}</span>
            <button @click="menuToggle()">
                <img class="w-[40px] inline" src="../public/humans/human1.png" alt="human" />
            </button>
            <!-- Toggle -->
            <span id="toggle" 
                    class="absolute right-[20px] pt-2 pb-2 bg-white w-32 box-border drop-shadow-md rounded-lg text-gray-700 
                         transition duration-0 opacity-0 invisible
                         before:absolute before:top-[-10px] before:right-[18px] before:bg-white before:w-[19px] before:h-[19px] before:rotate-45">
                <div class="flex flex-col">
                    <span @click="signIn.Logout()" class="text-center py-1 inline
                                focus:bg-gray-300 active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline hover:bg-[#ECECFE]">
                        <IconLogout class="w-[25px] h-[25px] mr-2 inline align-top"/>Log Out
                    </span>
                </div>
            </span>
    </div>
    <div v-else class="mx-5" >
        <router-link :to="{name:'LogIn'}"  @click="showMenuSide(myRouter.currentRoute._rawValue.fullPath)" id="non-active" class="text-white active:text-[#FFCB4C] hover:text-[#FFCB4C] ">
            <IconLogin class="w-[25px] h-[25px] mr-2 ml-0 inline align-top"/>Log In
        </router-link>
    </div>


    </nav>
    <div class="flex flex-row h-screen">
        <!-- Sidebar -->
        <div id="side-menu" class="overflow-hidden bg-[#ECECFE] min-w-[150px] w-[180px] text-gray-700 font-semibold grid content-between">
            <div class="flex flex-col">
            <router-link :to="{name:'Home'}" class="px-7 py-2 focus:bg-gray-300 active:bg-gray-400 active:text-white hover:bg-[#FFCB4C]" >
                <IconHome class="w-[20px] h-[20px] mx-2 inline align-top"/>Home</router-link>
            <router-link :to="{name:'CreateEvent'}" class="px-7 py-2 focus:bg-gray-300 active:bg-gray-400 active:text-white hover:bg-[#FFCB4C]" >
                <IconBooking class="w-[20px] h-[20px] mx-2 inline align-top"/>Book</router-link>
            <router-link :to="{name:'ViewEvent'}" class="px-7 py-2 focus:bg-gray-300 active:bg-gray-400 active:text-white hover:bg-[#FFCB4C]">
                <IconEvent class="w-[20px] h-[20px] mx-2 inline align-top"/>Event</router-link>
            <router-link :to="{name:'ViewEventCategory'}" class="px-7 py-2 focus:bg-gray-300 active:bg-gray-400 active:text-white hover:bg-[#FFCB4C]">
                <IconCategory class="w-[20px] h-[20px] mx-2 inline align-top"/>Category</router-link>

            <router-link :to="{name:'ViewUser'}" v-show="signIn.statusLogin && signIn.user.role === 'admin'"
                class="px-7 py-2 focus:bg-gray-300 active:bg-gray-400 active:text-white hover:bg-[#FFCB4C]">
                <IconAccount class="w-[20px] h-[20px] mx-2 inline align-top"/>User</router-link>
            <router-link :to="{name:'LogIn'}" v-show="signIn.statusLogin && signIn.user.role === 'admin'"
                class="px-7 py-2 focus:bg-gray-300 active:bg-gray-400 active:text-white hover:bg-[#FFCB4C]">
                <IconKeyCheck class="w-[20px] h-[20px] mx-2 inline align-top"/>Check</router-link>
            </div>

            <div class="flex flex-col pb-14 my-2">
            <router-link :to="{name:'About'}" class="px-7 py-2 focus:bg-gray-300 active:bg-gray-400 active:text-white hover:bg-[#FFCB4C]">
                <IconAbout class="w-[20px] h-[20px] mx-2 inline align-top"/>About us</router-link>
            </div>
        </div>
        <div class="w-full mx-0 overflow-auto mb-10">
            <router-view></router-view>
        </div>
    </div>
</div>
</template>
 
<style scoped>
* {
    margin: 0;
    padding: 0;
}
.router-link-active{
    background-color: rgb(209 213 219);
}
#non-active.router-link-active{
    background-color: inherit;
    color: #FFA21A;
    text-decoration: underline;
}
 .showToggle {
    top: 70px;
    visibility: visible;
    opacity: 1;
  }
  
</style>