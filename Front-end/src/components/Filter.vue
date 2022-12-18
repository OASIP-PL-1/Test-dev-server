<script setup>
    import { ref } from 'vue'
    import IconSearch from './icons/iconSearch.vue'

    defineEmits(['past','upcoming','selectDay','categoryName','reset'])
    defineProps({
      eventCategories:{type: Array,require: true},
      filterMode:{type:String, require:true}
    })

    const filterMode = ref('')
    const selectedCategory = ref()
    const selectedDay = ref()

</script>

<template>        

    <!-- Filter bar -->

    <div class="bg-[#ECECFE] flex flex-row items-center px-5 py-1 rounded-lg ">
        <h2 class="w-[400px] text-lg font-semibold">Filter :</h2>
        <div class="w-full flex items-center border-l-2 border-r-2 border-gray-400 px-5 mx-2">
            <input type="radio" id ="past" value="past" v-model="filterMode" @click="($emit('past'))"
                   class="w-5 h-5 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"/>
            <label for="past"
                   class="py-3 ml-2 w-full  hover:text-blue-500">Past events</label>
        </div>
        <div class="w-full flex items-center border-r-2 border-gray-400 px-2 mx-2">
            <input type="radio" id="upcoming" value="upcoming" v-model="filterMode" @click="($emit('upcoming'))" 
                   class="w-5 h-5 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"/>
            <label for="upcoming"
                    class="py-3 ml-2 w-full hover:text-blue-500">Upcoming events</label>
        </div>
        <div class="w-full flex items-center border-r-2 border-gray-400 px-2 mx-2">
            <input type="radio" id="date" value="date" v-model="filterMode"
                    class="w-5 h-5 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"/>
            <label for="date"
                    class="py-3 ml-2 w-full hover:text-blue-500">Date</label>
        </div>
        <div class="w-full flex items-center border-r-2 border-gray-400 px-2 mx-2">
            <input type="radio" id="category" value="category" v-model="filterMode"
                    class="w-5 h-5 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"/>
            <label for="category" 
                    class="py-3 ml-2 w-full hover:text-blue-500">Category name</label>
        </div>
        <div class="w-[200px]">
            <button @click="($emit('reset'))"
                    class="bg-[#5C5CFF] text-white font-semibold py-2 px-5 rounded-full 
                           hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300">Reset</button>
        </div>    
    </div>

    <!-- choose date , choose dropdown category name-->
    <div class="w-full mb-5 pl-2 px-2 py-1 text-center">
        <div id="filter-date" v-show="filterMode === 'date'">
            <p class="p-1">Choose date</p>
            <input type="date" v-model="selectedDay" @change="($emit('selectDay', selectedDay))" 
                   class="w-2/12 text-gray-500 p-1 rounded-lg"> 
        </div>
        <div id="filter-category" v-show="filterMode === 'category'">
            <p class="p-1">Choose a category</p>
            <select v-model="selectedCategory" @change="($emit('categoryName', selectedCategory))"
                    class="w-3/12 text-gray-500 p-1 rounded-lg">
                        <option selected value="0">--Not selected--</option>
                        <option v-for="category in eventCategories" :value="category.id">
                            {{ category.categoryName }}
                        </option>
            </select>
        </div>
    </div>
</template>

<style scoped>
</style>


