<script setup>
import {ref, computed, onMounted} from 'vue'
import { useSignIn } from '../state/signIn.js';

// import Icons
import IconEdit from '../components/icons/iconEdit.vue'
import IconLoading from '../components/icons/iconLoading.vue'
import IconCancel from '../components/icons/iconCancel.vue'

const signIn = useSignIn()

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
        message.value = "Not Found Backend Server!!!"
        console.log(error)
    });
    console.log(res.status)
    if(res.status==200){
      eventCategories.value = await res.json()
      loading.value = false
      console.log(`GET List All Category OK`)
    }else if(res.status===401){
      let errorText = await res.text()
      console.log(errorText)
      if(errorText==="Token is expired."){
        await signIn.sendRefreshToken()
      }else{
        message.value = "Please login"
      }
    }else if(res.status===403){
      console.log('Unauthorized access')
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
      console.log(res.status)
      if(res.status===200){
        console.log('PUT Category Updated')
        const updatedCategory = await res.json()
        console.log(updatedCategory)      
        eventCategories.value = eventCategories.value.map((thisCategory) => thisCategory.id === updatedCategory.id ? 
          {...thisCategory, 
            categoryName: updatedCategory.categoryName, 
            categoryDescription: updatedCategory.categoryDescription,
            duration: updatedCategory.duration,
          } : thisCategory 
        ) 
        hideModalEdit()
      }else if(res.status===400){
        console.log("Cannot Edit This Event : The data is incorrect")
      }else if(res.status===414){
        console.log("Cannot Edit Category : The data length in the input field is too large. Please try again.")
      }else if(res.status===500){
        console.log('Error, Internal Server Error')
      }else if(res.status===401){
        console.log('Plase login')
      }else if(res.status===403){
        console.log('Unauthorized access')
      }else{console.log('Error, Cannot Edit Category')}
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
  <!-- <div style="margin-top: 6em;">
  <div v-if="loading" class="subText">{{message}}</div>
  <div v-else-if="eventCategories == 0" class="center"> -- No Event Categories -- </div>
  <div v-else class="center">
    <div class="box">
      <table id="listAll">
        <tr>
          <th style="width: 10%">Category Name</th>
          <th style="width: 5%;">Duration (min.)</th>
          <th style="width: 20%;">Description</th>
          <th style="width: 1%;"></th>
        </tr>
        <tr v-for="(category, index) in eventCategories" :key="index">
          <td style="border-right: 1px solid #ddd;">{{category.categoryName}}</td>
          <td style="border-right: 1px solid #ddd;" class="center" >{{category.duration}}</td>
          <td v-if="category.categoryDescription !== null">{{category.categoryDescription}}</td>
          <td v-else>-</td>
          <td style="vertical-align: middle;">
          <div class="editbt">
            <button class="button-18"  @click="edit(category)">Edit</button>
          </div>
          </td>
        </tr>
      </table>
    </div>
  </div> -->

  <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
  <div v-else-if="eventCategories == 0" class="text-red-600 my-16 text-center"> -- No Event Categories -- </div>
  <div v-else class="m-5 grid grid-cols-3 gap-3">
    <div v-for="(category, index) in eventCategories" :key="index" class="bg-white rounded-2xl px-5 pt-3 pb-7">
      <IconEdit class="w-4 h-4 ml-5 mr-2 inline align-top float-right text-gray-400" @click="edit(category)"/>
      <h4 class="text-[16px] font-semibold mt-2">{{category.categoryName}}</h4>
      <hr class="my-2 border-black">
      <p class="my-4"><b>duration : </b> {{category.duration}} min</p>
      <b>description</b>
      <div v-if="category.categoryDescription !== null">{{category.categoryDescription}}</div>
      <div v-else>-</div>
    </div>
    <!-- Modal Edit  -->
    <div class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block" v-show="false">
      <div class="bg-white w-[700px] m-auto mt-32 py-3 px-5 text-left rounded-xl">
        <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
            Edit Category
          <button class="float-right" @click=""><IconCancel class="w-5 h-5"/></button>
        </h2>
        <div class="flex flex-row m-3">
          
        </div>
      </div>
    </div>
    <!-- <div v-show="editMode">
      <modalEditCategory/>
    </div> -->
  </div>

  <!-- Modal Edit Category
    <div class="modal-mask" v-show=editMode style="display:block">
        <div class="modal-wrapper">
         Modal content
            <div class="modal-container">
                <span class="close" @click="hideModalEdit()" >&times;</span>
                <div class="modal-header">
                  {{editingCategory}}
                    <h3>-- Edit Category --</h3>
                </div>
                <div class="modal-body">
                  <table class="formEdit">
                    <tr>
                      <th>Category Name : </th>
                      <td>
                        <input type="text" v-model="editingCategory.name" size="50" maxlength="100">
                        <br>
                        <span class="warning" v-show="checkName(editingCategory.id,editingCategory.name)">
                          <span style="color: orangered;">&#9888;</span>This category name is already existed, please try another name.
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <th>Duration : </th>
                      <td>
                        <input type="number" min="1" max="480" v-model="editingCategory.duration" maxlength="3">
                        <span class="subText" style="font-size: smaller;">&ensp; (1 - 480 min)</span>
                        <br>
                        <span class="warning" v-show="!checkDuration(editingCategory.duration)">
                          <span style="color: orangered;">&#9888;</span>Duration is out of range, please choose time between 1-480 minutes
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <th>Description : </th>
                      <td><textarea v-model="editingCategory.description" rows="7" cols="50" maxlength="500"></textarea></td>
                    </tr>
                  </table>
                </div>
                <div class="modal-button">
                  <button @click="hideModalEdit()" :class="['button-18','negative']">Cancel</button>&ensp;
                  <button @click="updateCategory(editingCategory)" class="button-18" 
                      :disabled="checkName(editingCategory.id,editingCategory.name) 
                                  || checkEdited 
                                  || !checkDuration(editingCategory.duration) 
                                  || editingCategory.name.trim().length <= 0"> Save </button>
                </div>
            </div>
        </div>
    </div>
  </div> -->
  <div v-show="editMode" class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
        <div class="bg-white w-[700px] py-5 px-7 m-auto mt-48 rounded-xl">
          <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
                Edit Category
                <button class="float-right" @click="hideModalEdit()"><IconCancel class="w-5 h-5"/></button>
          </h2>
          <div class="flex flex-col my-3">
            <div class="flex flex-row mx-5 ">
              <h3 class="basis-1/4 text-[16px] font-semibold px-5">category name</h3>
              <div class="basis-3/4">
                <input type="text" v-model="editingCategory.name" size="50" maxlength="100" class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3]">
                <span class="text-red-400 block" v-show="checkName(editingCategory.id,editingCategory.name)">
                      &#9888; This category name is already existed, please try another name.
                </span>
              </div>
            </div>
            <div class="flex flex-row mx-5 ">
              <h3 class="basis-1/4 text-[16px] font-semibold px-5">duration</h3>
              <div class="basis-3/4">
                <input type="number" min="1" max="480" v-model="editingCategory.duration" maxlength="3" class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3] w-24">
                  <span class="text-gray-500 text-sm">&ensp; (1 - 480 min)</span>
                  <span class="text-red-400 block" v-show="!checkDuration(editingCategory.duration)">
                    &#9888; Duration is out of range, please choose time between 1-480 minutes
                  </span>
              </div>
            </div>
            <div class="flex flex-row mx-5 ">
              <h3 class="basis-1/4 text-[16px] font-semibold px-5">description</h3>
              <div class="basis-3/4">
                <textarea v-model="editingCategory.description" rows="7" cols="50" maxlength="500" class="bg-[#E3ECFC] p-1 text-[#3333A3] rounded-sm h-20 w-full"></textarea>
              </div>
            </div>
          </div>

          <div class="text-right mb-3">
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
h3{
    color: #3333A3;
}
  /* hr {
    color: #3333A3;
  }
  h3 {
    text-align: center;
  }
  .subText{
    color: gray;
    text-align: center;
  }
  .warning{
    color: orangered;
    font-size: smaller;
  }
  .center{
    text-align: center;
  }
  .box {    
    border-radius: 30px;
    margin-left: auto;
    margin-right: auto;
    text-align: center;
    max-width: 1600px;
  }
  table {
    text-align: left;
    width:100%;
    border-collapse: collapse;
    border-radius: 30px;
    overflow: hidden;
    box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
  }
  
  #listAll td{
    padding: 10px 20px 10px 20px;
    vertical-align: center;
    max-width: 500px;
  }

  #listAll tr:nth-child(even){background-color: #ECECFE;}
  #listAll tr:nth-child(odd){background-color: #b5b5f7}

  #listAll tr:hover {background-color: #ffd468;}

  #listAll th {
    text-align: center;
    background-color: #3333A3;
    color: #FFCB4C;
    padding: 15px;
    font-size: 19px;
  }
  .editbt {
    text-align: right;
  } */

  /* -- part modal -- */
  .modal-mask {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        transition: opacity 0.3s ease;
    }
    .modal-wrapper {
        margin-top: 10em;
        vertical-align: middle; 
    }
    .modal-container {
        max-width: 600px;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        margin: auto;
    }
    /* .modal-header h3 {
        color: rgb(0, 0, 0);
        margin: 1em 0em;
        text-align: center;
    } */
    .modal-body {
        margin: 20px 0;
    }
    .modal-button {
        display: flex;
        justify-content: end;
    }
    .close {
        color: #aaaaaa;
        float: right;
        font-size: 28px;
        font-weight: bold;
    }

    .close:hover,
    .close:focus {
        color: #000;
        text-decoration: none;
        cursor: pointer;
    }

    /* -- table Form Edit -- */
  .formEdit table {
    text-align: left;
    width:100%;
  }
  .formEdit th{
    text-align: right;
    vertical-align: top;
    padding: 10px 0px;
    width: 25%;
  }
  .formEdit td {
    text-align: left;
    padding: 5px 2px;
  }
  input, textarea {
    border-radius: 10px;
    padding: 0.5em;
    margin: 0.25em 0;
    text-rendering: auto;
    overflow: visible;
    -o-object-fit: cover;
    object-fit: cover;
  }
</style>