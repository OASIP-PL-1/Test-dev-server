<script setup>
    import {ref, onBeforeMount, onMounted} from 'vue'    
    import {useRoute, useRouter} from 'vue-router'
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    import cancel_icon from '../components/icons/cancel.vue'
    import EditUser from '../components/EditUser.vue'
    import {useListUser} from '../state/getListUser.js'

    const getListUser = useListUser() 

    const {params} = useRoute()
    const datetimeFormat = useDatetimeFormat()

    const thisUser = ref({})
    const showDetail = ref({})
    const loading =ref()
    const message = ref()

    const getThisUser = async () => {
        loading.value = true
        message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users/${params.userId}`)
    .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
    });
        thisUser.value = await res.json()
        console.log(res.status)
        if(res.status===200){
            console.log(`GET This User id: ${params.userId} OK`)
            showDetail.value = true
        }else if(res.status===404){
            console.log(`Not Found! This User id: ${params.userId}`)
            showDetail.value = false
        }
        loading.value = false
    }
    //*****ย้ายไปใน state แล้ว */
    // -- get list All User for check unique name and email --
    // const userCheckList = ref()

    // const getUserCheckList = async () => {
    //     loading.value = true
    //     message.value = "loading..."
    //     const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`)
    //       .catch((error)=> {
    //         message.value = "Not Found Backend Server!!!"
    //         console.log(error)
    //         console.log('GET List All User Fail')
    //     });
    //     userCheckList.value = await res.json()
    //     userCheckList.value = userCheckList.value.filter((user) => user.id !== thisUser.value.id)
    //     loading.value = false
    //     if(res.status==200){
    //       console.log(`GET List All User OK`)
    //       console.log(res.status)
    //     }
    //   }

    onBeforeMount(async () => {
        await getThisUser()
        getListUser.getUserCheckListEdit(thisUser.value.id)
    })

    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    const goToViewUser = () => myRouter.push({ name: 'ViewUser'})

    // random image user 
    const pathImg = (userId) => `/humans/human${userId%8+1}.png`

    // DELETE User
    const removeUser = async () => {
        if(confirm("Are you sure delete this account?")==true){
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users/${thisUser.value.id}` , {
                method: 'DELETE'
            }).catch(error => console.log(error) );
            console.log(res.status)
            if (res.status===200) {
                console.log('DELETE successfully')
                goBack()
            }else if(res.status===404){
                console.log(`Not Found! This User id: ${thisUser.value.id}`)
            }
            else{
                console.log('Error, Cannot Delete This User')
                console.log(res.status)
            }
        }
    }

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
                'content-type':'application/json'
                },
                body: JSON.stringify({
                    id : editingUser.id,
                    userName: editingUser.userName.trim(),
                    userEmail: editingUser.userEmail.trim(),
                    userRole: editingUser.userRole,
                })
            }).catch(error => console.log(error) );
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
            }
            else if(res.status===404){
                console.log("Cannot Edit This User : Not Found! User id")
            }
            else{
                console.log("Error, Cannot Update This User")
                }
            }
        }
</script>
 
<template>

    <!-- <div style="margin-top: 10em;"> -->
        <!-- <button @click="goBack" class="button-18" role="button">Back</button>&ensp; -->
        <div v-if="loading" class="subText" style="margin-top: 2em;">{{message}}</div>
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
                            <!-- <span v-show="checkDateTime">This event cannot be edited because it has passed.</span>&ensp; -->
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
        </div>
        <!-- </div> -->
</template>
 
<style scoped>
    .box {
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
        /* margin: 0 10% 2em 0; */
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
        /* width: 800px; */
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
    }
</style>