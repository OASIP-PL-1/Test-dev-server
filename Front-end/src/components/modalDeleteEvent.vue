<script setup>
import IconDeleteAlert from './icons/iconDeleteAlert.vue'
import IconCancel from './icons/iconCancel.vue'

defineEmits(['hideDeleteModal','removeEvent'])
defineProps({
    modalStatusDelete:{
       type: Boolean,
       require: true
  }
})
</script>

<template>
    <div v-show="modalStatusDelete" class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
        <div class="bg-white w-96 py-5 px-5 text-center m-auto mt-64 rounded-xl">
            <button @click="$emit('hideDeleteModal')" class="float-right"><IconCancel class="w-5 h-5"/></button>
            <div class="flex flex-row m-3">
                <IconDeleteAlert class="w-16 h-16 mr-5 inline align-top text-red-400"/>
                <div class="flex flex-col text-left mb-10">
                    <h3 class="text-[20px] font-semibold">Delete Event</h3>
                    Do you want to delete this event ?
                </div>
            </div>
            <div class="text-right">
                <button @click="$emit('hideDeleteModal')" class="bg-[#E3ECFC] text-[#5C5CFF] py-1.5 px-4 rounded-full 
                           hover:bg-[#5C5CFF] hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                &ensp;
                <button @click="$emit('removeEvent')" class="bg-red-400 text-white py-1.5 px-4 rounded-full 
                           hover:bg-red-500 active:bg-red-600 duration-300">Confirm</button>
            </div>
        </div>
    </div>
</template>

<style></style>