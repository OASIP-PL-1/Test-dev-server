<script setup>
  import ShowListEvent from '../components/ShowListEvent.vue'
  import Filter from '../components/Filter.vue'

  import {ref, onMounted} from 'vue'

  const events = ref()
  const loading =ref()
  const message = ref()

  const getEvents = async () => {
    loading.value = true
    message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`)
      .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log('GET List All Event Fail')
    });
    events.value = await res.json()
    loading.value = false
    if(res.status==200){
      console.log(`GET List All Event OK`)
      console.log(res.status)
    }
  }

  const eventCategories = ref()
  const getEventCategoryName = async () => {
    loading.value = true
    message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories/name`)
      .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log('GET List All CategoryName Fail')
    });
    eventCategories.value = await res.json()
    loading.value = false
    filterMode.value = ''
    if(res.status==200){
      console.log(`GET List All CategoryName OK`)
      console.log(res.status)
    }
  }

  onMounted(async () => {
      await getEvents()
      await getEventCategoryName()
  })

  const filterMode = ref('')

// --- Filter ---
  const getPastEvent = async () => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/past`)
    .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log(`GET Filter Mode : Past Fail`)
    });
    events.value = await res.json()
    filterMode.value = 'past'
    if(res.status==200){
      console.log(`Filter Mode : ${filterMode.value}`)
      console.log(res.status)
    }
  }

  const getUpcomingEvent = async () => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/upcoming`)
    .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log(`GET Filter Mode : Upcoming Fail`)
    });
    events.value = await res.json()
    filterMode.value = 'upcoming'
    console.log(res.status)
    if(res.status==200){
      console.log(`Filter Mode : ${filterMode.value}`)
      console.log(res.status)
    }
  }

  const getEventByDate = async (date) => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/date/${date}`)
    .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log(`GET Filter Mode : Date Fail`)
    });
    events.value = await res.json()
    filterMode.value = 'date'
    console.log(res.status)
    if(res.status==200){
      console.log(`Filter Mode : ${filterMode.value} = ${date}`)
      console.log(res.status)
    }
  }

  const getEventByCategory = async (id) => {
    if(id > 0){
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/category/${id}`)
    .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log(`GET Filter Mode : Category Fail`)
    });
      events.value = await res.json()
      filterMode.value = 'category'
      if(res.status==200){
        console.log(`Filter Mode : ${filterMode.value} = ${(eventCategories.value.find((category)=> id === category.id)).categoryName}`)
        console.log(res.status)
      }
    }else{
      getEvents()
    }
  }

</script>
 
<template>
  <div v-if="loading" class="subText">{{message}}</div>
  <div v-else>
      <table>
        <tr>
          <td style="width: 250px">
            <Filter :eventCategories="eventCategories"
            @past="getPastEvent"
            @upcoming="getUpcomingEvent"
            @selectDay="getEventByDate"
            @categoryName="getEventByCategory"
            @reset="getEvents"/>
          </td>
          <td>
            <ShowListEvent :events="events" :filterMode="filterMode"/>
          </td>
        </tr>
      </table>
  </div>
</template>
 
<style scoped>
  .subText{
    color: gray;
    text-align: center;
  }
  .centerText{
    text-align: center;
  }
  table {
    margin-left: auto;
    margin-right: auto;
    -o-object-fit: cover;
    object-fit: cover;
  }
  td {
    padding: 0px 20px;
    width: auto;
    -o-object-fit: cover;
    object-fit: cover;
  }
  input .text {
    height: 20px;
    width: 200px;
    padding: 4px;
  }
  select {
    height: 32px;
    width: 200px;
    padding: 4px;
    margin: 8px 0px;
  }
</style>