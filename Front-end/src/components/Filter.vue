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

    const showFilterMode = (mode) => {
        const getFilterDate = document.getElementById("filter-date")
        const getFilterCategory = document.getElementById("filter-category")

        if(mode=='date'){
            if(getFilterDate.classList[getFilterDate.classList.length-1]=='invisible'){
                getFilterDate.classList.remove('invisible')
                getFilterDate.classList.add('visible')
                getFilterCategory.classList.remove('visible')
                getFilterCategory.classList.add('invisible')
            }
        }else if(mode=='category'){
            if(getFilterCategory.classList[getFilterCategory.classList.length-1]=='invisible'){
                getFilterCategory.classList.remove('invisible')
                getFilterCategory.classList.add('visible')
                getFilterDate.classList.remove('visible')
                getFilterDate.classList.add('invisible')
            }
        }else{
            getFilterCategory.classList.remove('visible')
            getFilterCategory.classList.add('invisible')
            getFilterDate.classList.remove('visible')
            getFilterDate.classList.add('invisible')
        }
    }

</script>

<template>        

    <!-- Filter bar -->

    <div class="bg-[#ECECFE] flex flex-row items-center px-5 py-1 rounded-lg ">
        <h2 class="w-[400px] text-lg font-semibold">Filter :</h2>
        <div class="w-full flex items-center border-l-2 border-r-2 border-gray-400 px-5 mx-2">
            <input type="radio" id ="past" value="past" v-model="filterMode" @click="($emit('past'),showFilterMode())"
                   class="w-5 h-5 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"/>
            <label for="past"
                   class="py-3 ml-2 w-full  hover:text-blue-500">Past events</label>
        </div>
        <div class="w-full flex items-center border-r-2 border-gray-400 px-2 mx-2">
            <input type="radio" id="upcoming" value="upcoming" v-model="filterMode" @click="($emit('upcoming'),showFilterMode())" 
                   class="w-5 h-5 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"/>
            <label for="upcoming"
                    class="py-3 ml-2 w-full hover:text-blue-500">Upcoming events</label>
        </div>
        <div class="w-full flex items-center border-r-2 border-gray-400 px-2 mx-2">
            <input type="radio" id="date" value="date" v-model="filterMode" @click="showFilterMode('date')"
                    class="w-5 h-5 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"/>
            <label for="date"
                    class="py-3 ml-2 w-full hover:text-blue-500">Date</label>
        </div>
        <div class="w-full flex items-center border-r-2 border-gray-400 px-2 mx-2">
            <input type="radio" id="category" value="category" v-model="filterMode" @click="showFilterMode('category')"
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

    <!-- Search bar , choose date , choose dropdown category name-->
    <div v-show="filterMode === 'date' || filterMode === 'category'" class="flex flex-row w-full mb-5 pl-2 px-2 py-1 ">
        <!-- Search Hide -->
        <div class="flex flex-col mx-5 basis-1/2 invisible">
            <p class="p-1"><IconSearch class="w-5 h-5 inline mr-2"/>Search by booking name </p>
            <input type="text" class="w-[300px] text-[#3333A3] p-1 bg-inherit border-b-2 hover:border-blue-800"> 
        </div>
        <div id="filter-date" class="flex flex-col mx-5 basis-1/4 invisible">
            <p class="p-1">Choose date </p>
            <input type="date" v-model="selectedDay" @change="($emit('selectDay', selectedDay))" 
                   class="w-[150px] text-gray-500 p-1 rounded"> 
        </div>
        <div id="filter-category" class="flex flex-col mx-5 basis-1/4 invisible">
            <p class="p-1">Choose a category</p>
            <select v-model="selectedCategory" @change="($emit('categoryName', selectedCategory))"
                    class="w-[250px] text-gray-500 p-1 rounded">
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


