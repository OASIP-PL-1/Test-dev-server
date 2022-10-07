<script setup>
  import ShowListEvent from '../components/ShowListEvent.vue'
  import Filter from '../components/Filter.vue'

  import {ref, onMounted} from 'vue'
  import { useSignIn } from '../state/signIn';

  const signIn = useSignIn()

  const events = ref()
  const loading =ref()
  const message = ref()

  const getEvents = async () => {
    loading.value = true
    message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
      )
      .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log('GET List All Event Fail')
    });
    console.log(res.status)
    if(res.status==200){
      events.value = await res.json()
      loading.value = false
      console.log(`GET List All Event OK`)
      console.log(events.value)
      await getEventCategoryName()
    }else if(res.status===401){
      let errorText = await res.text()
      console.log(errorText)
      if(errorText==="Token is expired."){
        await signIn.sendRefreshToken()
      }else{
        message.value = "Please login"
      }
      console.log('Please login')
    }else if(res.status===403){
      message.value = 'Unauthorized access'
      console.log('Unauthorized access')
    }
  }

  const eventCategories = ref()
  const getEventCategoryName = async () => {
    loading.value = true
    message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories/name`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }).catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log('GET List All CategoryName Fail')
    });
    if(res.status==200){
      eventCategories.value = await res.json()
      loading.value = false
      filterMode.value = ''
      console.log(`GET List All CategoryName OK`)
      console.log(res.status)
    }else if(res.status===401){
      console.log('Please login')
      message.value = "Please login"
    }else if(res.status===403){
      console.log('Unauthorized access')
    }
  }

  onMounted(async () => {
      await getEvents()
      // await getEventCategoryName()
  })

  const filterMode = ref('')

// --- Filter ---
  const getPastEvent = async () => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/past`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
      )
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
    }else if(res.status===401){
      console.log('Please login')
    }else if(res.status===403){
      console.log('Unauthorized access')
    }
  }

  const getUpcomingEvent = async () => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/upcoming`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      })
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
    }else if(res.status===401){
      console.log('Please login')
    }else if(res.status===403){
      console.log('Unauthorized access')
    }
  }

  const getEventByDate = async (date) => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/date/${date}`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }).catch((error)=> {
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
    }else if(res.status===401){
      console.log('Please login')
    }else if(res.status===403){
      console.log('Unauthorized access')    
    }
  }

  const getEventByCategory = async (id) => {
    if(id > 0){
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/category/${id}`  
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
      )
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
      }else if(res.status===401){
        console.log('Please login')
      }else if(res.status===403){
        console.log('Unauthorized access')
      }
    }else{
      getEvents()
    }
  }
</script>
 
<template>
  <div style="margin-top: 10em;">
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
            <td style="padding-left: 20px">
              <ShowListEvent :events="events" :filterMode="filterMode"/>
            </td>
          </tr>
        </table>
    </div>
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
    /* padding: 0 0 0 20px; */
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