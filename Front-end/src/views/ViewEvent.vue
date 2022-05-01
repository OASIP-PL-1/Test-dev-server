<script setup>
  import ShowListEvent from '../components/ShowListEvent.vue'
  import {ref, onMounted} from 'vue'

  const events = ref({})
  const loading =ref()
  const message = ref()

  const getEvents = async () => {
    loading.value = true
    message.value = "loading..."
    // const res = await fetch('http://localhost:8080/api/events')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`)
      .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    events.value = await res.json()
    console.log(res.status)
    loading.value = false
  }

  onMounted(async () => {
      await getEvents()
      console.log(events.value)
  })

</script>
 
<template>
  <div v-if="loading" class="subText">{{message}}</div>
  <div v-else><ShowListEvent :events="events"/></div>
</template>
 
<style>
 .subText{
   color: gray;
   text-align: center;
 }
 .centerText{
   text-align: center;
 }
</style>