<script setup>
    import icon_Menu from './components/icons/menu.vue'
    import {ref, computed} from 'vue'
    import {useRouter} from 'vue-router'
    
    import './style.css'
    import {useSignIn} from './state/signIn.js'

    const signIn = useSignIn()
    signIn.checkLogin()

    const myRouter = useRouter()
    const gotoThisUser = () => myRouter.push({ name: 'ThisUser', params:{userId:signIn.user.id}})
<<<<<<< HEAD
    const gotoHome = () => myRouter.push({name: 'Home'})
=======
    // const gotoHome = () => myRouter.push({name: 'Home'})
    // const user = ref({
    //     username:localStorage.getItem('userName'),
    //     userRole:localStorage.getItem('userRole')
    // })
>>>>>>> 61aab8d9fcb43cec6b8c0b338a83380dc2465ba6

    // gotoHome()

    const menuToggle = () => {
        const toggleMenu = document.querySelector(".menu");
        toggleMenu.classList.toggle("active");
      }
<<<<<<< HEAD

    
=======
    // const removeStorage = () => {
    //     if(confirm("Are you sure want to Logout?") == true){
    //         signIn.removeCookie('accessToken')
    //         signIn.removeCookie('refreshToken')
    //         signIn.removeCookie('userName')
    //         signIn.removeCookie('userRole')
    //         signIn.statusLogin = false
    //         signIn.username = ""
    //         signIn.role = ""
    //         gotoHome()
    //     }
    // }
>>>>>>> 61aab8d9fcb43cec6b8c0b338a83380dc2465ba6
</script>
 
<template>
    <div class="bg-home" >
        <div class="navbar">
            <span class="navtop" style="display: inline;">
                <span class="navhover">
                    <router-link :to="{name:'Home'}">
                        <img src="./assets/logo.png" alt="logo"
                        style="float: left; padding: 10px 0 10px 30px; max-width: 450px; min-width: 250px; width: 10%; display: inline;">
                    </router-link>
                    <span style="color: #FFCB4C; font-size: 10px; float: left;">v1.1.28.20</span>
                    <span><p style="float:right;" v-show="signIn.statusLogin===true">
                        <div class="action">
                            <div class="profile" @click="menuToggle();">
                                <img src="./assets/icons/account.png"/>
                            </div>
                            <span class="menu">
                            <h3><b>{{signIn.user.name}}</b></h3>
                                <ul>
                                    <li>
                                        <img src="./assets/icons/profile.png" /><a @click="gotoThisUser(), menuToggle()">My profile</a>
                                    </li>
                                    <li>
                                        <img src="./assets/icons/editUser.png" /><a @click="gotoThisUser(), menuToggle()">Edit profile</a>
                                    </li>
                                    <li>
                                        <img src="./assets/icons/logout.png" /><a @click="signIn.Logout()">Logout</a>
                                    </li>
                                </ul>
                            </span>
                        </div>
                        </p>
                    </span>
                    <span class="dropdown">
                        <p style="float:right;" class="dropbtn"><icon_Menu/></p>
                        <span class="dropdown-content">
                            <a @click="signIn.Logout()" v-if="signIn.statusLogin===true" ><p style="float:right;">Logout</p></a>
                            <router-link :to="{name:'LoginUser'}" v-else><p style="float:right;">Login</p></router-link>
                            <router-link :to="{name:'CreateUser'}"><p style="float:right;">Sign Up</p></router-link>
                            <router-link :to="{name:'ViewEvent'}"><p style="float:left;">Event</p></router-link>
                            <router-link :to="{name:'ViewEventCategory'}"><p style="float:left;">Category</p></router-link>
                            <router-link :to="{name:'AddNewEvent'}"><p style="float:left;">Book</p></router-link>
                            <router-link :to="{name:'ViewUser'}"><p style="float:left;">User</p></router-link>
                            <router-link :to="{name:'About'}"><p style="float:left;">About</p></router-link>
                        </span>
                    </span> 
                </span>
            </span>
        </div>
        <div style="margin-top: 10em;">
            <router-view></router-view>
        </div>
    </div>
</template>
 
<style scoped>
* {
    margin: 0;
    padding: 0;
}
</style>