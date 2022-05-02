<script setup>
  defineProps({
      events:{
          type: Object,
          require: true
      }
  })
</script>
 
<template>
  <div v-if="events.length === 0" class="centerText">
    <span class="emptyText"> --- No Scheduled Events -- </span>
  </div>
  <div v-else>
    <h2>List All Event</h2>
    <div class="grid-container">
      <div v-for="(event,index) in events" :key="index" class="grid-item">
        <router-link :to="{ name: 'ThisEvent', params:{eventId:event.id}}">
          <div>
            <h4>Booking Name : {{event.bookingName}}</h4>
              <hr>
            <b>Category Name :</b> {{event.categoryName}}
              <br>
            <b>Date :</b> {{new Date(event.startTime).toLocaleDateString('th-TH')}}
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
 .emptyText{
   color: red;
 }
 .grid-container {
    display: grid;
    grid-template-columns: auto auto auto;
    background-color: #FAD2E1;
    padding: 15px;
    column-gap: 15px;
    row-gap: 15px;
    border-radius: 10px;
  }
  .grid-item {
    background-color: rgba(255, 255, 255, 0.8);
    padding: 0px 20px 20px 20px;
    border-radius: 10px;
  }
  a {
    color: #000000;
    text-decoration: none;
  }
  .router-link-active {
    color: #f3b2cb;
  }
</style>