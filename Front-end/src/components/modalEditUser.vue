<script setup>
    import {ref, computed} from 'vue'

    import IconCancel from './icons/iconCancel.vue'

    defineEmits(['hideEditMode','save'])
    const props = defineProps({
        thisUser:{
            type: Object,
            require: true
        },
        users:{
            type: Array
        }
    })
    console.log(props.users)

    const editingUser = ref({
        id : props.thisUser.id,
        userName : props.thisUser.userName,
        userEmail : props.thisUser.userEmail,
        userRole : props.thisUser.userRole
    })

    const resetEditUser = () =>{
      editingUser.value = {
        id : props.thisUser.id,
        userName : props.thisUser.userName,
        userEmail : props.thisUser.userEmail,
        userRole : props.thisUser.userRole
      }
    }

    const userCheckList = ref(props.users)

    //Validate Check
    const checkData = computed(()=>{
        return editingUser.value.userName.trim().length === 0 
                || editingUser.value.userEmail.trim().length === 0
                || emailStatus.value || matchNameStatus.value || matchEmailStatus.value
    })

    // - check format email
    const emailStatus = ref(false)
    const emailValidation = (inputEmail) => {
      inputEmail = inputEmail.trim()
        if(inputEmail !== ""){
            const mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
            emailStatus.value =  inputEmail.match(mailformat) ? false : true
        }else{
            emailStatus.value = false
        }
    }
    // - check ชื่อซ้ำ 
    const matchNameStatus = ref(false)
    const checkMatchName = (name) => {
      name = name.trim()
      if(name.length > 0){
        const matchName = userCheckList.value.filter((user) => user.userName.replace(' ','').toLowerCase() === name.replace(' ','').toLowerCase())
        //true = ชื่อซ้ำ 
        matchName.length > 0 ? matchNameStatus.value = true : matchNameStatus.value = false
      }else{
        matchNameStatus.value = false
      }
    }

    // - check email ซ้ำ 
    const matchEmailStatus = ref(false)
    const checkMatchEmail = (email) => {
      email = email.trim()
      if(email.length > 0){
        const matchEmail = userCheckList.value.filter((user) => user.userEmail === email)
        //true = email ซ้ำ
        matchEmail.length > 0 ? matchEmailStatus.value = true : matchEmailStatus.value = false
      }else{
        matchEmailStatus.value = false
      }
    }

    const pathImg = (userId) => `${import.meta.env.BASE_URL}humans/human${userId%8+1}.png`

</script>
 
<template>
  <!-- <div class="modal-mask" style="display:block">
    <div class="modal-wrapper">
      <div class="modal-container">
      <span class="close" @click="goBack()" >&times;</span>
      <h3 style="text-align: center;"> -- Editing Mode -- </h3>
        <div class="modal-body">
          <label for="username"><b> User Name :</b></label><br>
          <input type="text" id="username" name="username" 
                  v-model="editingUser.userName" maxlength="100" size="50" @blur="checkMatchName(editingUser.userName)" required><br>
          <span v-show="matchNameStatus" class="warning">Username is already existed, please try another name<br/></span>
          <br/>
          <label for="useremail"><b>Email :</b></label><br>
          <input type="text" id="useremail" name="useremail" 
              v-model="editingUser.userEmail" maxlength="50" size="50" 
              @blur="emailValidation(editingUser.userEmail), checkMatchEmail(editingUser.userEmail)" required>
          <span v-show="emailStatus" class="warning"><br/>Input email is invalid!<br/></span>
          <span v-show="matchEmailStatus" class="warning"><br/>This email is already existed, please try another email<br/></span>
          <br/>
          <br>
          <label for="userrole"><b>Role :</b></label><br>
          <input type="radio" id="1" name="role" value="admin" v-model="editingUser.userRole" :checked="editingUser.userRole==='admin'"> 
          <label for="role"> Admin</label>&ensp;&ensp;&ensp;
          <input type="radio" id="2" name="role" value="lecturer" v-model="editingUser.userRole" :checked="editingUser.userRole==='lecturer'"> 
          <label for="role"> Lecturer</label>&ensp;&ensp;&ensp;
          <input type="radio" id="3" name="role" value="student" v-model="editingUser.userRole" :checked="editingUser.userRole==='student'"> 
          <label for="role"> Student</label>
          <br>
          <div class="button-right">
            <button @click="$emit('hideEditMode')" :class="['button-18','negative']" role="button">Cancal</button> &ensp;
            <button @click="$emit('save',editingUser)" class="button-18" role="button" :disabled="checkData">Save</button>
         </div>
        </div>
      </div>
    </div>
  </div>    -->
