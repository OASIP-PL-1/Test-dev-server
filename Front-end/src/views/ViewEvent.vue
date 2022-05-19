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
    // const res = await fetch('http://localhost:8080/api/events')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`)
      .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    events.value = await res.json()
    loading.value = false
  }

  const eventCategories = ref()
  const getEventCategoryName = async () => {
    loading.value = true
    message.value = "loading..."
    // const res = await fetch('http://localhost:8080/api/eventCategories')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories/name`)
      .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    eventCategories.value = await res.json()
    loading.value = false
    filterMode.value = ''
  }

  onMounted(async () => {
      await getEvents()
      await getEventCategoryName()
  })

  const filterMode = ref('')

// --- Filter ---
  const getPastEvent = async () => {
    // const res = await fetch('http://localhost:8080/api/events/past')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/past`)
    .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    events.value = await res.json()
    filterMode.value = 'past'
  }

  const getUpcomingEvent = async () => {
    // const res = await fetch('http://localhost:8080/api/events/upcoming')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/upcoming`)
    .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    events.value = await res.json()
    filterMode.value = 'upcoming'
  }

  const getEventByDate = async (date) => {
    console.log(date)
    // const res = await fetch('http://localhost:8080/api/events/upcoming')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/date/${date}`)
    .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
    events.value = await res.json()
    filterMode.value = 'date'
  }

  const getEventByCategory = async (id) => {
    if(id > 0){
    // const res = await fetch('http://localhost:8080/api/events/upcoming')
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/category/${id}`)
    .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
      events.value = await res.json()
      filterMode.value = 'category'
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