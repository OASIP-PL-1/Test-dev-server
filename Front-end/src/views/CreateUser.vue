<script setup>
    import { ref, computed, onMounted } from 'vue'
    import {useRouter} from 'vue-router'
    import {useListUser} from '../state/getListUser.js'

    const getListUser = useListUser() 


    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    const goThisUser = (newId) => myRouter.push({name: 'ThisUser', params:{userId:newId}})

    // -- get list All User for check unique name and email --
    // const users = ref()
    // const loading = ref()
    // const message = ref()

    // const getUsers = async () => {
    //     loading.value = true
    //     message.value = "loading..."
    //     const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`)
    //       .catch((error)=> {
    //         message.value = "Not Found Backend Server!!!"
    //         console.log(error)
    //         console.log('GET List All User Fail')
    //     });
    //     users.value = await res.json()
    //     loading.value = false
    //     if(res.status==200){
    //       console.log(`GET List All User OK`)
    //       console.log(res.status)
    //     }
    //   }
    onMounted(async () => {
      await getListUser.getUserCheckListCreate()
    })

    // -- Create New User --
    const newUser = ref({
        name : "",
        email: "",
        role : "student",
        password : '',
        confirmPW : '',
    })

    const createNewUser = async (user)=>{
      console.log(user)
      const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`,{
        method:'POST',
        headers:{
          'content-type':'application/json',
        },
        body: JSON.stringify({
            userName:user.name.trim(),
            userEmail:user.email.trim(),
            userRole:user.role,
            userPassword:user.password
        })
      }).catch(error => console.log(error));
      console.log(res.status)
            if(res.status===200){
                const newId = await res.json()
                console.log(newId)
                goThisUser(newId)
                console.log('Create New User OK')
            }else if(res.status===400){
                console.log(res)
                console.log(res.message)
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
        role : "student",
        password : "",
        confirmPW :"",
      }
      matchNameStatus.value = false
      matchEmailStatus.value = false
    }

    // ----- Validate check -----

    // - ถ้าเงื่อนไขเป็น false ทั้งหมดถึงจะให้ sign up ได้ (:disabled=true/false)
    const checkBeforeAdd = computed(()=>{
      //true = Invalid
      return newUser.value.name.trim().length === 0
        || newUser.value.email.trim().length === 0
        || newUser.value.password.length === 0
        || newUser.value.confirmPW.length === 0
        || emailStatus.value
        || matchNameStatus.value || matchEmailStatus.value
        || !passwordStatus.value || !(newUser.value.password === newUser.value.confirmPW)
        || matchConfirmPassword.value === false
    })

    // - check ชื่อซ้ำ 
    const matchNameStatus = ref(false)
    const checkMatchName = (name) => {
      name = name.trim()
      if(name.length > 0){
        const matchName = getListUser.userCheckListCreate.filter((user) => user.userName.replace(' ','').toLowerCase() === name.replace(' ','').toLowerCase())
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
        const matchEmail = getListUser.userCheckListCreate.filter((user) => user.userEmail === email)
        //true = email ซ้ำ
        matchEmail.length > 0 ? matchEmailStatus.value = true : matchEmailStatus.value = false
      }else{
        matchEmailStatus.value = false
      }
    }

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

    // const showInputConfirm = ref(false) 
    // - check length password
    const passwordStatus = ref(true) // true = Valid
    const checkPassword = (password) => {
      if(password.length < 8 ){
        passwordStatus.value = false 
        // showInputConfirm.value = false
      }else{
        passwordStatus.value = true
        // showInputConfirm.value = true
      }
    }
    
    // - check ว่า password = confrim password ไหม?
    const matchConfirmPassword = ref() // true = match
    const checkComfirmPassword = (pw1,pw2) => pw1 === pw2 ? matchConfirmPassword.value = true : matchConfirmPassword.value = false
    //Encode password


</script>
 
<template>
  <!-- <div style="margin-top: 10em;"> -->
    <div v-if="getListUser.loading" class="subText" style="margin-top: 2em;">{{getListUser.message}}</div>
    <div v-else>
      <div class="thisEvent">
        <button @click="goBack" class="button-18" role="button">Back</button>
      </div>
    <div class="box">
      <h2>Sign Up</h2>
      <p>Fill the form to create an account.</p>
      <hr>
      <table>
        <tr>
          <th><label for="username"><b>Username : </b></label></th>
          <td style="text-align: right;"><span class="subText">{{newUser.name.length}} / 100</span></td>
        </tr>
        <tr>
          <td colspan="2">
            <input type="text" placeholder="Enter username" name="username" 
                  v-model="newUser.name" maxlength="100" size="50" @blur="checkMatchName(newUser.name)" required>&ensp;
            <span v-show="matchNameStatus" class="warning">Username is already existed, please try another name</span>
          </td>
        </tr>
          <tr>
            <th><label for="email"><b>Email : </b></label></th>
            <td style="text-align: right;"><span class="subText">{{newUser.email.length}} / 50</span></td>
          </tr>
        <tr>
          <td colspan="2">
            <input type="text" placeholder="Enter email" name="email" 
                  v-model="newUser.email" maxlength="50" size="50" @blur="emailValidation(newUser.email), checkMatchEmail(newUser.email)" required>&ensp;
            
            <span v-show="emailStatus" class="warning"><br/>Input email is invalid!</span>
            <span v-show="matchEmailStatus" class="warning"><br/>This email is already existed, please try another email</span>
          </td>
        </tr>
        <tr>
            <th><label for="password"><b>Password : </b></label></th>
            <td style="text-align: right;"><span class="subText">8 - 14 character</span></td>
          </tr>
        <tr>
          <td colspan="2">
            <input type="password" placeholder="Enter password" name="password" 
                  v-model="newUser.password" minlength="8" maxlength="14" size="50" @blur="checkPassword(newUser.password)" required >&ensp;
            <span v-show="!passwordStatus" class="warning"><br/>Password should be at least 8 characters.</span>
          </td>
        </tr>
        <tr>
            <th><label for="confrim"><b>Confirm Password : </b></label></th>
        </tr>
        <tr>
          <td colspan="2">
            <input type="password" placeholder="Comfirm password" name="confrim" 
                  v-model="newUser.confirmPW" minlength="8" maxlength="14" size="50" @blur="checkComfirmPassword(newUser.password, newUser.confirmPW)" required>&ensp;
            <span v-show="matchConfirmPassword === false" class="warning"><br/>The password DOES NOT match</span>
          </td>
        </tr>
        <tr style="color:white; padding-top: 10px;">
          <td colspan="2">
            <b style="padding: 1em; ">Role :</b>
          <label>
            <input type="radio" id="1" name="role" value="admin" v-model="newUser.role"> Admin
          </label>
          <label style="padding-left: 2em">
            <input type="radio" id="2" name="role" value="lecturer" v-model="newUser.role"> Lecturer
          </label>
          <label style="padding-left: 2em">
            <input type="radio" id="3" name="role" value="student" v-model="newUser.role" checked> Student
          </label>
          </td>
        </tr>
      </table>
        <br>
      <div class="button-center">
        <button type="submit" class="button-18" @click="createNewUser(newUser)" :disabled="checkBeforeAdd" style="width: 100%;">Sign Up</button>
        <br/><br/>
        <!-- <button type="button" :class="['button-18','negative']" @click="clearInput">Clear</button> -->
      </div>
    </div>
    </div>
  <!-- </div> -->
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
    input {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0 ;
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
        width: 80%;
    }
    tr {
        padding: auto;
        -o-object-fit: cover;
        object-fit: cover;
    }
    th {
        /* vertical-align: top; */
        text-align: left;
        width: 19%;
        padding: 10px 2em 0 2em;
        -o-object-fit: cover;
        object-fit: cover;
    }
    td {
        text-align: left;
        width: 30%;
        padding: 0 2em 10px 2em;
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
      max-width: 600px;
      box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
      margin-left: auto;
      margin-right: auto;
      text-align: center;
      -o-object-fit: cover;
      object-fit: cover;
    }
    .thisEvent {
      padding: 0 2em;
    }
    .button-right {
      text-align: center;
    }
    .warning{
        color: orangered;
    }
    span{
      font-size: smaller;
    }
    /* input:invalid {
      border: red 1px solid;
    } */
 
</style>