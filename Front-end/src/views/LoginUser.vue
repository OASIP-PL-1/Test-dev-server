<script setup>
    import { ref, computed} from 'vue'
    import {useRouter} from 'vue-router'
    import {useListUser} from '../state/getListUser.js'

    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    // const goThisUser = (newId) => myRouter.push({name: 'ThisUser', params:{userId:newId}})
    
    const userLogin = ref({email:"",password:""})
    const message = ref("")

    const loginUser = async (user)=>{
      const res = await fetch(`${import.meta.env.VITE_BASE_URL.slice(0, -4)}/login`,{
        method:'POST',
        headers:{
          'content-type':'application/json'
        },
        body: JSON.stringify({
            userEmail:user.email.trim(),
            userPassword:user.password
        })
      }).catch(error => console.log(error))

      const respone = await res.json()

      console.log(respone)
      // console.log(respone.status)
      // console.log(respone.message.message)
      // console.log(respone.message.accessToken)

      if(res.status===400){
        console.log("Invalid Data")
      }
        if(respone.status===404){
          message.value = "A user with the specified email DOES NOT exist"
        }else if(respone.status===401){
          message.value = "Password Incorrect"
        }else if(respone.status===200){
          message.value = "Login Successful"
          // localStorage
          localStorage.setItem('jwtToken',respone.message.accessToken)
          console.log(localStorage.getItem('jwtToken'))
        }else if(res.status===400){
          console.log("Invalid Data")
        }
    }

    // ----- Validate check -----
    const checkBeforeAdd = computed(()=>{
      //true = Invalid
      return userLogin.value.email.trim().length === 0 
        || userLogin.value.password.length === 0
        || userLogin.value.password.length < 8
    })
    
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
</script>
 
<template>
      <!-- <div style="margin-top: 10em;"> -->
    <div>
        <div class="thisEvent">
            <button @click="goBack" class="button-18" role="button">Back</button>
        </div>
    <div class="box">
      <h2>Login</h2>
      <hr>
      <table>
          <tr>
            <th><label for="email"><b>Email : </b></label></th>
            <!-- <td style="text-align: right;"><span class="subText"> / 50</span></td> -->
          </tr>
        <tr>
          <td colspan="2">
            <input type="text" placeholder="Enter email" name="email" 
                   v-model="userLogin.email" maxlength="50" size="50" required>&ensp;
            
            <!-- <span v-show="emailStatus" class="warning"><br/>Input email is invalid!</span> -->
            <!-- <span v-show="matchEmailStatus" class="warning"><br/>This email is already existed, please try another email</span> -->
          </td>
        </tr>
        <tr>
            <th><label for="password"><b>Password : </b></label></th>
            <!-- <td style="text-align: right;"><span class="subText">8 - 14 character</span></td> -->
          </tr>
        <tr>
          <td colspan="2">
            <input type="password" placeholder="Enter password" name="password" 
                   v-model="userLogin.password" minlength="8" maxlength="14" size="50" @blur=checkPassword(userLogin.password) required>&ensp;
            <span v-show="!passwordStatus" class="warning"><br/>At least 8 characters.</span>
          </td>
        </tr>
      </table>
      <br>
      <span style="color: greenyellow;" v-if="message==='Login Successful'">{{message}}</span>
      <span style="color: red;" v-else>{{message}}</span>
      <div class="button-center">
        <button type="submit" class="button-18" @click="loginUser(userLogin)" :disabled="checkBeforeAdd" style="width: 100%;">Login</button>
        <br/><br/>

      </div>
    </div>
    </div>
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