<script setup>
  import ShowListEvent from '../components/ShowListEvent.vue'
  import Filter from '../components/Filter.vue'
  import IconLoading from '../components/icons/iconLoading.vue'

  import {ref, onMounted} from 'vue'
  import {useRouter} from 'vue-router'
  import { useSignIn } from '../state/signIn';

  const signIn = useSignIn()

  const myRouter = useRouter()
  const goToError401 = () => myRouter.push({name: 'Error401'}) 
  const goToError403 = () => myRouter.push({name: 'Error403'})
  const goToError500 = () => myRouter.push({name: 'Error500'})

  onMounted(async () => {
      await getEvents()
      // await getEventCategoryName()
  })
  
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
        // message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log('GET List All Event Fail')
        goToError500()
    });
    if(res.status==200){
      events.value = await res.json()
      loading.value = false
      await getEventCategoryName()
    }else if(res.status===401){
      let errorText = await res.text()
      if(errorText==="Token is expired."){
        await signIn.sendRefreshToken()
      }else{
        goToError401()
      }
    }else if(res.status===403){
      goToError403()
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
        // message.value = "Not Found Backend Server!!!"
        console.log(error)
        alert('GET List All CategoryName Fail')
        goToError500()
    });
    if(res.status==200){
      eventCategories.value = await res.json()
      loading.value = false
      filterMode.value = ''
    }else if(res.status===401){
      goToError401()
    }else if(res.status===403){
      goToError403()
    }
  }



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
        // message.value = "Not Found Backend Server!!!"
        console.log(error)
        alert('GET Filter Mode : Past Fail')
        goToError500()
    });
    events.value = await res.json()
    filterMode.value = 'past'
    if(res.status==200){
      console.log(`Filter Mode : ${filterMode.value}`)
    }else if(res.status===401){
      goToError401()
    }else if(res.status===403){
      goToError403()
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
        // message.value = "Not Found Backend Server!!!"
        console.log(error)
        alert(`GET Filter Mode : Upcoming Fail`)
        goToError500()
    });
    events.value = await res.json()
    filterMode.value = 'upcoming'
    if(res.status==200){
      console.log(`Filter Mode : ${filterMode.value}`)
    }else if(res.status===401){
      goToError401()
    }else if(res.status===403){
      goToError403()
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
        // message.value = "Not Found Backend Server!!!"
        console.log(error)
        alert(`GET Filter Mode : Date Fail`)
        goToError500()
    });

    events.value = await res.json()
    filterMode.value = 'date'
    if(res.status==200){
      console.log(`Filter Mode : ${filterMode.value} = ${date}`)
    }else if(res.status===401){
      goToError401()
    }else if(res.status===403){
      goToError403()
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
        // message.value = "Not Found Backend Server!!!"
        console.log(error)
        alert(`GET Filter Mode : Category Fail`)
        goToError500()
    });
      events.value = await res.json()
      filterMode.value = 'category'
      if(res.status==200){
        console.log(`Filter Mode : ${filterMode.value} = ${(eventCategories.value.find((category)=> id === category.id)).categoryName}`)
      }else if(res.status===401){
        goToError401()
      }else if(res.status===403){
        goToError403()
      }
    }else{
      getEvents()
    }
  }
</script>
 
<template>
    <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
    <div v-else class="flex flex-col m-10">
      <div class="w-auto">
        <Filter :eventCategories="eventCategories"
              :filterMode="filterMode"
              @past="getPastEvent"
              @upcoming="getUpcomingEvent"
              @selectDay="getEventByDate"
              @categoryName="getEventByCategory"
              @reset="getEvents"/>
        <ShowListEvent :events="events" :filterMode="filterMode"/>
      </div>
      <div>
      </div>
    </div>
</template>
 
<style scoped>
  
</style>