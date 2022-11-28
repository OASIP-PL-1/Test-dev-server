<script setup>
    import {ref, computed, onMounted} from 'vue'
    import {useListUser} from '../state/getListUser.js'
    import {useSignIn} from '../state/signIn.js'
    import {useRouter} from 'vue-router'

    import IconLoading from '../components/icons/iconLoading.vue'
    import IconDetail from '../components/icons/iconDetail.vue'
    import IconEdit from '../components/icons/iconEdit.vue'
    import IconDelete from '../components/icons/iconDelete.vue'
    import IconDeleteAlert from '../components/icons/iconDeleteAlert.vue'
    import IconCancel from '../components/icons/iconCancel.vue'
    import Dot from '../components/icons/dot.vue'
    import modalDeleteUser from '../components/modalDeleteUser.vue'

    const getListUser = useListUser()
    const userCheckList = ref()
    const users = ref()

    const signIn = useSignIn()

    onMounted(async () => {
        await getListUser.getUsers()
        users.value = getListUser.users
  })
    // random image user 
    const pathImg = (userId) => `${import.meta.env.BASE_URL}humans/human${userId%8+1}.png`

    const myRouter = useRouter()
    const goToThisUser = (id) => myRouter.push({ name: 'ThisUser', params:{userId:id}})
    const goToSignUp = () => myRouter.push({name: 'SignUp'})

    // -- Edit Mode --
    const editMode = ref(false)
    const thisUser = ref({id:0})
    const editingUser = ref({id:0})
    const hideEditMode = () => {editMode.value=false}
    const showEditMode = () => {editMode.value=true}
    const editUser = async (user)=>{
        thisUser.value =user
        editingUser.value = {
            id : user.id,
            userName : user.userName,
            userEmail :user.userEmail,
            userRole : user.userRole 
        }
        await getListUser.getUserCheckListEdit(user.id)
        userCheckList.value = getListUser.userCheckListEdit
        showEditMode()
    }

    //Validate Check
    const checkData = computed(()=>{
        if(editMode.value==true){ 
            return editingUser.value.userName.trim().length === 0 
                || editingUser.value.userEmail.trim().length === 0
                || emailStatus.value || matchNameStatus.value || matchEmailStatus.value
        }
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

    const updateUser = async (editingUser)=>{
        // check ว่าข้อมูลถูกแก้ไขหรือไม่?
        if(editingUser.userName===thisUser.value.userName 
            && editingUser.userEmail===thisUser.value.userEmail 
            && editingUser.userRole===thisUser.value.userRole)
            {// ถ้าไม่ได้แก้ไข จะไม่ส่งค่าไป updated
                console.log(`User id ${editingUser.id} save ok , not PUT`)  
                hideEditMode() 
        }else{
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`, 
            {
                method:'PUT',
                headers:{
                'content-type':'application/json',
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
                },
                body: JSON.stringify({
                    id : editingUser.id,
                    userName: editingUser.userName.trim(),
                    userEmail: editingUser.userEmail.trim(),
                    userRole: editingUser.userRole,
                })
            }).catch(error => console.log(error));
            console.log(res.status)
            if(res.status===200){
                console.log('PUT This User Updated')
                thisUser.value = await res.json()
                console.log(thisUser.value)
                users.value = users.value.map((user) => user.id === thisUser.value.id ? 
                {...user, 
                    id : thisUser.value.id,
                    userName: thisUser.value.userName,
                    userEmail: thisUser.value.userEmail,
                    userRole: thisUser.value.userRole,
                } : user 
                )
                hideEditMode()
            }else if(res.status===400){
                console.log("Cannot Edit This User : The data is incorrect")
            }else if(res.status===414){
                console.log("Cannot Edit This User  : The data length in the input field is too large. Please try again.")
            }else if(res.status===404){
                console.log("Cannot Edit This User : Not Found! User id")
            }else if(res.status===401){
                console.log('Please login')
            }else if(res.status===403){
                console.log('Unauthorized access')
            }else{
                console.log("Error, Cannot Update This User")
                }
            }
        }

    // DELETE User
    const modalStatusDelete = ref(false)
    const showDeleteModal = () => {modalStatusDelete.value = true}
    const hideDeleteModal = () => {modalStatusDelete.value = false}
    const deleteUser = (user) => {
        thisUser.value = user
        showDeleteModal()
    }

    const removeUser = async () => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users/${thisUser.value.id}` , {
            method: 'DELETE',
            headers:{
            'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch(error => console.log(error) );
        console.log(res.status)
        if (res.status===200) {
            console.log('DELETE successfully')
            await getListUser.getUsers()
            users.value = getListUser.users
            hideDeleteModal()
        }else if(res.status===404){
            console.log(`Not Found! This User id: ${thisUser.value.id}`)
        }else if(res.status===401){
            console.log('Please login')
        }else if(res.status===403){
            console.log('Unauthorized access')
        }else{
            console.log('Error, Cannot Delete This User')
        }
    }


</script>
 
<template>
    <!-- <div style="margin-top: 8em;">
        <div v-if="getListUser.loading" class="subText" style="margin-top: 2em;">{{getListUser.message}}</div>
        <div v-else-if="getListUser.users.length === 0" class="center">-- No User--</div>
        <div v-else>
            <div v-for="(user,index) in getListUser.users" :key="index" class="boxRow">
                <table class="boxUser">
                    <tr>
                        <td style="text-align: center; width: 20%;">
                            <span class="grid-item-pic">
                                <img :src="pathImg(user.id)" alt="human" width="80">
                            </span>
                        </td>
                        <td style="text-align: left; width: 60%; min-width: 500px; padding: 1em;">
                            <b style="font-size: larger;">{{user.userName}} </b><br/>
                            {{user.userEmail}}
                        </td>
                        <td style="text-align: left; width: 10%; min-width: 50px; padding: 1em;">
                            {{user.userRole}}
                        </td>
                        <td style="text-align: right; width: 10%;">
                            <button class="button-18" @click="goToThisUser(user.id)">Detail</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div> -->
    <div v-if="getListUser.loading" class="text-blue-800 my-16 text-center">
        <span v-if="getListUser.message=='loading...'"><IconLoading/></span><span v-else>{{getListUser.message}}</span>
    </div>
    <div v-else-if="getListUser.users.length === 0" class="text-red-600 my-16 text-center">-- No User --</div>
    <div v-else class="w-auto px-10 mt-5">
        <div class="mb-4 font-semibold flex justify-between">
            Total {{getListUser.users.length}} users 
            <button @click="goToSignUp()"
                    class="my-auto mx-5 py-2 px-5 text-center rounded-full border-[#3333A3] border-2 bg-[#3333A3] text-white
                    hover:bg-[#ECECFE] hover:text-[#3333A3] duration-300">
                    + Create User
            </button>
        </div>
        <div v-for="(user,index) in users" :key="index"
            class="bg-white rounded-lg flex flex-row mx-auto my-2 pl-10 py-2 w-5/6 shadow-md">
            <div class="basis-3/4 flex flex-row">
                <img :src="pathImg(user.id)" alt="human" class="w-16 h-16"/>
                <div class="my-auto px-5 w-3/5">
                    <h3 class="text-[16px] font-semibold">{{user.userName}}</h3>
                    <p>{{user.userEmail}}</p>
                </div>
                <div class="my-auto mx-3 w-24 p-0.5  text-center rounded-md border-2">
                    <Dot v-if="user.userRole=='admin'"  class="w-5 h-5 inline text-red-500"/>
                    <Dot v-else-if="user.userRole=='lecturer'" class="w-5 h-5 inline text-blue-500"/>
                    <Dot v-else-if="user.userRole=='student'" class="w-5 h-5 inline text-yellow-400"/>
                    {{user.userRole}}
                </div>
            </div>
            <div class="basis-1/4 px-4 border-l-2 flex flex-row text-center text-[12px]">
                <button @click="goToThisUser(user.id)" class="text-gray-400 hover:text-[#3333A3] duration-150 px-3 py-1">
                    <IconDetail class="w-5 h-5 inline mx-1"/>detail</button>
                <button @click="editUser(user)" class="text-gray-400 hover:text-[#3333A3] duration-150 px-3 py-1">
                    <IconEdit class="w-5 h-5 inline mx-1"/>edit</button>
                <button @click="deleteUser(user)" class="text-gray-400 hover:text-[#3333A3] duration-150 px-3 py-1">
                    <IconDelete class="w-5 h-5 inline mx-1"/>delete</button>
            </div>
        </div>

    <!-- EDIT MODAL -->
        <div v-show="editMode" class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
            <div class="bg-white w-[700px] m-auto mt-32 py-3 px-5 text-left rounded-xl">
            <h2 class="text-lg font-semibold pb-2 border-b-2 border-gray-400">
                Edit User
            <button class="float-right" @click="hideEditMode()"><IconCancel class="w-5 h-5"/></button>
            </h2>
            <div class="flex flex-row m-3">
            <img :src="pathImg(editingUser.id)" alt="human" class="w-32 h-32 my-2 mx-auto"/>
            <div class="flex flex-col ml-10 mt-3">
                <div class="my-2">
                <h4>username</h4>
                <input type="text" id="username" name="username" 
                        v-model="editingUser.userName" maxlength="100" size="50"  @blur="checkMatchName(editingUser.userName)" required
                        class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3]">
                <span v-show="matchNameStatus" class="text-red-500 block">Username is already existed, please try another name</span>
                </div>
                <div class="my-2">
                <h4>email</h4>
                <input type="text" id="useremail" name="useremail" 
                        v-model="editingUser.userEmail" maxlength="50" size="50"  
                        @blur="emailValidation(editingUser.userEmail), checkMatchEmail(editingUser.userEmail)" required
                        class="bg-[#E3ECFC] p-1 rounded-sm text-[#3333A3]">
                    <span v-show="emailStatus" class="text-red-500 block">Input email is invalid!</span>
                    <span v-show="matchEmailStatus" class="text-red-500 block">This email is already existed, please try another email</span>
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
                <button @click="hideEditMode()" role="button"
                        class="bg-red-100 text-red-500 py-1.5 px-4 rounded-full 
                                hover:bg-red-500 hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                        &ensp;
                <button @click="updateUser(editingUser)"  role="button" :disabled="checkData"
                                class="bg-[#5C5CFF] text-white py-1.5 px-4 rounded-full 
                                    hover:bg-[#FFA21A] active:bg-[#3333A3] duration-300 disabled:bg-gray-300">Save</button>
                </div>
                
            </div>
            </div>
            </div>
        </div>

    <!-- DELETE -->
        <div v-show="modalStatusDelete" class="bg-black w-full h-full bg-opacity-30 fixed top-0 left-0 block">
            <div class="bg-white w-[500px] py-5 px-7 m-auto mt-48 rounded-xl">
                <button @click="hideDeleteModal()" class="float-right"><IconCancel class="w-5 h-5"/></button>
                <div class="flex flex-row m-3">
                    <IconDeleteAlert class="w-16 h-16 mr-5 inline align-top text-red-400"/>
                    <h3 class="text-[20px] font-semibold ">The data connected to this account will be deleted.</h3>
                </div>
                <hr/>
                <p class="my-2 mx-5">Are you sure you want to delete this account?</p>
                <div class="border-2 rounded-xl flex flex-row px-5 py-2 mt-3 mb-5">
                    <img :src="pathImg(thisUser.id)" alt="human" class="w-16 h-16"/>
                    <div class="my-auto px-5">
                        <h3 class="font-semibold">{{thisUser.userName}}</h3>
                        <p>{{thisUser.userEmail}}</p>
                    </div>
                </div>

                <div class="text-right">
                    <button @click="hideDeleteModal()" class="bg-[#E3ECFC] text-[#5C5CFF] py-1.5 px-4 rounded-full 
                            hover:bg-[#5C5CFF] hover:text-white active:bg-[#3333A3] duration-300">Cancel</button>
                    &ensp;
                    <button @click="removeUser()" class="bg-red-400 text-white py-1.5 px-4 rounded-full 
                            hover:bg-red-500 active:bg-red-600 duration-300">Confirm</button>
                </div>
            </div>
        </div>


    </div>
</template>
 
<style scoped>
   
</style>