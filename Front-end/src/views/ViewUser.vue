<script setup>
    import {ref, onMounted} from 'vue'
    import {useListUser} from '../state/getListUser.js'
    import {useRoute, useRouter} from 'vue-router'
import ThisUserVue from './ThisUser.vue';

    const getListUser = useListUser()

    // ย้าย function ไปทำ state
//     const users = ref()
//     const loading =ref()
//     const message = ref()

//     const getUsers = async () => {
//     loading.value = true
//     message.value = "loading..."
//     const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`)
//       .catch((error)=> {
//         message.value = "Not Found Backend Server!!!"
//         console.log(error)
//         console.log('GET List All User Fail')
//     });
//     users.value = await res.json()
//     loading.value = false
//     if(res.status==200){
//       console.log(`GET List All User OK`)
//       console.log(res.status)
//     }
//   }

    onMounted(async () => {
        await getListUser.getUsers()
  })
    // random image user 
    const pathImg = (userId) => `/humans/human${userId%8+1}.png`

    const myRouter = useRouter()
    const goToThisUser = (id) => myRouter.push({ name: 'ThisUser', params:{userId:id}})
</script>
 
<template>
    <div style="margin-top: 8em;">
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
                            <!-- <button class="button-18">
                                 <router-link :to="{ name: 'ThisUser', params:{userId:user.id}}" >Detail</router-link>
                            </button> -->
                            <button class="button-18" @click="goToThisUser(user.id)">Detail</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</template>
 
<style scoped>
    p {
        color: black;
    }
    .boxRow{
        margin: 1em;
    }
    .boxUser {
        background-color:#b5b5f7;
        padding: 1em 2em 1em 2em;
        border-radius: 30px;
        /* min-height: 100px; */
        /* min-width: 200px; */
        box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
        text-align: center;
        -o-object-fit: cover;
        object-fit: cover;
        margin-left: auto;
        margin-right: auto;
        max-width: 800px;
        /* min-width: auto; */
        color: white;
    }
    .grid-item-pic {
        -o-object-fit: cover;
        object-fit: cover;
        min-width: 100px;
    }
    .center {
        text-align: center;
    }
    .subText {
        color: gray;
        text-align: center;
    }
    a:link { text-decoration: none; }
</style>