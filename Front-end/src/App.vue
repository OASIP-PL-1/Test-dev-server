<script setup>
    import icon_Menu from './components/icons/menu.vue'
    import './style.css'

    const menuToggle = () => {
        const toggleMenu = document.querySelector(".menu");
        toggleMenu.classList.toggle("active");
      }
    const removeToken = () => localStorage.removeItem('jwtToken')
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
                    <span style="color: #FFCB4C; font-size: 10px; float: left;">v1.1.29.20</span>
                    <span><p style="float:right;">
                        <div class="action">
                            <div class="profile" @click="menuToggle();">
                                <img src="./assets/icons/account.png"/>
                            </div>
                            <span class="menu">
                            <h3><b>FirstName</b></h3>
                                <ul>
                                    <li>
                                        <img src="./assets/icons/profile.png" /><router-link :to="{name:'LoginUser'}">My profile</router-link>
                                    </li>
                                    <li>
                                        <img src="./assets/icons/editUser.png" /><router-link :to="{name:'LoginUser'}">Edit profile</router-link>
                                    </li>
                                    <li>
                                        <img src="./assets/icons/logout.png" /><router-link :to="{name:'Home'}" @click="removeToken()">Logout</router-link>
                                    </li>
                                </ul>
                            </span>
                        </div>
                        </p>
                    </span>
                    <span class="dropdown">
                        <p style="float:right;" class="dropbtn"><icon_Menu/></p>
                        <span class="dropdown-content">
                            <router-link :to="{name:'LoginUser'}"><p style="float:right;">Login</p></router-link>
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