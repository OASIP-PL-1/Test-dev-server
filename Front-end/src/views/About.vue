<script setup>
    import {useRouter} from 'vue-router'

    const myRouter = useRouter()
    const goBack = () => myRouter.go(-1)
</script>
 
<template>
    <div class="boxAbout">
        <h2>About</h2>
        <p>We are PL-1 CaFe Ni</p>
        <p>school of information technology KMUTT</p>
        <div class="grid-cols-3">
            <div>
                <p>
                    <img src="../assets/dev-panalee.png" width="250px">
                <br>
                Panalee Palasri
                <br>
                63130500075
                </p>
            </div>
            <div>
                <p>
                    <img src="../assets/dev-piraya.png" width="250px">
                <br>  
                Piraya Sutthiparinyanon
                <br>
                63130500087
                </p>
            </div>
            <div>
                <p>
                    <img src="../assets/dev-vichuta.png" width="250px">
                <br>    
                Vichuta Pipoppinyo
                <br>
                6310500106
                </p>
            </div>
        </div>
    </div>
    <div class="btbackAbout">
        <button @click="goBack" class="button-18" role="button">Back</button>
    </div>
</template>
 
<style  scoped>
    h2 {
        color: #FFCB4C;
    }
    p {
        color: white;
    }
    .boxAbout {
        background-color:#3333A3;
        padding: 10px 40px 40px 40px;
        border-radius: 30px;
        min-height: 300px;
        /* min-width: 600px; */
        box-shadow: 0 12px 20px rgba(0, 0, 0, 0.12);
        margin-left: 2em;
        margin-right: 2em;
        text-align: center;
        -o-object-fit: cover;
        object-fit: cover;

    }
    .grid-cols-3 {
        display: grid;
        grid-template-columns: repeat(3,minmax(0,1fr));
        text-align: center;
        min-width: 100%;
    }
    .btbackAbout {
        margin: auto;
        margin-top: 1em;
        text-align: center;
    }
</style>