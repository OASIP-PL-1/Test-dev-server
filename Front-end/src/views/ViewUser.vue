<script setup>
    import {ref, onMounted} from 'vue'

    const users = ref()
    const loading =ref()
    const message = ref()

    const getUsers = async () => {
    loading.value = true
    message.value = "loading..."
    const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`)
      .catch((error)=> {
        message.value = "Not Found Backend Server!!!"
        console.log(error)
        console.log('GET List All User Fail')
    });
    users.value = await res.json()
    loading.value = false
    if(res.status==200){
      console.log(`GET List All User OK`)
      console.log(res.status)
    }
  }

    onMounted(async () => {
      await getUsers()
  })
    // random image user 
    const pathImg = (userId) => `/humans/human${userId%8+1}.png`
</script>
 
<template>
    <div style="margin-top: 8em;">
        <div v-if="loading" class="subText" style="margin-top: 2em;">{{message}}</div>
        <div v-else-if="users == 0" class="center">-- No User--</div>
        <div v-else v-for="(user,index) in users" :key="index">
            
                <table class="boxUser">
                    <tr>
                        <td style="text-align: center; width: 20%;">
                            <span class="grid-item-pic">
                            <img :src="pathImg(user.id)" alt="human" width="80">
                            </span>
                        </td>
                        <td style="text-align: left; width: 50%; ">
                            <b style="font-size: larger;">{{user.userName}} </b><br/>
                            {{user.userEmail}}
                        </td>
                        <td style="text-align: left; width: 20%;">
                            {{user.userRole}}
                        </td>
                        <td style="text-align: center; width: 10%;">
                            <router-link :to="{ name: 'ThisUser', params:{userId:user.id}}" class="button-18">Detail</router-link>
                        </td>
                    </tr>
                </table>
        </div>
    </div>
</template>
 
<style scoped>
    p {
        color: black;
    }
    .boxUser {
        background-color:#b5b5f7;
        padding: 20px 40px;
        border-radius: 30px;
        min-height: 100px;
        min-width: 600px;
        box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
        margin: 1em 2em;
        /* text-align: center; */
        -o-object-fit: cover;
        object-fit: cover;
        text-align: center;
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
    table{
        width: auto;
        margin-left: auto;
        margin-right: auto;
        color: white;
    }
</style>