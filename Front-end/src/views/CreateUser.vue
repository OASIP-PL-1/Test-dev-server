<script setup>
    import { ref, computed, onMounted } from 'vue'
    import {useRouter} from 'vue-router'


    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    const goThisUser = (newId) => myRouter.push({name: 'ThisUser', params:{userId:newId}})

    // -- get list All User for check unique name and email --
    const users = ref()
    const loading = ref()
    const message = ref()

    const getUsers = async () => {
        loading.value = true
        message.value = "loading..."
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`)
          .catch((error)=> {
            message.value = "Not Found Backend Server!!!"
            console.log(error)
            console.log('GET List All User Fail')
        });
        users.value = await res.json()
        loading.value = false
        if(res.status==200){
          console.log(`GET List All User OK`)
          console.log(res.status)
        }
      }
        onMounted(async () => {
          await getUsers()
      })

    // -- Create New User --
    const newUser = ref({
        name : "",
        email: "",
        role : "student"
    })

    const createNewUser = async (user)=>{
      console.log(user)
      const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`,{
        method:'POST',
        headers:{
          'content-type':'application/json'
        },
        body: JSON.stringify({
            userName:user.name.trim(),
            userEmail:user.email.trim(),
            userRole:user.role
        })
      }).catch(error => console.log(error));
      console.log(res.status)
            if(res.status===200){
                const newId = await res.json()
                console.log(newId)
                goThisUser(newId)
                console.log('Create New User OK')
            }else if(res.status===400){
                console.log("Cannot Create New User : The data is incorrect")
            }else if(res.status===414){
                console.log("Cannot Create New User : The data length in the input field is too large. Please try again.")
            }else if(res.status===404){
                console.log("Cannot Create New User : Not Found! User id")
            }else{
                console.log("Error, Cannot Create New User")
            }
      clearInput()
    }
    
    const clearInput = () => {
      newUser.value = {
        name : "",
        email: "",
        role : "student"
      }
      matchNameStatus.value = false
      matchEmailStatus.value = false
    }

    // Validate check

    // - ถ้าเงื่อนไขเป็น false ทั้งหมดถึงจะให้ sign up ได้ (:disabled=true/false)
    const checkBeforeAdd = computed(()=>{
      return newUser.value.name.trim().length === 0
        || newUser.value.email.trim().length === 0
        || emailStatus.value
        || matchNameStatus.value || matchEmailStatus.value
    })

    // - check ชื่อซ้ำ 
    const matchNameStatus = ref(false)
    const checkMatchName = (name) => {
      name = name.trim()
      if(name.length > 0){
        const matchName = users.value.filter((user) => user.userName === name)
        //true = ชื่อซ้ำ 
        matchName.length > 0 ? matchNameStatus.value = true : matchNameStatus.value = false
      }else{
        matchEmailStatus.value = false
      }
    }

    // - check email ซ้ำ 
    const matchEmailStatus = ref(false)
    const checkMatchEmail = (email) => {
      email = email.trim()
      if(email.length > 0){
        const matchEmail = users.value.filter((user) => user.userEmail === email)
        //true = email ซ้ำ
        matchEmail.length > 0 ? matchEmailStatus.value = true : matchEmailStatus.value = false
      }else{
        matchEmailStatus.value = false
      }
    }

    // - check email
    const emailStatus = ref(false)
    const emailValidation = (inputEmail) => {
        if(inputEmail !== ""){
            const mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
            emailStatus.value =  inputEmail.match(mailformat) ? false : true
        }else{
            emailStatus.value = false
        }
    }
    

    
</script>
 
<template>
  <div style="margin-top: 10em;">
    <button @click="goBack" class="button-18" role="button">Back</button>
    <div class="box">
      <h2>Sign Up</h2>
      <p>Fill the form to create an account.</p>
      <hr>
      <div style="color: white;">
        <label>
          <input type="radio" id="1" name="role" value="admin" v-model="newUser.role"> Admin
        </label>
        <label>
          <input type="radio" id="2" name="role" value="lecturer" v-model="newUser.role"> Lecturer
        </label>
        <label>
          <input type="radio" id="3" name="role" value="student" v-model="newUser.role" checked> Student
        </label>
      </div>
      <table>
        <tr>
          <th><label for="username"><b>Username : </b></label></th>
          <td>
            <input type="text" placeholder="Enter username" name="username" 
                  v-model="newUser.name" maxlength="100" size="50" @blur="checkMatchName(newUser.name)" required>&ensp;
            <span class="subText">{{newUser.name.trim().length}} / 100</span>
            <br><span v-show="matchNameStatus" class="warning">ชื่อนี้ถูกใช้ไปแล้ว! กรุณาตั้งชื่อใหม่</span>
          </td>
        </tr>
        <tr>
          <th><label for="email"><b>Email : </b></label></th>
          <td>
            <input type="text" placeholder="Enter email" name="email" 
                  v-model="newUser.email" maxlength="50" size="50" @blur="emailValidation(newUser.email), checkMatchEmail(newUser.email)" required>&ensp;
            <span class="subText">{{newUser.email.trim().length}} / 50</span>
            <span v-show="emailStatus" class="warning"><br/>Sorry! an invalid email!</span>
            <span v-show="matchEmailStatus" class="warning"><br/>อีเมลนี้ เคยถูกใช้ไปแล้ว!</span>
          </td>
        </tr>
      </table>
      
        <br>
        <br>
      <div class="button-right">
        <button type="button" :class="['button-18','negative']" @click="clearInput()">Cancel</button> &ensp;
        <button type="submit" class="button-18" @click="createNewUser(newUser)" :disabled="checkBeforeAdd">Sign Up</button>
      </div>
    </div>
  </div>
      <!-- <p>By creating an account you agree to our <a href="#" style="color:dodgerblue">Terms & Privacy</a>.</p> -->
      <!-- <p>{{newUser}}</p> -->
</template>
 
<style scoped>
    h2 {
        color: #FFCB4C;
    }
    p, b, th {
        color: white;
    }
    input, label {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0;
        text-rendering: auto;
        overflow: visible;
        -o-object-fit: cover;
        object-fit: cover;
    }
    table {
        margin-left: auto;
        margin-right: auto;
        -o-object-fit: cover;
        object-fit: cover;
    }
    tr {
        padding: auto;
        -o-object-fit: cover;
        object-fit: cover;
    }
    th {
        /* vertical-align: top; */
        text-align: right;
        width: 19%;
        padding: 5px 2px;
        -o-object-fit: cover;
        object-fit: cover;
    }
    td {
        text-align: left;
        width: 30%;
        padding: 5px 2px;
        -o-object-fit: cover;
        object-fit: cover;
    }
    .box {
      background-color:#3333A3;
      padding: 1em 2em 1em 2em;
      border-radius: 30px;
      /* min-height: 400px; */
      /* max-height: 400px; */
      /* min-width: 200px; */
      max-width: 750px;
      box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
      margin-left: auto;
      margin-right: auto;
      text-align: center;
      -o-object-fit: cover;
      object-fit: cover;
    }
    .button-right {
      text-align: right;
    }
    .warning{
        color: orangered;
    }
</style>