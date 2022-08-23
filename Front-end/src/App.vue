<script setup>
</script>
 
<template>
    <div class="bg-home">
        <div class="navbar">
            <router-link :to="{name:'Home'}">
                <img src="./assets/logo.png" alt="logo"
                style="padding-left: 2em; padding-top: 10px; max-width: 450px; min-width: 250px; width: 10%;">
            </router-link>
            <span style="color: #FFCB4C; font-size: 10px">v1.1.21.6</span>
            <span class="navtop" style="display: inline;">
                <span class="navhover">
                    <div class="dropdown">
                        <p style="float:right;" class="dropbtn">///</p>
                        <div class="dropdown-content">
                            <router-link :to="{name:'About'}"><p style="float:right;">About</p></router-link>
                            <router-link :to="{name:'CreateUser'}"><p style="float:right;">Sign Up</p></router-link>
                            <router-link :to="{name:'ViewUser'}"><p style="float:right;">User</p></router-link>
                            <router-link :to="{name:'AddNewEvent'}"><p style="float:right;">Book</p></router-link>
                            <router-link :to="{name:'ViewEventCategory'}"><p style="float:right;">Category</p></router-link>
                            <router-link :to="{name:'ViewEvent'}"><p style="float:right;">Event</p></router-link>
                        </div>
                    </div> 
                </span>
            </span>
        </div>
        <div>
            <router-view></router-view>
        </div>
    </div>
</template>
 
<style>
    @import url('https://fonts.googleapis.com/css2?family=K2D&display=swap');

    template {
        padding: 0;
    }
    body {
        font-family: 'K2D', sans-serif;
        background-image: url(./assets/background.png); 
        -webkit-background-size: cover;
        -moz-background-size: cover;
        -o-background-size: cover;
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
        background-attachment: fixed;
        /* margin-top: 10em; */
        margin-left: 0;
        overflow-x: hidden;
        -o-object-fit: cover;
        object-fit: cover;
    }
    .bg-home {
        align-items: center;
        width: 100vw;
        margin: 0;
        display: auto;
    }
    .navbar {
        overflow: auto;
        position: fixed;
        background-color: #3333A3;
        top: 0;
        width: 100%;
        color: white;
        padding-top: 0.5em;
        padding-bottom: 0.5em;
    }
    .navbar p {
        float: right;
        display: block;
        font-weight: bold;
        text-align: center;
        padding: 10px 30px;
        text-decoration: none;
        margin: 0;
        font-size: 18px;
    }
    .navtop {
        padding-top: 2em;
    }
    a {
        color: white;
    }
    .navhover a:hover:not([disabled]) {
        color: #FFA21A;
    }
    .router-link-active {
        color: #FFCB4C;
    }
    button {
        box-shadow: 0 2px 2px 0 rgba(0,0,0,0.2);
        color: white;
    }
    button:hover:not([disabled]) {
        background-color: #FFA21A;
        color: white;
        transition-duration: .1s;
    }
    button:active:not([disabled]){
        background-color: #3333A3;
        color: white;
        transition-duration: .1s;
    }
    .button-18 {
        align-items: center;
        background-color: #5C5CFF;
        border: 0;
        border-radius: 100px;
        box-sizing: border-box;
        color: #ffffff;
        cursor: pointer;
        display: inline-flex;
        font-family: -apple-system, system-ui, system-ui, "Segoe UI", Roboto, "Helvetica Neue", "Fira Sans", Ubuntu, Oxygen, "Oxygen Sans", Cantarell, "Droid Sans", "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Lucida Grande", Helvetica, Arial, sans-serif;
        font-size: 16px;
        font-weight: 600;
        justify-content: center;
        line-height: 20px;
        max-width: 480px;
        min-height: 40px;
        min-width: 0px;
        overflow: hidden;
        padding: 0px;
        padding-left: 20px;
        padding-right: 20px;
        text-align: center;
        touch-action: manipulation;
        transition: background-color 0.167s cubic-bezier(0.4, 0, 0.2, 1) 0s, box-shadow 0.167s cubic-bezier(0.4, 0, 0.2, 1) 0s, color 0.167s cubic-bezier(0.4, 0, 0.2, 1) 0s;
        user-select: none;
        -webkit-user-select: none;
        vertical-align: middle;
    }
    .button-18:disabled {
        background-color: #c2c2ff;
    }
    .negative {
        background-color: #fff3d5;
        color: orangered;
    }
    hr {
        border: 1px solid #5C5CFF;
    }
    /* -------- Drop down ----------- */
    
    @media only screen and (max-width: 720px) {
    .dropdown {
        float: right;
        overflow: hidden;
    }

    .dropdown .dropbtn {
        border: none;
        outline: none;
        color: white;
        padding: 14px 16px;
        background-color: inherit;
        font-family: inherit;
        margin: 0;
    }

    /* .navbar a:hover, .dropdown:hover .dropbtn {
        background-color: red;
    } */

    .dropdown-content {
        display: none;
        /* position: absolute; */
        /* background-color: #f9f9f9; */
        /* min-width: 160px; */
        /* box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); */
        z-index: 1;
    }

    .dropdown-content a {
        float: none;
        /* color: black; */
        padding: 12px 16px;
        text-decoration: none;
        display: inline;
        text-align: left;
    }

    /* .dropdown-content a:hover {
        background-color: #ddd;
    } */

    .dropdown:hover .dropdown-content {
        display: block;
    }
    }
        
</style>