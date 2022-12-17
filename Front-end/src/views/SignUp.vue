<script setup>
    import { ref, computed, onMounted } from 'vue'
    import {useRouter} from 'vue-router'
    import {useListUser} from '../state/getListUser.js'
    import { useSignIn } from '../state/signIn.js';
    //-- insert icons --
    import IconAccount from '../components/icons/iconAccount.vue'
    import IconEmail from '../components/icons/iconEmail.vue'
    import IconPassword from '../components/icons/iconPassword.vue'
    import IconKeyCheck from '../components/icons/iconKeyCheck.vue'
    import IconUserRole from '../components/icons/iconUserRole.vue'
    import IconLoading from '../components/icons/iconLoading.vue'
    import IconBack from '../components/icons/iconBack.vue'

    const getListUser = useListUser() 
    const signIn = useSignIn()

    const myRouter = useRouter()
    const goToViewUser = () => myRouter.push({name:'ViewUser'})
    const goThisUser = (newId) => myRouter.push({name: 'ThisUser', params:{userId:newId}})
    const goToError401 = () => myRouter.push({ name: 'Error401'})
    const goToError403 = () => myRouter.push({ name: 'Error403'})

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
      const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`,{
        method:'POST',
        headers:{
          'content-type':'application/json',
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        },
        body: JSON.stringify({
          userName:user.name.trim(),
          userEmail:user.email.trim(),
          userRole:user.role,
          userPassword:user.password
        })
      }).catch(error => console.log(error));
        if(res.status===200){
          const newId = await res.json()
          goThisUser(newId)
        }else if(res.status===400){
          alert("Cannot Create New User : The data is incorrect")
        }else if(res.status===414){
          alert("Cannot Create New User : The data length in the input field is too large. Please try again.")
        }else if(res.status===404){
          alert("Cannot Create New User : Not Found! User id")
        }else if(res.status===401){
          let errorText = await res.text()
          alert(errorText)
          if(errorText==="Token is expired."){
            await signIn.sendRefreshToken()
          }else{
            goToError401()
          }
        }else if(res.status===403){
          goToError403
        }else{
          alert("Error, Cannot Create New User")
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

    // - check length password
    const passwordStatus = ref(true) // true = Valid
    const checkPassword = (password) => {
      if(password.length < 8 ){
        passwordStatus.value = false 
      }else{
        passwordStatus.value = true
      }
    }
    
    // - check ว่า password = confrim password ไหม?
    const matchConfirmPassword = ref() // true = match
    const checkComfirmPassword = (pw1,pw2) => pw1 === pw2 ? matchConfirmPassword.value = true : matchConfirmPassword.value = false

</script>
 
<template>
  <div class="h-full">
    <div v-if="getListUser.loading" class="text-blue-800 my-16 text-center"><span v-if="getListUser.message=='loading...'"><IconLoading/></span><span v-else>{{getListUser.message}}</span></div>
    <div v-else class="bg-white float-right h-full w-2/4">
      <button @click="goToViewUser()" class="text-gray-400 m-5"><IconBack class="w-5 h-5 inline"/> Back</button>
      <div class="my-3 text-center text-3xl text-[#3333A3]"><b>Create User</b></div>
      <div class="mx-24 flex flex-col">
        <div class="my-3">
          <label for="username" class="w-full flex items-center justify-between">
            <IconAccount class="w-[20px] h-[20px] inline mr-2"/>
            <b class="flex-auto">Username</b>
            <span class="flex-auto text-right">{{newUser.name.length}} / 100</span>
          </label>
          <input type="text"
                   placeholder="Username" 
                   name="username" 
                   class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5 mt-1"
                   v-model="newUser.name" 
                   maxlength="100" 
                   size="50" 
                   @blur="checkMatchName(newUser.name)"
                   required
                   >
          <div v-show="matchNameStatus" class="text-red-500 text-right">&#9888; Username is already existed, please try another name</div>
        </div>
       
        <div class="my-3">
          <label for="email" class="w-full flex items-center justify-between">
            <IconEmail class="w-[20px] h-[20px] inline mr-2"/>
            <b class="flex-auto">Email</b>
            <span class="flex-auto text-right">{{newUser.email.length}} / 50</span>
          </label>
            <input type="text"
                   placeholder="Email" 
                   name="email" 
                   class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5 mt-1"
                   v-model="newUser.email" 
                   maxlength="50" 
                   size="50" 
                   @blur="emailValidation(newUser.email), checkMatchEmail(newUser.email)"
                   required
                   >
          <div v-show="emailStatus" class="text-red-500 text-right">&#9888; Input email is invalid!</div>
          <div v-show="matchEmailStatus" class="text-red-500 text-right">&#9888; This email is already existed, please try another email</div>
        </div>

        <div class="my-3">
          <label for="password" class="w-full flex items-center justify-between">
            <IconPassword class="w-[20px] h-[20px] inline mr-2"/>
            <b class="flex-auto">Password</b>
            <span class="flex-auto text-right">8 - 14 character</span>
          </label>
            <input type="password" 
                  placeholder="Password" 
                  name="password"
                  class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5 mt-1"
                  v-model="newUser.password" 
                  minlength="8" 
                  maxlength="14" 
                  size="50" 
                  @blur="checkPassword(newUser.password)" 
                  required>
          <div v-show="!passwordStatus" class="text-red-500 text-right">&#9888; At least 8 characters</div>
        </div>

        <div class="my-3">
        <label for="confrim">
            <IconKeyCheck class="w-[20px] h-[20px] inline mr-2"/>
            <b>Confirm Password</b>
        </label>
            <input type="password" 
                  placeholder="Comfirm password" 
                  name="confrim"
                  class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5 mt-1"
                  v-model="newUser.confirmPW" 
                  minlength="8" 
                  maxlength="14" 
                  size="50" 
                  @blur="checkComfirmPassword(newUser.password, newUser.confirmPW)" 
                  required>
          <div v-show="matchConfirmPassword === false" class="text-red-500 text-right">&#9888; The password DOES NOT match</div>
        </div>

        <div class="my-3">
          <label for="role">
            <IconUserRole class="w-[20px] h-[20px] inline mr-2"/>
            <b>Role</b>
          </label>
          <div class="mt-4">
            <div class="my-auto m-1 py-1.5 px-2 text-center rounded-lg border-2 inline">
              <input type="radio" id="1" name="role" value="admin" v-model="newUser.role" class="accent-[#F36747] align-middle w-4 h-4">
              <label for="1" class="ml-1 align-top">admin</label>
            </div>
            <div class="my-auto m-1 py-1.5 px-2 text-center rounded-lg border-2 inline">
              <input type="radio" id="2" name="role" value="lecturer" v-model="newUser.role" class="accent-pink-500 align-middle w-4 h-4">
              <label for="2" class="ml-1 align-top">lecturer</label>
            </div>
            <div class="my-auto m-1 py-1.5 px-2 text-center rounded-lg border-2 inline">
              <input type="radio" id="3" name="role" value="student" v-model="newUser.role" class="accent-violet-500 align-middle w-4 h-4" checked>
              <label for="3" class="ml-1 align-top">student</label>
            </div>
          </div>
        </div>

        <button type="submit" 
          class="bg-[#5C5CFF] text-white text-[18px] font-bold py-2 px-5 rounded-full
          hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300 mt-6"
          @click="createNewUser(newUser)" :disabled="checkBeforeAdd" style="width: 100%;">Create</button>
      </div>
    </div>
</div>
</template>
 
<style scoped>

</style>