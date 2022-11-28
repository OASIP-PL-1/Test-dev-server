<script setup>

</script>
   
<template>
    <div class="text-center">
        <img class="my-2" src="../../assets/logo.png" alt="error"/>
        <div class="text-[100px]">500</div>
        <div class="text-[50px]">Internal Server Error</div>
        <div class="mb-8">Please be patient or try again later</div>
        <button @click=gotoHome() class="bg-[#5C5CFF] text-white font-semibold py-2 px-5 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300">Back to Home</button>
    </div>
</template>
   
<style scoped>
</style>