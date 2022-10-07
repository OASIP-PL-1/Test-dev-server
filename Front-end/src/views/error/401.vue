<script setup>
  import {useRouter} from 'vue-router'

  const myRouter = useRouter()
  const gotoLogin = () => myRouter.push({ name: 'LoginUser'})
</script>
   
<template>
    <div style="margin-top: 10em;">
      <div class="subText">Please <a @click="gotoLogin()" style="color: blue; text-decoration: underline;" >Login</a> </div>
    </div>
</template>
   
<style scoped>
</style>