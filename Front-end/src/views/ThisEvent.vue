<script setup>
    import { computed } from '@vue/reactivity';
    import {ref, onBeforeMount} from 'vue'
    import {useRoute, useRouter} from 'vue-router'
    import EditEvent from '../components/EditEvent.vue';

    const {params} = useRoute()

// --- Get all list --- 
    const thisEvent = ref({})
    const showDetail = ref({})
    const loading =ref()
    const message = ref()

    const getThisEvent = async () => {
        loading.value = true
        message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}`)
    .catch(()=> {
        message.value = "Not Found Backend Server!!!"
    });
        thisEvent.value = await res.json()
        console.log(res.status)
        if(res.status===200){
            showDetail.value = true
        }else if(res.status===404){
            showDetail.value = false
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
    // const goNext = () => {
    //     myRouter.push({ name: 'ThisEvent', params:{eventId:params.eventId++}})
    //     getThisEvent()
    // }


// --- show Date --- (Sun 1 Jan 2022)
    const days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
    const months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sep','Oct','Nov','Dec']
    const a = new Date('2022-05-04')
    const showDate = (givenDate) => {
        // console.log(givenDate)
        const day = days[givenDate.getDay()]
        const date = givenDate.getDate()
        const month = months[givenDate.getMonth()]
        const year = givenDate.getFullYear()
        return day + ' ' + date + ' ' + month + ' ' + year
    }
// --- show Time --- (16:30)
    const showTime = (givenDate) => {
    return givenDate.toLocaleTimeString('th-TH').substring(0,5)
}
// --- calculate End time --- 
    const addMinutes = (date,duration) => {
        const changeDate = date
        changeDate.setMinutes(changeDate.getMinutes()+duration)
        return changeDate
    }

// --- Edit Mode ---
    const editMode = ref(false)
    const showEditMode = () => editMode.value = true
    const hideEditMode = () => editMode.value = false
    // const checkDateTime = computed(() => new Date(thisEvent.value.startTime) < new Date())

// --- Edit---
    const updateEvent = async (editingEvent)=>{
        const status = await checkOverlap(editingEvent)
        console.log(status)
        console.log(editingEvent)
        if(!status){
            // true เข้ามาในนี้ แปลว่า overlap 2 ใส่ไม่ได้
            console.log("This event is overlap")
        }else{
            // false ไม่เข้า ส่งไป backend
            const dataTime = new Date(editingEvent.dateTime)
            if(editingEvent.notes !== null){
                if(editingEvent.notes.length === 0){
                    editingEvent.notes = null
                }
            }
            const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events`, 
            {
                method:'PUT',
                headers:{
                'content-type':'application/json'
                },
                body: JSON.stringify({
                    id : editingEvent.id,
                    startTime: dataTime.toISOString().replace(".000Z", "Z"),
                    notes: editingEvent.notes
                })
            }).catch(error => console.log(error) );
            if(res.status===200){
                console.log('edited successfully')
                thisEvent.value = await res.json()
                console.log(thisEvent.value)         
                hideEditMode()
            }else if(res.status===400){
                console.log("This event is overlap")
                console.log(res.message)
                overlapStatus.value = false
            }else{
                console.log('error, cannot update')
                console.log(res.status)
            }
        }
  }

//Check overlap----------------
    const overlapStatus = ref(true)
    const checkOverlap = async(editingEvent) => {
        const id = params.eventId
        const date = new Date(editingEvent.dateTime)
        const dateFormat = date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate() + "-"
            + date.getHours() + "-" + date.getMinutes() + "-" + date.getSeconds()
        console.log(date)
        console.log(dateFormat)
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/edit/${id}/${dateFormat}`)
            .catch(()=> {
                message.value = "Not Found Backend Server!!!"
            });
        overlapStatus.value = await res.json()
        // console.log(await res.json())
        console.log( overlapStatus.value)
        return overlapStatus.value
    }
    
// --- Delete---
    const modalStatusDelete = ref(false)
    const showDeleteModal = () => {modalStatusDelete.value = true}
    const hideDeleteModal = () => {modalStatusDelete.value = false}

    const removeEvent = async () => {
        const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events/${params.eventId}` , {
            method: 'DELETE'
        })
        if (res.status===200) {
            console.log('deleted successfully')
            goToViewEvent()
        } else console.log('error, cannot delete')
    }


    
</script> 

<template>
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
                    <td>{{showDate(new Date(thisEvent.startTime))}} </td>
                    <th>Start Time :</th>
                    <td>{{showTime(new Date(thisEvent.startTime))}}</td>
                </tr>
                <tr>
                    <th></th>
                    <td></td>
                    <th>End Time : </th>
                    <td>{{showTime(addMinutes(new Date(thisEvent.startTime),thisEvent.duration))}}</td>
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
                <button @click="showEditMode()" class="button-18" role="button">Edit</button> &ensp;
                <button @click="showDeleteModal()" class="button-18" role="button">Delete</button>     
            </div>
        </div>

        <div v-else>
            <EditEvent :thisEvent="thisEvent"
                :overlapStatus="overlapStatus"
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
                    <button @click="removeEvent()" class="button-18">Confirm</button>&ensp;
                    <button @click="hideDeleteModal()" class="button-18" style="background-color: orangered;">Cancel</button>
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
        /* min-width: 600px; */
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
        width: 300px;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 10px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        margin: auto;
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
</style>