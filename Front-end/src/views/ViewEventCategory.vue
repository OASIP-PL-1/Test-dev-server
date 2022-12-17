<script setup>
import {ref, computed, onMounted} from 'vue'
import { useSignIn } from '../state/signIn.js';
import {useRouter} from 'vue-router'

// import Icons
import IconEdit from '../components/icons/iconEdit.vue'
import IconLoading from '../components/icons/iconLoading.vue'
import IconCancel from '../components/icons/iconCancel.vue'

const signIn = useSignIn()

const myRouter = useRouter()
    const goToError401 = () => myRouter.push({name: 'Error401'}) 
    const goToError403 = () => myRouter.push({name: 'Error403'})
    const goToError500 = () => myRouter.push({name: 'Error500'})

const eventCategories = ref()
const loading =ref()
const message = ref()
// --- GET List All Event Category ---
const getEventCategories = async () => {
    loading.value = true
    message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
      ).catch((error)=> {
        // message.value = "Not Found Backend Server!!!"
        console.log(error)
        goToError500()
    });
    if(res.status==200){
      eventCategories.value = await res.json()
      loading.value = false
    }else if(res.status===401){
      let errorText = await res.text()
      if(errorText==="Token is expired."){
        await signIn.sendRefreshToken()
      }else{
        goToError401()
      }
    }else if(res.status===403){
      goToError403()
    }
}
onMounted(async () => {
      await getEventCategories()
  })

// --- Edit Mode (Modal) ---
const editMode = ref(false)
const showModalEdit = () => editMode.value = true
const hideModalEdit = () => editMode.value = false
const editingCategory = ref({})

// --- Editing Event Category ---
const edit = (editCategory) => {
  showModalEdit()
  editingCategory.value = {
    id : editCategory.id,
    name : editCategory.categoryName,
    duration : editCategory.duration,
    description : editCategory.categoryDescription
  }
}

// --- PUT Update Event Category ---
const updateCategory = async (category)=>{
  //เพิ่มตัว check ข้อมูลแก้ไขไหม ถ้าไม่ ไม่ต้อง PUT ไป
  
  if(category.description !== null){
    if(category.description.trim().length === 0){
      category.description = null
    }else{
      category.description = category.description.trim()
    }
  }
  const res = await fetch(`${import.meta.env.VITE_BASE_URL}/eventcategories`, 
  {
    method:'PUT',
    headers:{
      'content-type':'application/json',
      'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
    },
    body: JSON.stringify({
      id: category.id,
      categoryName: category.name.trim(),
      categoryDescription: category.description,
      duration: category.duration
    })
  }).catch(error => console.log(error));
      if(res.status===200){
        const updatedCategory = await res.json()  
        eventCategories.value = eventCategories.value.map((thisCategory) => thisCategory.id === updatedCategory.id ? 
          {...thisCategory, 
            categoryName: updatedCategory.categoryName, 
            categoryDescription: updatedCategory.categoryDescription,
            duration: updatedCategory.duration,
          } : thisCategory 
        ) 
        hideModalEdit()
      }else if(res.status===400){
        alert("Cannot Edit This Event : The data is incorrect")
      }else if(res.status===414){
        alert("Cannot Edit Category : The data length in the input field is too large. Please try again.")
      }else if(res.status===500){
        goToError500()
      }else if(res.status===401){
        goToError401()
      }else if(res.status===403){
        goToError403()
      }else{alert('Error, Cannot Edit Category')}
}

// --- Validate ---
const checkEdited = computed(()=>{
  //true = ยังไม่แก้ข้อมูล
  if(editingCategory.value.id !== undefined && eventCategories.value !== undefined){
    const selectedCategory = eventCategories.value.find((category)=> category.id === editingCategory.value.id)
    return selectedCategory.categoryName === editingCategory.value.name
            && selectedCategory.categoryDescription === editingCategory.value.description
            && selectedCategory.duration === editingCategory.value.duration 
  }
})
const checkDuration = (duration) => duration >= 1 && duration <= 480 //true = duration ถูกต้อง
const checkName = (id,name) => {
  if(editingCategory.value.id !== undefined && id !== undefined && name !== undefined){
    const otherCategories = eventCategories.value.filter((category) => category.id !== id)
    //true = ชื่อซ้ำตัวอื่น
    return otherCategories.find((category) => category.categoryName.trim().toLowerCase() === name.trim().toLowerCase())
  }
}
</script>
 
