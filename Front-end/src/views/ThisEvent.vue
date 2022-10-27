<script setup>
    import {ref, onBeforeMount} from 'vue'
    import {useRoute, useRouter} from 'vue-router'
    import EditEvent from '../components/EditEvent.vue';
    import {useDatetimeFormat} from '../state/datetimeFormat.js'
    import { useSignIn } from '../state/signIn.js';

    const signIn = useSignIn()
    
    const {params} = useRoute()
    const datetimeFormat = useDatetimeFormat()

// --- Get all list --- 
    const thisEvent = ref({})
    const showDetail = ref({})
    const loading =ref()
    const message = ref()

    const getThisEvent = async () => {
        loading.value = true
        message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
    ).catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
    });
    console.log(res.status)
    if(res.status===200){
            thisEvent.value = await res.json()
            console.log(`GET This Event id: ${params.eventId} OK`)
            showDetail.value = true
        }else if(res.status===404){
            console.log(`Not Found! This Event id: ${params.eventId}`)
            showDetail.value = false
        }else if(res.status===401){
            console.log('Please login')
            goToError401()
        }else if(res.status===403){
            console.log('Unauthorized access')
            goToError403()
        }
        loading.value = false
    }

    onBeforeMount(async () => {
      await getThisEvent()
    })
// -- random image --
    const pathImg = `${import.meta.env.BASE_URL}humans/human${(params.eventId%8)+1}.png`

// --- Go back --- 
    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
    const goToViewEvent= () => myRouter.push({ name: 'ViewEvent'})
    const goToError401 = () => myRouter.push({ name: 'Error401'})
    const goToError403 = () => myRouter.push({ name: 'Error403'})

// --- GET List Overlap ---    
const listOverlap = ref([])
const selectedCategory = ref('')
const selectedDate = ref('')
    
const getListOverlap = async (editingEvent) => {
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/list-edit-overlap/${editingEvent.id}/${editingEvent.date}`
    ,{
        method: "GET",
        headers:{
          'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
        }
      }
    ).catch((error)=> console.log(error));
    console.log(res.status)
    if(res.status==200){
        listOverlap.value = await res.json()
        console.log(`-- Get List Overlap Times --`)
        console.log(listOverlap.value)
    }else if(res.status===404){
        console.log(`Not Found This Event id: ${params.eventId}`)
    }else if(res.status===401){
        console.log('Please login')
    }else if(res.status===403){
        console.log('Unauthorized access')
    }
    console.log(listOverlap.value)
    selectedCategory.value = thisEvent.value.categoryName
    selectedDate.value = datetimeFormat.showDate(new Date(editingEvent.date)).substring(4,15)
}

// --- Edit Mode ---
    const editMode = ref(false)
    const showEditMode = () => editMode.value = true
    const hideEditMode = () => {
        editMode.value = false
        overlapStatus.value = true
        listOverlap.value = []
    }

// --- Edit---
    const updateEvent = async (editingEvent)=>{
        const status = await checkOverlap(editingEvent)
        console.log(status)
        if(!status){
            // true เข้ามาในนี้ แปลว่า overlap 2 ใส่ไม่ได้
            console.log("Can't Edit Event : This event is Overlap!")
            await getListOverlap(editingEvent)
        }else{
            // false ไม่เข้า ส่งไป backend
            const dataTime = new Date(editingEvent.dateTime)
            if(editingEvent.notes !== null){
                if(editingEvent.notes.trim().length === 0){
                    editingEvent.notes = null
                }else{
                    editingEvent.notes = editingEvent.notes.trim()
                }
            }
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`, 
            {
                method:'PUT',
                headers:{
                'content-type':'application/json',
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
                },
                body: JSON.stringify({
                    id : editingEvent.id,
                    startTime: dataTime.toISOString().replace(".000Z", "Z"),
                    notes: editingEvent.notes
                })
            }).catch(error => console.log(error) );
            console.log(res.status)
            if(res.status===200){
                console.log('PUT This Event Updated')
                thisEvent.value = await res.json()
                console.log(thisEvent.value)         
                hideEditMode()
            }else if(res.status===400){
                console.log("Cannot Edit This Event : The data is incorrect")
            }else if(res.status===414){
                console.log("Cannot Edit This Event  : The data length in the input field is too large. Please try again.")
            }else if(res.status===404){
                console.log("Cannot Edit This Event : Not Found! Event id")
            }else if(res.status===401){
                console.log('Please login')
            }else if(res.status===403){
                console.log('Unauthorized access')
            }else{
                console.log("Error, Cannot Create New Event")
            }
        }
  }

