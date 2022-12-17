<script setup>
  import {useRouter} from 'vue-router'

  const myRouter = useRouter()
  const gotoLogin = () => myRouter.push({ name: 'LogIn'})
</script>
   
<template>
    <div class="text-center mt-36">
      <img class="max-h-48 inline-block mb-4" src="../../assets/401.png" alt="error"/>
      <div class="text-[50px]">Authorization Required</div>
      <div class="mb-8">This page is allowed for registered users only</div>
      <button @click=gotoLogin() class="bg-[#5C5CFF] text-white font-semibold py-2 px-5 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300">Go to Log In</button>
    </div>
</template>
   
<style scoped>
</style>