<template>

  <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
  <div v-else-if="eventCategories == 0" class="text-red-600 my-16 text-center"> -- No Event Categories -- </div>
  <div v-else class="m-10 grid grid-cols-3 gap-3">
    <div v-for="(category, index) in eventCategories" :key="index" class="bg-white rounded-lg px-5 pt-3 pb-7">
      <IconEdit class="w-4 h-4 ml-5 inline align-top float-right text-gray-400" @click="edit(category)"/>
      <h4 class="text-[16px] font-semibold mt-2 pb-2 border-b-2 border-gray-400">{{category.categoryName}}</h4>
      <p class="my-4"><b>Duration : </b> {{category.duration}} min</p>
      <b>Description</b>
      <div v-if="category.categoryDescription !== null">{{category.categoryDescription}}</div>
      <div v-else>-</div>
    </div>
    <!-- Modal Edit  -->
    <div class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block" v-show="false">
      <div class="bg-white w-[700px] m-auto mt-32 py-3 px-5 text-left rounded-lg">
        <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
            Edit Category
          <button class="float-right" @click=""><IconCancel class="w-5 h-5"/></button>
        </h2>
        <div class="flex flex-row m-3">
          
        </div>
      </div>
    </div>
  </div>

  <div v-show="editMode" class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
        <div class="bg-white w-[700px] py-5 px-7 m-auto mt-48 rounded-lg">
          <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
                Edit Category
            <button class="float-right" @click="hideModalEdit()"><IconCancel class="w-5 h-5"/></button>
          </h2>
          <div class="flex flex-col my-3">
            <div class="flex flex-row mx-5">
              <h3 class="basis-1/4 font-semibold px-5 text-[#3333A3]">Category name</h3>
              <div class="basis-3/4">
                <input type="text" v-model="editingCategory.name" size="50" maxlength="100" 
                      class="bg-gray-50 border border-gray-300 rounded-lg p-1 mt-1">
                <span class="text-red-400 block" v-show="checkName(editingCategory.id,editingCategory.name)">
                      &#9888; This category name is already existed, please try another name.
                </span>
              </div>
            </div>
            <div class="flex flex-row mx-5 mt-2">
              <h3 class="basis-1/4 font-semibold px-5 text-[#3333A3]">Duration</h3>
              <div class="basis-3/4">
                <input type="number" min="1" max="480" v-model="editingCategory.duration" maxlength="3" 
                      class="bg-gray-50 border border-gray-300 rounded-lg p-1 mt-1">
                  <span class="text-[12px] text-gray-400">&ensp; (1 - 480 min)</span>
                  <span class="text-red-400 block" v-show="!checkDuration(editingCategory.duration)">
                    &#9888; Duration is out of range, please choose time between 1-480 minutes
                  </span>
              </div>
            </div>
            <div class="flex flex-row mx-5 mt-2">
              <h3 class="basis-1/4 font-semibold px-5 text-[#3333A3]">Description</h3>
              <div class="basis-3/4">
                <textarea v-model="editingCategory.description" rows="7" cols="50" maxlength="500" 
                        class="bg-gray-50 border border-gray-300 rounded-lg p-1 mt-1 h-20"></textarea>
              </div>
            </div>
          </div>

          <div class="text-right">
                <button @click="hideModalEdit()" class="bg-red-100 text-red-500 py-1.5 px-4 rounded-full 
                           hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                &ensp;
                <button @click="updateCategory(editingCategory)" :disabled="checkName(editingCategory.id,editingCategory.name) 
                                  || checkEdited 
                                  || !checkDuration(editingCategory.duration) 
                                  || editingCategory.name.trim().length <= 0"
                        class="bg-[#5C5CFF] text-white py-1.5 px-4 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300 disabled:bg-gray-300">Save</button>
           </div>
        </div>
    </div>

</template>
 
<style scoped>
b {
    color: #3333A3;
}
</style>