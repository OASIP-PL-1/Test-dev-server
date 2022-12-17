<script setup>
import {useRouter} from 'vue-router'
const myRouter = useRouter()
// const closeModal = () => myRouter.go(-1)
const gotoHome = () => myRouter.push({name: 'Home'})
</script>
     
<template>
    <div class="text-center mt-36">
      <img class="max-h-48 inline-block mb-4" src="../../assets/403.png" alt="error"/>
      <div class="text-[50px]">Access Denied</div>
      <div class="mb-8">You don’t have access to this page</div>
      <button @click=gotoHome() class="bg-[#5C5CFF] text-white font-semibold py-2 px-5 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300">Back to Home</button>
    </div>
</template>
     
<style scoped>
</style>