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
          message.value = "Password Incorrect"
        }else if(respone.status===404){
          message.value = "A user with the specified email DOES NOT exist"
        }
      }else if(res.status===400){
        alert("Invalid Data")
      }else{
        alert('Error Login, ' + res.status)
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
      }else{
        passwordStatus.value = true
      }
    }
</script>
 
<template>
  <div class="h-full">
    <div class="bg-[#FFF] float-right h-full w-2/4">
      <div class="pt-32 pb-8 text-center text-3xl text-[#3333A3]"><b>Log In</b></div>
        <div class="mx-24">
          <label for="email">
            <IconEmail class="w-[20px] h-[20px] inline mr-2"/>
            <b>Email</b>
          </label>
          <div class="relative flex items-center text-gray-400 focus-within:text-gray-600 mt-1 mb-6">
            <input type="text"
                   placeholder="Email" 
                   name="email" 
                   class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                   v-model="userLogin.email" 
                   maxlength="50" 
                   size="50" 
                   required
                   >
          </div>   
         
        <label for="password">
          <IconPassword class="w-[20px] h-[20px] inline mr-2"/>
          <b>Password</b>
        </label>
          <div class="relative flex items-center text-gray-400 focus-within:text-gray-600 mt-1">
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
      <div v-show="!passwordStatus" class="text-red-500 text-right">At least 8 characters</div>
      <div class="text-green-500 mt-8 mb-2 text-center" v-if="message==='Login Successful'">{{message}}</div>
      <div class="text-red-500 mt-8 mb-2 text-center" v-else>{{message}}</div>
      <button type="submit" 
              class="bg-[#5C5CFF] text-white text-[18px] font-bold py-2 px-5 rounded-full
                    hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300"
              @click="logIn(userLogin)" :disabled="checkBeforeAdd" style="width: 100%;">Login</button>
    </div>
  </div>
</div>
</template>
 
<style scoped>
   
</style>