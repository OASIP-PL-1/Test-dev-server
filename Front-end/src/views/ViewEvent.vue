<script setup>
  import ShowListEvent from '../components/ShowListEvent.vue'
  import {ref, onMounted} from 'vue'

  const events = ref([])

  const getEvents = async () => {
    // const res = await fetch('http://localhost:8080/api/events')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`)
    events.value = await res.json()
  }

  onMounted(async () => {
      await getEvents()
      console.log(events.value)
  })

</script>
 
<template>
  <ShowListEvent :events="events"/>
</template>
 
<style>
 
</style>