//Check overlap----------------
    const overlapStatus = ref(true)
    const checkOverlap = async(editingEvent) => {
        const id = params.eventId
        const dateTime = new Date(editingEvent.dateTime).toISOString()
        console.log(dateTime)
        const dateFormat = dateTime.substring(0,10) + '-' + dateTime.substring(11,13) + '-' +dateTime.substring(14,16) + '-' + dateTime.substring(17,19) 
        console.log(dateFormat) //2022-05-26-04-00-00 (-7)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/edit/${id}/${dateFormat}`
        ,{
            method: "GET",
            headers:{
                'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }
        ).catch((error)=> {
                message.value = "Not Found Backend Server!!!"
                console.log(error)
            });
            console.log(res.status)
        if(res.status==200){
            overlapStatus.value = await res.json()
            console.log(`--- Check Overlap Status ---`)
        }else if(res.status===401){
            console.log('Please login')
        }else if(res.status===403){
            console.log('Unauthorized access')
        }
        return overlapStatus.value
    }
    
// --- Delete---
    const modalStatusDelete = ref(false)
    const showDeleteModal = () => {modalStatusDelete.value = true}
    const hideDeleteModal = () => {modalStatusDelete.value = false}

    const removeEvent = async () => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}` , {
            method: 'DELETE',
            headers:{
            'Authorization' : 'Bearer '+signIn.getCookie('accessToken')
            }
        }).catch(error => console.log(error) );
        console.log(res.status)
        if (res.status===200) {
            console.log('DELETE successfully')
            goToViewEvent()
        }else if(res.status===404){
            console.log(`Not Found! This Event id: ${params.eventId}`)
        }else if(res.status===401){
            console.log('Please login')
        }else if(res.status===403){
            console.log('Unauthorized access')
        }else{
            console.log('Error, Cannot Delete This Event')
        }
    }
</script> 

<template>
    <div style="margin-top: 10em;">
        <div class="thisEvent">
            <button @click="goBack" class="button-18" role="button">Back</button>&ensp;
            <div v-if="loading" class="subText" style="margin-top: 2em;">{{message}}</div>
            <div v-else-if="!showDetail" class="NotFoundText" style="margin-top: 2em;">
            -- Not Found Data --
            </div>

            <div v-else-if="!editMode">
                <div class="center">
                <div class="box">
                    <div class="header">
                        <div class="grid-container">
                            <span class="grid-item-pic">
                            <!-- <img src="/humans/human1.png" alt="human"> -->
                            <img :src="pathImg" alt="human">
                            </span>
                            <span class="grid-item">
                            <h3>Booking Name : {{thisEvent.bookingName}}</h3>
                            <b>Email :</b> {{thisEvent.bookingEmail}}
                            </span>
                        </div>
                    <hr>
                    </div>
                <table>
                    <tr>
                        <th>Category Name : </th>
                        <td>{{thisEvent.categoryName}}</td>
                        <th>Duration : </th>
                        <td>{{thisEvent.duration}} min.</td>
                    </tr> 
                    <tr>
                        <th>Date :</th>
                        <td>{{datetimeFormat.showDate(new Date(thisEvent.startTime))}} </td>
                        <th>Start Time :</th>
                        <td>{{datetimeFormat.showTime(new Date(thisEvent.startTime))}}</td>
                    </tr>
                    <tr>
                        <th></th>
                        <td></td>
                        <th>End Time : </th>
                        <td>{{datetimeFormat.showTime(datetimeFormat.addMinutes(new Date(thisEvent.startTime),thisEvent.duration))}}</td>
                    </tr>
                </table>
                <div class="header">
                    <div v-if="thisEvent.notes === null">
                        <b>Notes :</b> -
                    </div>
                    <div v-else>
                        <b>Notes :</b> <br>{{thisEvent.notes}}
                    </div>
            </div>
            </div>            
            </div>
                <div class="button-right">
                    <!-- <span v-show="checkDateTime">This event cannot be edited because it has passed.</span>&ensp; -->
                    <button @click="showDeleteModal()" :class="['button-18','negative']" role="button">Delete</button>  &ensp;  
                    <button @click="showEditMode()" class="button-18" role="button">Edit</button>
                </div>
            </div>

            <div v-else>
                <EditEvent :thisEvent="thisEvent"
                    :overlapStatus="overlapStatus"
                    :listOverlap="listOverlap"
                    :selectedCategory="selectedCategory"
                    :selectedDate="selectedDate"
                    @hideEditMode="hideEditMode"
                    @save="updateEvent"/>
            </div>

        <!-- Modal Delete -->
            <div class="modal-mask" v-show=modalStatusDelete style="display:block">
                <div class="modal-wrapper">
                <!-- Modal content -->
                    <div class="modal-container">
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
            </div>
        </div>
    </div>
</template>
 
<style scoped>
    h3 {
        color: #FFA21A;
    }
    b {
        color: #FFCB4C;
    }
    img {
        text-align: center;
        width: 100%;
        -o-object-fit: cover;
        object-fit: cover;
        margin-left: auto;
        margin-right: auto;
    }
    table {
        width: 70%;
        margin: 1em 3em;
        -o-object-fit: cover;
        object-fit: cover;
    }
    th {
        width: 30%;
        /* min-width: 125px; */
        text-align: right;
        padding: 0.25em 1em;
        color: #FFCB4C;
        -o-object-fit: cover;
        object-fit: cover;
    }
    td {
        width: 30%;
        /* min-width: 150px; */
        -o-object-fit: cover;
        object-fit: cover;
    }
    .subText{
        color: gray;
        text-align: center;
    }
    .center {
        margin: auto;
        width: 80%;
    }
    .header {
        margin: 1em 3em;
    }
    .box {
        background-color: #3333A3;
        padding: 1em 2em 3em 2em;
        border-radius: 30px;
        margin-top: 1em;
        margin-bottom: 1em;
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
    .button-right {
        float: right;
        margin: 0 10% 2em 0;
    }
    .thisEvent { 
        margin-left: 1em;
        margin-right: 1em;
        padding-left: 20px;
        padding-right: 20px;
    }
    .modal-mask {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        transition: opacity 0.3s ease;
    }
    .modal-wrapper {
        margin-top: 15em;
        vertical-align: middle; 
    }
    .modal-container {
        width: 400px;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        margin: auto;
        text-align: center;
    }
    .modal-header h3 {
        color: rgb(0, 0, 0);
        margin: 2em 0em;
    }
    .modal-body {
        margin: 20px 0;
    }
    .modal-button {
        display: flex;
        justify-content: end;
    }
    .close {
        color: #aaaaaa;
        float: right;
        font-size: 28px;
        font-weight: bold;
    }

    .close:hover,
    .close:focus {
        color: #000;
        text-decoration: none;
        cursor: pointer;
    }
    .grid-container {
        display: grid;
        grid-template-columns: 10% 90%;
        gap: 2em;
    }
    .grid-item-pic {
        -o-object-fit: cover;
        object-fit: cover;
        min-width: 100px;
    }
    .grid-item {
        -o-object-fit: cover;
        object-fit: cover;
        padding-left: 20px;
    }
    .confirmbt {
        background-color: #fd7038;
    }
    .confirmbt:hover:not([disabled]) {
        background-color: #FFA21A;
        color: white;
        transition-duration: .1s;
    }
</style>