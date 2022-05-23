<script setup>
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
  
// --- show Date --- 
  const days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
  const months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sep','Oct','Nov','Dec']
  const showDate = (givenDate) => {
      // console.log(givenDate)
      const day = days[givenDate.getDay()]
      const date = givenDate.getDate()
      const month = months[givenDate.getMonth()]
      const year = givenDate.getFullYear()
      return day + ' ' + date + ' ' + month + ' ' + year
  }
// --- show start time ---
const showTime = (givenDate) => {
    return givenDate.toLocaleTimeString('th-TH').substring(0,5)
}
</script>
 
<template>
  <div class="showEvent">
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
                  <b>Date :</b> {{ showDate(new Date(event.startTime)) }}
              <br>
                  <b>Start Time :</b> {{ showTime(new Date(event.startTime))}}
              <br>
                  <strong>Category :</strong> 
              <br>
                  <div class="category"><b>{{event.categoryName}}</b>  &emsp;({{event.duration}} min.)</div>
              </div>
            </router-link>
          </div>
        </div>
  </div>
</template>
 
<style scoped>
  .category{
    background-color: antiquewhite;
    border-radius: 10px;
    margin-top: 5px;
    padding: 0 4px;
  }
  h2 {
    color: #FFCB4C;
  }
  h4{
    padding-top: 0;
    margin: 0 0 1em 0;
  }
  strong {
    font-size: smaller;
    color: #3333A3;
  }
 .showEvent {
    background-color: #3333A3;
    border-radius: 30px;
    padding: 4px 24px 24px;
    box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
    -o-object-fit: cover;
    object-fit: cover;
 }
 .grid-container {
    display: grid;
    border-radius: 20px;
    grid-template-columns: 340px 340px 340px;
    background-color: #9F9FF9;
    padding: 15px;
    column-gap: 20px;
    row-gap: 20px;
    overflow-y: scroll;
    overflow-x: hidden;
    height: 500px;
  }
  .grid-item {
    background-color: rgba(255, 255, 255, 0.8);
    padding: 20px 20px 20px 20px;
    border-radius: 20px;
    transition: background-color 1s, transform .5s;
    transition-duration: box-shadow 2s;
    width: 300px;
    height: 175px;
  }
  .grid-item:hover{
    background-color: rgba(255, 255, 255, 1);
    transform: scale(1.1);
    box-shadow: 40px rgba(0, 0, 0, 0.5);
  }
  a {
    color: #000000;
    text-decoration: none;
  }
  ::-webkit-scrollbar {
    width: 10px;
    background-color: #FFCB4C;
    border-radius: 5px;
  }
  ::-webkit-scrollbar-thumb {
    background: #FFA21A; 
    border-radius: 10px;
  }::-webkit-scrollbar-thumb:hover {
    background: #5C5CFF; 
  }
</style>