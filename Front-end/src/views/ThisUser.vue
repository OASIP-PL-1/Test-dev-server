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
      }).catch((error)=> {console.log(error)});
        if(res.status===200){
            thisUser.value = await res.json()
            getListUser.getUserCheckListEdit(thisUser.value.id)
            showDetail.value = true
            loading.value = false
        }else if(res.status===404){
            showDetail.value = false
            message.value = `-- Not Found Data of User ID : ${params.userId} --`
        }else if(res.status===401){
            alert('Please Login')
            goToViewUser()
        }else if(res.status===403){
            alert('Unauthorized access')
            goToViewUser()
        }
        loading.value = false
    }
    
    onBeforeMount(async () => {
        await getThisUser()
    })

    const myRouter = useRouter()
    const goToViewUser = () => myRouter.push({ name: 'ViewUser'})
    const goToError401 = () => myRouter.push({ name: 'Error401'})
    const goToError403 = () => myRouter.push({ name: 'Error403'})

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
            && editingUser.userRole===thisUser.value.userRole){ hideEditMode() }
        else{
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
            if(res.status===200){
                thisUser.value = await res.json()
                hideEditMode()
            }else if(res.status===400){
                alert("Cannot Edit This User : The data is incorrect")
            }else if(res.status===414){
                alert("Cannot Edit This User  : The data length in the input field is too large. Please try again.")
            }else if(res.status===404){
                alert("Cannot Edit This User : Not Found! User id")
            }else if(res.status===401){
                goToError401()
            }else if(res.status===403){
                goToError403()
            }else{
                alert("Error, Cannot Update This User")
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
        if (res.status===200) {
            goToViewUser()
        }else if(res.status===404){
            alert(`Not Found! This User id: ${thisUser.value.id}`)
        }else if(res.status===401){
            goToError401()
        }else if(res.status===403){
            goToError403()
        }else{
            alert('Error, Cannot Delete This User')
        }
    }

    const menuToggle = () => {
    const toggleMenu = document.getElementById("toggle-edit")
        toggleMenu.classList.toggle('showToggle')
    }
</script>
 
<template>
        <div v-if="loading" class="text-blue-800 my-16 text-center"><span v-if="message=='loading...'"><IconLoading/></span><span v-else>{{message}}</span></div>
        <div v-else-if="!showDetail" class="text-red-600 my-16 text-center">{{message}}</div>
        <div v-else class="mx-10 w-auto mt-5">
            <div class="mb-4 font-semibold"><button @click="goToViewUser()" class="underline hover:text-yellow-300">User</button> / Detail </div>
            <div class="bg-white rounded-lg flex flex-row mx-auto py-5 px-7 w-4/5 text-[16px]">
                <img :src="pathImg(thisUser.id)" alt="human" class="w-48 h-48 my-5 mx-auto"/>
                <div class="flex flex-col w-full mx-7 my-5">
                    <h3 class="font-semibold text-[20px]">{{thisUser.userName}}</h3>
                    <p class="py-1">{{thisUser.userEmail}}</p>
                    <div class="py-4">
                        <strong class="text-[#3333A3]">Role</strong>
                        <div class="my-auto mx-3 w-24 p-0.5 pr-2 text-center rounded-lg border-2 inline">
                            <Dot v-if="thisUser.userRole=='admin'"  class="w-5 h-5 inline text-[#F36747]"/>
                            <Dot v-else-if="thisUser.userRole=='lecturer'" class="w-5 h-5 inline text-pink-500"/>
                            <Dot v-else-if="thisUser.userRole=='student'" class="w-5 h-5 inline text-violet-500"/>
                            {{thisUser.userRole}}
                        </div>
                    </div>
                    <div class="flex flex-row my-3">
                    <div class="basis-1/2">
                        <h4>Created on</h4>
                        <p>{{datetimeFormat.showDateTimeZone(new Date(thisUser.createdOn))}}</p>
                    </div>
                    <div class="basis-1/2">
                        <h4>Updated on</h4>
                        <p>{{datetimeFormat.showDateTimeZone(new Date(thisUser.updatedOn))}}</p>
                    </div>
                </div>
                </div>

            <IconMore @click="menuToggle()" class="w-7 h-7 float-right text-gray-400" />
                <span id="toggle-edit" class="absolute top-[175px] right-[150px] py-2 bg-[#FFF] w-28 box-border drop-shadow-md rounded-lg text-gray-700 
                            transition duration-500 opacity-0 invisible">
                    <div class="flex flex-col">
                        <div @click="showEditMode()" class="pr-4 py-1 inline hover:bg-[#ECECFE]
                                    active:text-[#FFCB4C] hover:text-[#3333A3] hover:underline">
                            <IconEdit class="w-5 h-5 ml-5 mr-2 inline align-top"/>Edit
                        </div>
                        <div @click="showDeleteModal()" class="pr-4 py-1 inline hover:bg-[#ECECFE]
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
    color: #3333A3;
    
}
.showToggle {
    visibility: visible;
    opacity: 1;
}
</style>