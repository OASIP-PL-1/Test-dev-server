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
<div class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
  <div class="bg-white w-[700px] m-auto mt-32 py-5 px-7 text-left rounded-lg">
    <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
        Edit User
      <button class="float-right" @click="$emit('hideEditMode'),resetEditUser()"><IconCancel class="w-5 h-5"/></button>
    </h2>
    <div class="flex flex-row m-3">
      <img :src="pathImg(thisUser.id)" alt="human" class="w-32 h-32 mt-8 mx-auto"/>
      <div class="flex flex-col">
        <div class="my-2">
          <h4 class="font-semibold text-[#3333A3]">Username</h4>
          <input type="text" id="username" name="username" 
                v-model="editingUser.userName" maxlength="100" size="50"  @blur="checkMatchName(editingUser.userName)" required
                class="bg-gray-50 border border-gray-300 rounded-lg p-1 mt-1 w-5/6">
          <span v-show="matchNameStatus" class="text-red-500">Username is already existed, please try another name</span>
        </div>
        <div class="my-2">
          <h4 class="font-semibold text-[#3333A3]">Email</h4>
          <input type="text" id="useremail" name="useremail" 
                v-model="editingUser.userEmail" maxlength="50" size="50"  
                @blur="emailValidation(editingUser.userEmail), checkMatchEmail(editingUser.userEmail)" required
                class="bg-gray-50 border border-gray-300 rounded-lg p-1 mt-1 w-5/6">
            <span v-show="emailStatus" class="text-red-500">Input email is invalid!</span>
            <span v-show="matchEmailStatus" class="text-red-500">This email is already existed, please try another email</span>
        </div>

        <div class="my-7">
          <label class="font-semibold text-[#3333A3]" >Role</label>
          <div class="my-auto m-1 py-1.5 px-2 text-center rounded-md border-2 inline">
            <input type="radio" id="1" name="role" value="admin" v-model="editingUser.userRole" :checked="editingUser.userRole==='admin'"
                class="accent-[#F36747] align-middle w-4 h-4"> 
            <label for="1" class="ml-1 align-top">admin</label>
          </div>
          <div class="my-auto m-1 py-1.5 px-2 text-center rounded-md border-2 inline">
            <input type="radio" id="2" name="role" value="lecturer" v-model="editingUser.userRole" :checked="editingUser.userRole==='lecturer'"
                class="accent-pink-500 align-middle w-4 h-4"> 
            <label for="2" class="ml-1 align-top">lecturer</label>
          </div>
          <div class="my-auto m-1 py-1.5 px-2 text-center rounded-md border-2 inline">
            <input type="radio" id="3" name="role" value="student" v-model="editingUser.userRole" :checked="editingUser.userRole==='student'"
                class="accent-violet-500 align-middle w-4 h-4"> 
            <label for="3" class="ml-1 align-top">student</label>
          </div>
        </div>

        <div class="text-right">
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

</style>