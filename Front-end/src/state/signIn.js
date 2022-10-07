import { defineStore,acceptHMRUpdate } from 'pinia'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'


export const useSignIn = defineStore('signIn', () => {
    const myRouter = useRouter()
    const gotoHome = () => myRouter.push({name: 'Home'})

    // state  
    const statusLogin = ref()
    const user = ref({id:'', name:'', role:'', email:''})

    const getCookie = function(name){  //ดึง value จาก name=value ใน cookie
      let cookieName = `${encodeURIComponent(name)}=`,     //นำ name มาแปลงเป็น encode เพื่อจะเอาไปเทียบกับ cookie
        cookieStart = document.cookie.indexOf(cookieName), //หาตำแหน่งของ ที่เจอ'name='
        cookieValue = null;

        if (cookieStart > -1) { //ถ้าหาเจอ
        let cookieEnd = document.cookie.indexOf(';', cookieStart); //หาตำแหน่ง ที่เจอ ';'
        if (cookieEnd == -1) {  //ถ้าไม่มี ';' ปิด
          cookieEnd = document.cookie.length; //ให้ใช้ขนาดความยาวของ cookie แทน
        }

        cookieValue = decodeURIComponent(
          document.cookie.substring(cookieStart + cookieName.length, cookieEnd)
          //เลือก String ที่อยู่ระหว่าง 'name=' กับ ';'
          //มาแปลงเป็น decode แล้วเก็บเป็นค่า value
        );
      }
      return cookieValue;  
    }
    
    const setCookie = function(name, value, days){ //เปลี่ยน name และ value ใน cookie
      const d = new Date();
      d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
      let expires = "expires=" + d.toUTCString();
      document.cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}; ${expires};`
    }

    const removeCookie = (name) => setCookie(name,null,0)

    const checkLogin = () =>{
      if(getCookie('user')===null){
        statusLogin.value= false
      }else{
        statusLogin.value= true
        user.value = JSON.parse(getCookie('user'))
      }
    }
    
    const Logout = () => {
      if(confirm("Are you sure want to Logout?") == true){
          removeCookie('accessToken')
          removeCookie('refreshToken')
          removeCookie('user')
          statusLogin.value = false
          user.value = {id:'', name:'', role:'', email:''}
          gotoHome()
      }
  }

    const sendRefreshToken = async() => {
      const res = await fetch(`${import.meta.env.VITE_BASE_URL}/refresh`,{
        method: "POST",
        headers:{
          'content-Type' : 'application/json'
        },
        body: JSON.stringify({
          refreshToken : getCookie('refreshToken')
        })
      }).catch((error)=> {
          console.log(error)
          console.log('Error send refresh token')
      });

      console.log(res.status)
      if(res.status===200){
        const respone = await res.json()
        setCookie('accessToken',respone.message.accessToken,1)
        setCookie('refreshToken',respone.message.refreshToken,1)

        setCookie('user',JSON.stringify(JSON.parse(getCookie('user'))),1)

        alert("access token and refresh token UPDATED")
        location.reload() 
      }else if(res.status===400){
        console.log("Invalid Data")
      }

    }

    return {statusLogin, user, checkLogin, getCookie, setCookie, removeCookie, Logout, sendRefreshToken} 
})
if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useSignIn, import.meta.hot))
}