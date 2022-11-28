<script setup>
import IconMore from './icons/iconMore.vue'
import {useDatetimeFormat} from '../state/datetimeFormat.js'
import {useRouter} from 'vue-router'
const datetimeFormat = useDatetimeFormat()
  defineProps({
      events:{
          type: Object,
          require: true
      },
      filterMode:{
          type: String,
          require: true
      }
  })

const myRouter = useRouter()
const goToLoginUser = (eventId) => myRouter.push({name: 'ThisEvent',params:{eventId:eventId}})

  
</script>
 
<template>
  <!-- <div class="showEvent">
        <h2>List All Event</h2>
        <div class="grid-container">
          <div v-if="events == 0" class="centerText">
            <span v-if="filterMode === ''" class="emptyText"> --- No Scheduled Events --- </span>
            <span v-else-if="filterMode === 'past'" class="emptyText"> --- No Past Events --- </span>
            <span v-else-if="filterMode === 'upcoming'" class="emptyText"> --- No On-going or upcoming Events --- </span>
            <span v-else-if="filterMode === 'date'" class="emptyText"> --- No Scheduled Events for This Date --- </span>
            <span v-else-if="filterMode === 'category'" class="emptyText"> --- No Scheduled Events for This Category --- </span>
          </div>
          <div v-else v-for="(event,index) in events" :key="index" class="grid-item">
            <router-link :to="{ name: 'ThisEvent', params:{eventId:event.id}}">
              <div>
                  <strong>Booking name : </strong>
                  <h4 v-if="event.bookingName.length > 50">{{event.bookingName.substring(0,50)}} ...</h4>
                  <h4 v-else>{{event.bookingName}}</h4>
              <hr> 
                   <b>Date :</b> {{ datetimeFormat.showDate(new Date(event.startTime)) }}
              <br>
                  <b>Start Time :</b> {{ datetimeFormat.showTime(new Date(event.startTime))}}
              <br>
                  <strong>Category :</strong> 
              <br>
                  <div class="category"><b>{{event.categoryName}}</b>  &emsp;({{event.duration}} min.)</div>
              </div>
            </router-link>
          </div>
        </div>
  </div> -->

  <div class="w-full h-full">
    <div v-if="events == 0" class="text-red-600 w-full text-center">
            <div v-if="filterMode === ''"> --- No Scheduled Events --- </div>
            <div v-else-if="filterMode === 'past'"> --- No Past Events --- </div>
            <div v-else-if="filterMode === 'upcoming'"> --- No On-going or upcoming Events --- </div>
            <div v-else-if="filterMode === 'date'"> --- No Scheduled Events for This Date --- </div>
            <div v-else-if="filterMode === 'category'"> --- No Scheduled Events for This Category --- </div>
    </div>
    <div v-else>
      <div class="bg-[#3333A3] flex flex-row w-full py-5 rounded-t-xl
                        text-white text-lg font-semibold ">
          <div class="basis-10"></div>
          <div class="basis-[600px] px-5">Booking Name</div>
          <div class="basis-40 px-2">Date</div>
          <div class="basis-20 px-2">Time</div>
          <div class="basis-72 px-2">Category</div>
          <div class="basis-7 px-2"></div>
      </div>
      <div class="object-cover">
        <div class="overflow-y-scroll overflow-x-hidden object-cover h-80" id="list-event">      
          <div v-for="(event,index) in events" :key="index" @click="goToLoginUser(event.id)" 
            class="px-2 py-5 hover:bg-[#FFCB4C] flex flex-row w-full odd:bg-white even:bg-[#ECECFE]">
            <div class="basis-10 px-2 text-gray-500 text-center">#{{index+1}}</div>
            <div class="basis-[600px] px-5">{{event.bookingName}}</div>
            <div class="basis-40 px-2">{{ datetimeFormat.showDateNoDay(new Date(event.startTime)) }}</div>
            <div class="basis-20 px-2">{{ datetimeFormat.showTime(new Date(event.startTime))}}</div>
            <div class="basis-72 px-2">{{event.categoryName}}</div>
            <button class="basis-7 px-2 text-gray-500"><IconMore class="w-5 h-5 mx-2 inline align-top"/></button>
          </div>
        </div>
        <!-- <div class="bg-[#3333A3] w-full h-7 rounded-b-xl"></div> -->
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
  /* #list-event div:nth-child(even){background-color: white;}
  #list-event div:only-child(odd){background-color: #b1b1d4;}
   */
  /* #listAll tr:nth-child(even){background-color: #ECECFE;}
  #listAll tr:nth-child(odd){background-color: #b5b5f7} */
</style>