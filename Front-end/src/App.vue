<script setup>
async function connectToServer() {
        const ws = new WebSocket('ws://localhost:3000');
        return new Promise((resolve, reject) => {
            const timer = setInterval(() => {
                if(ws.readyState === 1) {
                    clearInterval(timer)
                    resolve(ws);
                }
            }, 10);
        });
    }
connectToServer()
</script>
 
<template>
<div id="app">
    <h1>OASIP - PL1</h1>
    <div>
        <!-- <router-link :to="{name:'ViewEvent'}">View Event</router-link> -->
    </div>
    <div>
        <router-view></router-view>
    </div>
</div>
</template>
 
<style>
    body {
        background-color: #FFF1E6;
        margin: 2em;
    }
    button {
        background-color: #BCD4E6;
        border-radius: 5px;
        box-shadow: 0 2px 2px 0 rgba(0,0,0,0.2);
        padding: 5px;
    }
</style>