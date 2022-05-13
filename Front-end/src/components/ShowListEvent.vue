<script setup>
  defineProps({
      events:{
          type: Object,
          require: true
      }
  })
  
// --- show Date --- 
  const days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
  const months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sep','Oct','Nov','Dec']
  const a = new Date('2022-05-04')
  const getDate = (givenDate) => {
      // console.log(givenDate)
      const day = days[givenDate.getDay()]
      const date = givenDate.getDate()
      const month = months[givenDate.getMonth()]
      const year = givenDate.getFullYear()
      return day + ' ' + date + ' ' + month + ' ' + year
  }

</script>
 
<template>
  <div class="showEvent">
        <h2>List All Event</h2>
        <div class="grid-container">
          <div v-if="events == 0" class="centerText">
            <span class="emptyText"> --- No Scheduled Events --- </span>
          </div>
          <div v-else v-for="(event,index) in events" :key="index" class="grid-item">
            <router-link :to="{ name: 'ThisEvent', params:{eventId:event.id}}">
              <div>
                  <h4><strong>Booking name: </strong>{{event.bookingName}}</h4>
              <hr>
                  <b>Category Name :</b> {{event.categoryName}}
              <br>
                  <b>Date :</b> {{ getDate(new Date(event.startTime)) }}
              <br>
                  <b>Start Time :</b> {{new Date(event.startTime).toLocaleTimeString('th-TH')}}
              <br>
                  <b>Duration :</b> {{event.duration}} (mins)
              </div>
            </router-link>
          </div>
        </div>
  </div>
</template>
 
<style scoped>
  h2 {
    color: #FFCB4C;
  }
 .showEvent{
   background-color: #3333A3;
   border-radius: 20px;
   padding: 4px 24px 24px;
   box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
   min-width: 1000px;
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
    height: 440px;
  }
  .grid-item {
    background-color: rgba(255, 255, 255, 0.8);
    padding: 4px 20px 20px 20px;
    border-radius: 20px;
    transition: background-color 1s, transform .5s;
    transition-duration: box-shadow 2s;
    width: 300px;
    max-height: 250px;
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