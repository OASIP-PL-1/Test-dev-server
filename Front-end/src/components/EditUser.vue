<script setup>
    import {ref, computed} from 'vue'
    import {useRouter} from 'vue-router'
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
    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
</script>
 
<template>
  <div class="modal-mask" style="display:block">
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
  </div>   
</template>
 
<style scoped>
    input {
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
        /* margin: 0 10% 2em 0; */
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
    }
</style>