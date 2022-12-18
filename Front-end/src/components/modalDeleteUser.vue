<script setup>
import IconDeleteAlert from './icons/iconDeleteAlert.vue'
import IconCancel from './icons/iconCancel.vue'

defineEmits(['hideDeleteModal','removeUser'])
defineProps({
    modalStatusDelete:{
       type: Boolean,
       require: true
  }, thisUser:{
        type:Object
  }
})
const pathImg = (userId) => `${import.meta.env.BASE_URL}humans/human${userId%8+1}.png`

</script>

<template>
    <div v-show="modalStatusDelete" class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
        <div class="bg-white w-2/6 py-5 px-5 m-auto mt-48 rounded-lg">
            <button @click="$emit('hideDeleteModal')" class="float-right"><IconCancel class="w-5 h-5"/></button>
            <div class="flex flex-row ml-6 mt-3 mr-3">
                <IconDeleteAlert class="w-16 h-16 inline align-middle text-red-400"/>
                <h3 class="text-[20px] font-semibold pl-2">The data connected to this account will be deleted.
                    <p class="text-[16px] font-normal">Are you sure you want to delete this account?</p>
                </h3>
            </div>
            <div class="border-2 rounded-lg flex flex-row px-2 py-2 mt-3 mb-5 ml-5 mr-5">
                <img :src="pathImg(thisUser.id)" alt="human" class="w-16 h-16"/>
                <div class="my-auto px-5">
                    <h3 class="font-semibold">{{thisUser.userName}}</h3>
                    <p>{{thisUser.userEmail}}</p>
                </div>
            </div>

            <div class="text-right">
                <button @click="$emit('hideDeleteModal')" class="bg-[#E3ECFC] text-[#5C5CFF] py-1.5 px-4 rounded-full 
                           hover:bg-[#5C5CFF] hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                &ensp;
                <button @click="$emit('removeUser')" class="bg-red-400 text-white py-1.5 px-4 rounded-full 
                            hover:bg-[#FFA21A] active:bg-red-500 duration-300">Confirm</button>
            </div>
        </div>
    </div>
</template>

<style></style>