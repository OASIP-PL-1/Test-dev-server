<script setup>
    import { ref, computed} from 'vue'
    import {useRouter} from 'vue-router'
    import {useSignIn} from '../state/signIn.js'

    //-- insert icons --
    import IconEmail from '../components/icons/iconEmail.vue'
    import IconPassword from '../components/icons/iconPassword.vue'

    const signIn = useSignIn()

    const myRouter = useRouter()
    const gotoEvent = () => myRouter.push({name: 'ViewEvent'})
    // const goBack = () => myRouter.go(-1)
    // const goThisUser = (newId) => myRouter.push({name: 'ThisUser', params:{userId:newId}})
    
    const userLogin = ref({email:"",password:""})
    const message = ref("")

    const logIn = async (user)=>{
      const res = await fetch(`${import.meta.env.VITE_BASE_URL}/login`,{
        method:'POST',
        headers:{
          'content-type':'application/json',
        },
        body: JSON.stringify({
            userEmail:user.email.trim(),
            userPassword:user.password
        })
      }).catch(error => console.log(error))
      
      const respone = await res.json()
      
      console.log(respone)
      if(res.status===200){
        if(respone.status===200){
          if(signIn.statusLogin === true && signIn.user.role === 'admin'){
            //มีการ login อยู่แล้ว และเป็น admin จะเป็น check login mode
            console.log("Check Password Login")
            message.value = "Login Successful"
          }else{
            message.value = "Login Successful"
              
            signIn.setCookie('accessToken',respone.message.accessToken,1)
            signIn.setCookie('refreshToken',respone.message.refreshToken,1)
            const thisUser = {
              id:Number(respone.message.userId),
              name:respone.message.userName,
              role:respone.message.userRole,
              email:respone.message.userEmail
            }
            signIn.setCookie('user',JSON.stringify(thisUser),1)

            signIn.statusLogin = true
            signIn.user = JSON.parse(signIn.getCookie('user'))

            if(signIn.user.role !== 'admin'){gotoEvent()}          
          }
        }else if(respone.status===401){
          console.log(respone.message.message)
          message.value = "Password Incorrect"
        }else if(respone.status===404){
          console.log(respone.message.message)
          message.value = "A user with the specified email DOES NOT exist"
        }
      }else if(res.status===400){
        console.log("Invalid Data")
      }else{
        console.log(res.status)
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
      <!-- <div style="margin-top: 10em;">
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
            <td style="text-align: right;"><span class="subText"> / 50</span></td>
          </tr>
        <tr>
          <td colspan="2">
            <input type="text" placeholder="Enter email" name="email" 
                   v-model="userLogin.email" maxlength="50" size="50" required>&ensp;
            
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
                   v-model="userLogin.password" minlength="8" maxlength="14" size="50" @blur=checkPassword(userLogin.password) required>&ensp;
            <span v-show="!passwordStatus" class="warning"><br/>At least 8 characters.</span>
          </td>
        </tr>
      </table>
      <br>
      <span style="color: greenyellow;" v-if="message==='Login Successful'">{{message}}</span>
      <span style="color: red;" v-else>{{message}}</span>
      <div class="button-center">
        <button type="submit" class="button-18" @click="logIn(userLogin)" :disabled="checkBeforeAdd" style="width: 100%;">Login</button>
        <br/><br/>

      </div>
    </div>
    </div> -->
    

  <div class="h-full">
    <div class="bg-[#FFF] float-right h-full w-2/4">
      <div class="pt-32 pb-8 text-center text-3xl text-[#3333A3]"><b>Log In</b></div>
        <div class="mx-24">
          <label for="email">
            <IconEmail class="w-[20px] h-[20px] inline mr-2"/>
            <b>Email</b>
          </label>
            <!-- <td style="text-align: right;"><span class="subText"> / 50</span></td> -->
            <!-- <label htmlFor="text" className="relative text-gray-400 focus-within:text-gray-600 block"> -->
          <div class="relative flex items-center text-gray-400 focus-within:text-gray-600 mt-2 mb-4">
            <!-- <label htmlFor="text" className="relative text-gray-400 focus-within:text-gray-600 block"> -->            
            <input type="text"
                   placeholder="Email" 
                   name="email" 
                   class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                   v-model="userLogin.email" 
                   maxlength="50" 
                   size="50" 
                   required
                   >
            <!-- <div class="text-right"> / 50</div> -->
          </div>   
            <!-- <span v-show="emailStatus" class="warning"><br/>Input email is invalid!</span> -->
            <!-- <span v-show="matchEmailStatus" class="warning"><br/>This email is already existed, please try another email</span> -->

        <label for="password">
          <IconPassword class="w-[20px] h-[20px] inline mr-2"/>
          <b>Password</b>
        </label>
          <div class="relative flex items-center text-gray-400 focus-within:text-gray-600 mt-2">
            <input type="password" 
                  placeholder="Password" 
                  name="password"
                  class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                  v-model="userLogin.password" 
                  minlength="8" 
                  maxlength="14" 
                  size="50" 
                  @blur=checkPassword(userLogin.password) 
                  required>
          </div>
      <div v-show="!passwordStatus" class="text-red-500 text-right">At least 8 characters.</div>
      <div class="text-green-500 mt-8 mb-2 text-center" v-if="message==='Login Successful'">{{message}}</div>
      <div class="text-red-500 mt-8 mb-2 text-center" v-else>{{message}}</div>
      <div class="button-center">
      <button type="submit" 
              class="bg-[#5C5CFF] text-white text-[18px] font-bold py-2 px-5 rounded-full
                    hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300"
              @click="logIn(userLogin)" :disabled="checkBeforeAdd" style="width: 100%;">Login</button>
      </div>
    </div>
  </div>
</div>
</template>
 
<style scoped>
    /* input {
        border-radius: 10px;
        padding: 0.5em;
        margin: 0.25em 0 ;
        text-rendering: auto;
        overflow: visible;
        -o-object-fit: cover;
        object-fit: cover;
    } */
    /* input:invalid {
      border: #3333A3 1px solid;
    } */
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
    /* .warning{
        color: orangered;
    } */
</style>