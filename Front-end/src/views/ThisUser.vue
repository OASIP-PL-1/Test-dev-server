<script setup>
    import {ref, onBeforeMount} from 'vue'    
    import {useRoute, useRouter} from 'vue-router'
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    import cancel_icon from '../components/icons/cancel.vue'

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

    onBeforeMount(async () => {
        await getThisUser()
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

</script>
 
<template>
    <div style="margin-top: 10em;">
        <!-- <button @click="goBack" class="button-18" role="button">Back</button>&ensp; -->
        <div v-if="loading" class="subText" style="margin-top: 2em;">{{message}}</div>
        <div v-else-if="!showDetail" class="NotFoundText" style="margin-top: 2em;">
        -- Not Found Data of User ID : {{params.userId}} --
        </div>
        <div v-else class="modal-mask">
            <div class="modal-wrapper">
                <div class="modal-container">
                    <span class="close" @click="goToViewUser()"><cancel_icon/></span>
                    <div class="modal-header">
                        <h3>Account</h3>
                        <img :src="pathImg(thisUser.id)" alt="human" width="80"/>
                        <p><b>{{thisUser.userName}}</b></p>
                    </div>
                    <div class="modal-body">
                        <p><b>Email : </b>{{thisUser.userEmail}}</p>
                        <p><b>Role : </b>{{thisUser.userRole}}</p>
                        <p><b>Created on : </b>{{datetimeFormat.showDateTimeZone(new Date(thisUser.createdOn))}}</p>
                        <p><b>Updated on : </b>{{datetimeFormat.showDateTimeZone(new Date(thisUser.updatedOn))}}</p>
                    <div class="button-right">
                        <!-- <span v-show="checkDateTime">This event cannot be edited because it has passed.</span>&ensp; -->
                        <button :class="['button-18','negative']" role="button" @click="removeUser">Delete</button>  &ensp;  
                        <button class="button-18" role="button">Edit</button>
                    </div>  
                    </div>
                </div>
            </div>
                     
                <!-- Modal Delete -->
                    <!-- <div class="modal-mask" v-show=modalStatusDelete style="display:block">
                        <div class="modal-wrapper"> -->
                        <!-- Modal content -->
                            <!-- <div class="modal-container">
                                <span class="close" @click="hideDeleteModal()" >&times;</span>
                                <div class="modal-header">
                                    <h3>Do you want to delete this event ?</h3>
                                </div>
                                <div class="modal-button">
                                    <button @click="removeEvent()" :class="['button-18', 'confirmbt']">Confirm</button>
                                    &ensp;<button @click="hideDeleteModal()" class="button-18">Cancel</button>
                                </div>
                            </div>
                        </div>  
                    </div> -->
        </div>
        </div>
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
        margin: 0 10% 2em 0;
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
        width: 800px;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        margin: auto;
        text-align: center;
    }
    .modal-header h3 {
        color: rgb(0, 0, 0);
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