<div class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
  <div class="bg-white w-[700px] m-auto mt-32 py-3 px-5 text-left rounded-xl">
    <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
        Edit User
      <button class="float-right" @click="$emit('hideEditMode'),resetEditUser()"><IconCancel class="w-5 h-5"/></button>
    </h2>
    <div class="flex flex-row m-3">
      <img :src="pathImg(thisUser.id)" alt="human" class="w-32 h-32 my-2 mx-auto"/>
      <div class="flex flex-col ml-10 mt-3">
        <div class="my-2">
          <h4>username</h4>
          <input type="text" id="username" name="username" 
                v-model="editingUser.userName" maxlength="100" size="50"  @blur="checkMatchName(editingUser.userName)" required
                class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3]">
          <span v-show="matchNameStatus" class="text-red-500">Username is already existed, please try another name</span>
        </div>
        <div class="my-2">
          <h4>email</h4>
          <input type="text" id="useremail" name="useremail" 
                v-model="editingUser.userEmail" maxlength="50" size="50"  
                @blur="emailValidation(editingUser.userEmail), checkMatchEmail(editingUser.userEmail)" required
                class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3]">
            <span v-show="emailStatus" class="text-red-500">Input email is invalid!</span>
            <span v-show="matchEmailStatus" class="text-red-500">This email is already existed, please try another email</span>
        </div>

        <div class="my-7">
          <label>role</label>
          <div class="my-auto m-1 py-1.5 px-2 text-center rounded-md border-2 inline">
            <input type="radio" id="1" name="role" value="admin" v-model="editingUser.userRole" :checked="editingUser.userRole==='admin'"
                class="accent-red-500 align-middle w-4 h-4"> 
            <label for="1" class="ml-1 align-top">admin</label>
          </div>
          <div class="my-auto m-1 py-1.5 px-2 text-center rounded-md border-2 inline">
            <input type="radio" id="2" name="role" value="lecturer" v-model="editingUser.userRole" :checked="editingUser.userRole==='lecturer'"
                class="accent-blue-500 align-middle w-4 h-4"> 
            <label for="2" class="ml-1 align-top">lecturer</label>
          </div>
          <div class="my-auto m-1 py-1.5 px-2 text-center rounded-md border-2 inline">
            <input type="radio" id="3" name="role" value="student" v-model="editingUser.userRole" :checked="editingUser.userRole==='student'"
                class="accent-yellow-400 align-middle w-4 h-4"> 
            <label for="3" class="ml-1 align-top">student</label>
          </div>
        </div>

        <div class="text-right mb-3">
          <button @click="$emit('hideEditMode'),resetEditUser()" role="button"
                  class="bg-red-100 text-red-500 py-1.5 px-4 rounded-full 
                        hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                &ensp;
          <button @click="$emit('save',editingUser)"  role="button" :disabled="checkData"
                        class="bg-[#5C5CFF] text-white py-1.5 px-4 rounded-full 
                            hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300 disabled:bg-gray-300">Save</button>
          </div>
        
      </div>
    </div>
  </div>
</div>

</template>
 
<style scoped>
h4{
    font-weight: bold;
    font-size: 14px;
    color: rgb(188, 188, 188);
}
    /* input {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0;
        text-rendering: auto;
        overflow: visible;
        -o-object-fit: cover;
        object-fit: cover;
    }
   .button-right {
        float: right;
    }
    .warning{
        color: orangered;
    }
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
        padding: 20px 30px 35px 30px;
        background-color: #fff;
        border-radius: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        margin: auto;
    }
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
    } */
</style>