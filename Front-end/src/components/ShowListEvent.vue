<script setup>
import IconMore from './icons/iconMore.vue'
import {useDatetimeFormat} from '../state/datetimeFormat.js'
import {useRouter} from 'vue-router'
const datetimeFormat = useDatetimeFormat()
  defineProps({
      events:{type: Object,require: true},
      filterMode:{type: String, require: true}
  })

const myRouter = useRouter()
const goToLoginUser = (eventId) => myRouter.push({name: 'ThisEvent',params:{eventId:eventId}})

  
</script>
 
<template>
  <div class="w-full h-full mt-5">
    <div v-if="events == 0" class="text-red-600 w-full text-center">
            <div v-if="filterMode === ''"> --- No Scheduled Events --- </div>
            <div v-else-if="filterMode === 'past'"> --- No Past Events --- </div>
            <div v-else-if="filterMode === 'upcoming'"> --- No On-going or upcoming Events --- </div>
            <div v-else-if="filterMode === 'date'"> --- No Scheduled Events for This Date --- </div>
            <div v-else-if="filterMode === 'category'"> --- No Scheduled Events for This Category --- </div>
    </div>
    <div v-else>
      <div class="bg-[#3333A3] flex flex-row w-full py-5 pl-2 pr-4 rounded-t-lg 
                        text-white text-lg font-semibold text-center">
          <div class="basis-1/12"></div>
          <div class="basis-4/12">Booking Name</div>
          <div class="basis-2/12">Date</div>
          <div class="basis-1/12">Time</div>
          <div class="basis-3/12">Category</div>
          <div class="basis-1/12"></div>
      </div>
      <div class="object-cover">
        <div class="overflow-y-scroll overflow-x-hidden object-cover h-96" id="list-event">      
          <div v-for="(event,index) in events" :key="index" @click="goToLoginUser(event.id)" 
                class="px-2 py-5 flex flex-row w-full odd:bg-white hover:bg-[#FFD880] even:bg-[#ECECFE]">
            <div class="basis-1/12 text-gray-500 text-center">#{{index+1}}</div>
            <div class="basis-4/12">{{event.bookingName}}</div>
            <div class="basis-2/12 text-center">{{ datetimeFormat.showDateNoDay(new Date(event.startTime)) }}</div>
            <div class="basis-1/12 text-center">{{ datetimeFormat.showTime(new Date(event.startTime))}}</div>
            <div class="basis-3/12 pl-5">{{event.categoryName}}</div>
            <div class="basis-1/12 text-gray-500 text-center"><button><IconMore class="w-5 h-5 inline align-top"/></button></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
 
<style scoped>
  ::-webkit-scrollbar {
    width: 10px;
    background-color: #d4d4d4;
    border-radius: 5px;
  }
  ::-webkit-scrollbar-thumb {
    background: #868686; 
    border-radius: 10px;
  }::-webkit-scrollbar-thumb:hover {
    background: #5C5CFF; 
  }
</style>