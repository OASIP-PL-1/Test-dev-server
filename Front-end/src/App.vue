<script setup>
</script>
 
<template>
<div class="bg-home">
    <div class="navbar">
        <!-- <h1 style="font-size: 2em; display: inline;">BOOKING. </h1> 
        <span><strong> OASIP - PL1</strong></span> -->
        <img src="./assets/logo.png" alt="logo"
            style="padding-left: 4em; padding-top: 5px; max-width: 500px; min-width: 300px; width: 20%;">
        <span class="navtop" style="display: inline;">
            <span class="navhover">
                <router-link :to="{name:'About'}"><p style="float:right;">About</p></router-link>
                <router-link :to="{name:'AddNewEvent'}"><p style="float:right;">Book</p></router-link>
                <router-link :to="{name:'ViewEvent'}"><p style="float:right;">Event</p></router-link>
                <router-link :to="{name:'Home'}"><p style="float:right;">Home</p></router-link>
            </span>
        </span>
    </div>
    <!-- <div> -->
        <!-- <router-link :to="{name:'ViewEvent'}">View Event</router-link> -->
    <!-- </div> -->
    <div>
        <router-view></router-view>
    </div>
</div>
</template>
 
<style>
    @import url('https://fonts.googleapis.com/css2?family=K2D&display=swap');
    @import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@600&display=swap');

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
        margin-top: 10em;
        margin-left: 0;
        overflow-x: hidden;
    }
    button {
        background-color: #3333A3;
        border-radius: 10px;
        box-shadow: 0 2px 2px 0 rgba(0,0,0,0.2);
        padding: 5px 10px;
        color: white;
        font-size: 1.5em;
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
        /* border-bottom: 1px solid #e7e7e7; */
        color: white;
        padding-top: 1em;
        padding-bottom: 1em;
    }
    .navbar p {
        float: right;
        display: block;
        font-weight: bold;
        text-align: center;
        padding: 10px 30px;
        text-decoration: none;
        margin: 0;
        font-size: 20px;
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
        /* CSS */
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

    .button-18:hover,
    .button-18:focus { 
        background-color: #16437E;
        color: #ffffff;
    }

    .button-18:active {
        background: #09223b;
        color: rgb(255, 255, 255, .7);
    }

    .button-18:disabled { 
        cursor: not-allowed;
        background: rgba(0, 0, 0, .08);
        color: rgba(0, 0, 0, .3);
    }
    hr {
        border: 1px solid #5C5CFF;
    }
</style>