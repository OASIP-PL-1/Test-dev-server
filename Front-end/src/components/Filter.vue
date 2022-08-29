<script setup>
    import { ref } from 'vue'
    defineEmits(['past','upcoming','selectDay','categoryName','reset'])
    defineProps({
      eventCategories:{
          type: Array,
          require: true
      }
    })

    const filterMode = ref('')
    const selectedCategory = ref()
    const selectedDay = ref()

</script>

<template>        
    <div class="filter">
    <h2>Filter</h2>
        <hr>
        <div>
            <div class="choices">
                <input type="radio" id ="past" value="past" v-model="filterMode" @click="($emit('past'))"/>
                <label for="past">Past events</label>
            <br>
                <input type="radio" id="upcoming" value="upcoming" v-model="filterMode" @click="($emit('upcoming'))"/>
                <label for="upcoming">Upcoming events</label>
            <br>
                <input type="radio" id="date" value="date" v-model="filterMode"/>
                <label for="date">Chosen date</label>
            <br>
                <input type="radio" id="category" value="category" v-model="filterMode"/>
                <label for="category">Category name</label>
            <br><br>
                <div v-show="filterMode=='date'" >
                <b>Filter by chosen date: </b>
                    <hr>
                    <span>Input a date:</span><br>
                    <input type="date" class="inputdate" v-model="selectedDay" @change="($emit('selectDay', selectedDay))">
                </div>
                <div v-show="filterMode=='category'">
                    <b>Filter by category name: </b>
                    <hr>
                    <span>Choose a category:</span><br>
                    <select v-model="selectedCategory" @change="($emit('categoryName', selectedCategory))">
                        <option selected value="0">--Not selected--</option>
                        <option v-for="category in eventCategories" :value="category.id">
                            {{ category.categoryName }}
                        </option>
                    </select>
                </div>
            </div>
        </div>
        <hr>
        <button @click="($emit('reset'))" class="button-18" style="float: right">Reset</button>
    </div>
</template>

<style scoped>
    h2 {
        color: #FFCB4C;
    }
    b, label, span {
        color: white;
    }
    select {
        border-radius: 5px;
    }
    .filter {
        background-color: #3333A3;
        border-radius: 30px;
        padding: 4px 20px 20px 20px;
        height: 600px;
        max-width: 200px;
        box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
        -o-object-fit: cover;
        object-fit: cover;
    }
    .choices{
        padding: 0;
    }
    .inputdate{
        margin: 8px 0 4px 0;
        padding: 8px;
        height: 20px;
        border-radius: 10px;
    }
    select {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0;
    }
</style>


