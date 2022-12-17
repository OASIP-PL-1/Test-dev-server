<script setup>
import {useRouter} from 'vue-router'
const myRouter = useRouter()
// const closeModal = () => myRouter.go(-1)
const gotoHome = () => myRouter.push({name: 'Home'})
</script>
 
<template>
    <!-- <div class="modal-mask">
    <div class="modal-wrapper">
      <div class="modal-container">
        <div class="modal-header"><h3>404 Error : Page Not Found</h3></div>
        <div class="modal-body">Please try again!</div>
        <div class="modal-button">
          <button class="button-18" @click="closeModal">Close</button>
        </div>
      </div>
    </div>
  </div> -->
  <div class="text-center mt-36">
    <img class="max-h-48 inline-block mb-4" src="../../assets/404.png" alt="error"/>
    <div class="text-[50px]">Page Not Found</div>
    <div class="mb-8">We can not find the page you are looking for</div>
    <button @click=gotoHome() class="bg-[#5C5CFF] text-white font-semibold py-2 px-5 rounded-full 
                           hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300">Back to Home</button>
  </div>
</template>
 
<style scoped>
  .modal-mask {
    position: fixed;
    z-index: 9998;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: table;
    transition: opacity 0.3s ease;
  }
  .modal-wrapper {
    display: table-cell;
    vertical-align: middle;
  }
  .modal-container {
    width: 300px;
    margin: 0px auto;
    padding: 20px 30px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  }
  .modal-header h3 {
    margin-top: 0;
    color: red;
  }
  .modal-body {
    margin: 20px 0;
  }
  .modal-button {
    display: flex;
    justify-content: end;
  }
</style>