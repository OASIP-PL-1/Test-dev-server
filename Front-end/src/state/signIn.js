import { defineStore,acceptHMRUpdate } from 'pinia'
import { computed, ref } from 'vue'

export const useSignIn = defineStore('signIn', () => {
    const statusLogin = ref()
    const username = ref()
    const role = ref()

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
      if( (getCookie('userName')!==null 
        || getCookie('userRole')!==null 
        || getCookie('accessToken')!==null 
        || getCookie('refreshToken')!==null) === false ){
        statusLogin.value= false
      }else{
        statusLogin.value= true
        username.value = getCookie('userName')
        role.value = getCookie('userRole')
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
        alert("access token and refresh token UPDATED")
        location.reload() 
      }else if(res.status===400){
        console.log("Invalid Data")
      }

    }

    return {statusLogin, username, role,checkLogin, getCookie, setCookie, removeCookie, sendRefreshToken} 
})
if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useSignIn, import.meta.hot))
}