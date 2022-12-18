<script setup>
import {useRouter} from 'vue-router'
const myRouter = useRouter()
const gotoHome = () => myRouter.push({name: 'Home'})
</script>
 
<template>
  <div class="text-center mt-36">
    <img class="max-h-48 inline-block mb-4" src="../../assets/404.png" alt="error"/>
    <div class="text-[50px]">Page Not Found</div>
    <div class="mb-8">We can not find the page you are looking for</div>
    <button @click=gotoHome() class="bg-[#5C5CFF] text-white font-semibold py-2 px-5 rounded-full 
                           hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300">Back to Home</button>
  </div>
</template>
 
<style scoped>
</style>