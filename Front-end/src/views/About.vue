<script setup>
    import {useRouter} from 'vue-router'

    import IconBack from '../components/icons/iconBack.vue'

    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
</script>
 
<template>
    <div class="m-10 w-auto">
        <div class="bg-white rounded-lg mx-auto px-10 py-8">
            <button @click="goBack()" class="text-gray-400"><IconBack class="w-5 h-5 inline"/> Back</button>
            <h2 class="text-xl font-semibold pb-2 border-b-2 border-gray-400 mt-5 text-center">About</h2>
            <p class="text-center text-lg">We are PL-1 CaFe Ni school of information technology KMUTT</p>
            <div class="flex flex-row text-center mt-10">
                <div class="basis-1/3">
                    <img src="../assets/dev-panalee.png" width="100px" class="mx-auto">
                    <br>
                    Panalee Palasri
                    <br>
                    63130500075
                </div>
                <div class="basis-1/3">
                    <img src="../assets/dev-piraya.png" width="100px" class="mx-auto">
                    <br>  
                    Piraya Sutthiparinyanon
                    <br>
                    63130500087
                </div>
                <div class="basis-1/3">
                    <img src="../assets/dev-vichuta.png" width="100px" class="mx-auto">
                    <br>    
                    Vichuta Pipoppinyo
                    <br>
                    6310500106
                </div>
            </div>
        </div>
    </div>
</template>
 
<style  scoped>
   
</style>