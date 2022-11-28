<script setup>
    import {ref, onBeforeMount, onMounted} from 'vue'    
    import {useRoute, useRouter} from 'vue-router'
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    // import cancel_icon from '../components/icons/iconCancel.vue'
    import modalEditUser from '../components/modalEditUser.vue'
    import modalDeleteUser from '../components/modalDeleteUser.vue'
    import {useListUser} from '../state/getListUser.js'
    import {useSignIn} from '../state/signIn.js'

    import IconLoading from '../components/icons/iconLoading.vue'
    import IconMore from '../components/icons/iconMore.vue'
    import IconEdit from '../components/icons/iconEdit.vue'
    import IconDelete from '../components/icons/iconDelete.vue'
    import Dot from '../components/icons/dot.vue'
    

    const getListUser = useListUser() 

    const signIn = useSignIn()

    const {params} = useRoute()

    const datetimeFormat = useDatetimeFormat()

    const thisUser = ref({})
    const showDetail = ref({})
    const loading =ref()
    const message = ref()

    const getThisUser = async () => {
        loading.value = true
        message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users/${params.userId}`,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      })
        .catch((error)=> {
            console.log(error)
        });
        console.log(res.status)
        if(res.status===200){
            thisUser.value = await res.json()
            console.log(`GET This User id: ${params.userId} OK`)
            getListUser.getUserCheckListEdit(thisUser.value.id)
            showDetail.value = true
            loading.value = false
        }else if(res.status===404){
            // console.log(`Not Found! This User id: ${params.userId}`)
            showDetail.value = false
            message.value = `-- Not Found Data of User ID : ${params.userId} --`
        }else if(res.status===401){
            console.log('Please Login')
            goToViewUser()
        }else if(res.status===403){
            console.log('Unauthorized access')
            goToViewUser()
        }
        loading.value = false
    }
    
    onBeforeMount(async () => {
        await getThisUser()
    })

    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    const goToViewUser = () => myRouter.push({ name: 'ViewUser'})

    // random image user 
    const pathImg = (userId) => `${import.meta.env.BASE_URL}humans/human${userId%8+1}.png`

    // --- Edit Mode ---
    const editMode = ref(false)
    const showEditMode = () => editMode.value = true
    const hideEditMode = () => { editMode.value = false } 

    // UPDATE User 
    const updateUser = async (editingUser)=>{
        // check ว่าข้อมูลถูกแก้ไขหรือไม่?
        if(editingUser.userName===thisUser.value.userName 
            && editingUser.userEmail===thisUser.value.userEmail 
            && editingUser.userRole===thisUser.value.userRole)
            {// ถ้าไม่ได้แก้ไข จะไม่ส่งค่าไป updated
                console.log('This User save ok , not PUT')  
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
            goToViewUser()
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

    const menuToggle = () => {
    const toggleMenu = document.getElementById("toggle-edit")
        toggleMenu.classList.toggle('showToggle')
    }
</script>
 
<template>
    <!-- <div style="margin-top: 10em;"> -->
        <!-- <button @click="goBack" class="button-18" role="button">Back</button>&ensp; -->
        <!-- <div v-if="loading" class="subText" style="margin-top: 2em;">{{message}}</div>
        <div v-else-if="!showDetail" class="NotFoundText" style="margin-top: 2em;">
        -- Not Found Data of User ID : {{params.userId}} --
        </div>
        <div v-else-if="!editMode" class="modal-mask">
            <div class="modal-wrapper">
                <div class="modal-container">
                    <span class="close" @click="goToViewUser()"><cancel_icon/></span>
                    <div class="modal-header">
                        <h2>Account</h2>
                        <img :src="pathImg(thisUser.id)" alt="human" width="80"/>
                        <p><b>{{thisUser.userName}}</b></p>
                    </div>
                    <div class="modal-body">
                        <p><b>Email : </b>{{thisUser.userEmail}}</p>
                        <p><b>Role : </b>{{thisUser.userRole}}</p>
                        <p><b>Created on : </b>{{datetimeFormat.showDateTimeZone(new Date(thisUser.createdOn))}}</p>
                        <p><b>Updated on : </b>{{datetimeFormat.showDateTimeZone(new Date(thisUser.updatedOn))}}</p>
                        <br>
                        <span class="button-right">
                            <button :class="['button-18','negative']" role="button" @click="removeUser()">Delete</button>  &ensp;  
                            <button class="button-18" role="button" @click="showEditMode()">Edit</button>
                        </span>
                        <br>
                    </div>
                </div>
            </div>
        </div>
        <div v-else>
            <EditUser :thisUser="thisUser"
                :users="getListUser.userCheckListEdit"
                @hideEditMode="hideEditMode"
                @save="updateUser" />
        </div> -->

        <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
        <div v-else-if="!showDetail" class="text-red-600 my-16 text-center">{{message}}</div>
        <div v-else class="mx-10 w-auto mt-5">
            <div class="mb-4 font-semibold"><button @click="goToViewUser()" class="underline hover:text-yellow-300">User</button> / Detail </div>
            <div class="bg-white rounded-2xl flex flex-row mx-auto px-10 py-5 w-4/5 text-[16px]">
                <img :src="pathImg(thisUser.id)" alt="human" class="w-48 h-48 my-5 mx-auto"/>
                <div class="flex flex-col w-full mx-7 my-5">
                    <h3 class="font-semibold text-[20px]">{{thisUser.userName}}</h3>
                    <p class="py-1">{{thisUser.userEmail}}</p>
                    <div class="py-4">
                        <strong>role </strong>
                        <div class="my-auto mx-2 w-24 py-1 pl-1 pr-2 text-center rounded-md border-2 inline">
                            <Dot v-if="thisUser.userRole=='admin'"  class="w-7 h-7 inline text-red-500"/>
                            <Dot v-else-if="thisUser.userRole=='lecturer'" class="w-7 h-7 inline text-blue-500"/>
                            <Dot v-else-if="thisUser.userRole=='student'" class="w-7 h-7 inline text-yellow-400"/>
                            {{thisUser.userRole}}
                        </div>
                    </div>
                    <div class="flex flex-row my-3">
                    <div class="basis-1/2 px-1">
                        <h4>created on</h4>
                        <p>{{datetimeFormat.showDateTimeZone(new Date(thisUser.createdOn))}}</p>
                    </div>
                    <div class="basis-1/2 px-1">
                        <h4>updated on</h4>
                        <p>{{datetimeFormat.showDateTimeZone(new Date(thisUser.updatedOn))}}</p>
                    </div>
                </div>
                </div>

            <IconMore @click="menuToggle()" class="w-7 h-7 float-right text-gray-400" />
                <span id="toggle-edit" class="absolute top-[175px] right-[120px] py-2 bg-[#E3ECFC] w-28 box-border drop-shadow-md rounded-xl text-gray-700 
                            transition duration-500 opacity-0 invisible">
                    <div class="flex flex-col">
                        <div @click="showEditMode()" class="pr-4 py-2 inline hover:bg-white
                                    active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                            <IconEdit class="w-5 h-5 ml-5 mr-2 inline align-top"/>Edit
                        </div>
                        <div @click="showDeleteModal()" class="pr-4 py-2 inline hover:bg-white
                                    active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                            <IconDelete class="w-5 h-5 ml-5 mr-2 inline align-top"/>Delete
                    </div>
                    </div>
                </span>
            </div>
            
            <div v-show="editMode">
            <modalEditUser :thisUser="thisUser"
                :users="getListUser.userCheckListEdit"
                @hideEditMode="hideEditMode"
                @save="updateUser" />
            </div>

            <modalDeleteUser 
                :thisUser="thisUser"    
                :modalStatusDelete="modalStatusDelete"
                @hideDeleteModal="hideDeleteModal"
                @removeUser="removeUser"/>

        </div>

</template>
 
<style scoped>
h4{
    font-weight: bold;
    font-size: 14px;
    color: rgb(188, 188, 188);
    
}
.showToggle {
    visibility: visible;
    opacity: 1;
}
    /* .box {
        background-color: #3333A3;
        padding: 1em 2em 3em 2em;
        border-radius: 30px;
        margin-left: auto;
        margin-right: auto;
        color: white;
        -o-object-fit: cover;
        object-fit: cover;
    }
    
    .NotFoundText{
        color: gray;
        text-align: center;
    }
    .grid-item-pic {
        -o-object-fit: cover;
        object-fit: cover;
        min-width: 100px;
    }
    .button-right {
        float: right;
    }
    .modal-mask {
        display: block;
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
        max-width: 800px;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        margin: auto;
        text-align: center;
    }
    .modal-header h2 {
        color: #3333A3;
        margin: 1em;
        font-weight: bolder;
        text-align: center;
    }
    .modal-body {
        margin: 20px 0;
    }
    .modal-button {
        display: flex;
        justify-content: end;
    }
    .close{
        float: right;
    } */
</style>