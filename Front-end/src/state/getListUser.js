import { defineStore,acceptHMRUpdate } from 'pinia'
import { computed, ref } from 'vue'

export const useListUser = defineStore('listuser',() => {
    // GET User
    const users = ref([])
    const loading =ref()
    const message = ref()
    // const token = ref()

    const getUsers = async () => {
      loading.value = true
      message.value = "loading..."
      const res = await fetch(`${import.meta.env.VITE_BASE_URL}/users`,{
        method: "GET",
        headers:{
          'Content-Type' : 'application/json',
          'Authorization' : 'Bearer '+localStorage.getItem('jwtToken')
        }
      })
        .catch((error)=> {
          message.value = "Error Backend Server!!!"
          console.log(error)
          console.log('GET List All User Fail')
         
      });
      console.log(res)
      console.log(res.status)
      if(res.status==200){
        users.value = await res.json()
        loading.value = false
        console.log(`GET List All User OK`)
        console.log(users.value)
      }else if(res.status===401){
        message.value = "Please login again"
      }else{
        message.value = "Not found Backend Server!!!"
      }
      
      

      
    }

    // userCheckList for CreateUser
    const userCheckListCreate = ref([])
    const getUserCheckListCreate = async () => {
      if(users.value.length === 0){
        await getUsers()
      }
      for(let i = 0; i < users.value.length; i++) {
        userCheckListCreate.value[i] = {
          userName: users.value[i].userName,
          userEmail: users.value[i].userEmail
        }
      }
      console.log('get User checklist for name, email [CREATE]')
      console.log(userCheckListCreate.value)
    }
 
    // userCheckList for ThisUser->EditUser
    const userCheckListEdit = ref([])
    const getUserCheckListEdit = (async(id) => {
        if(users.value.length === 0){
            await getUsers()
        }
        userCheckListEdit.value = users.value.filter((user) => user.id !== id)
        for(let i = 0; i < userCheckListEdit.value.length; i++) {
          userCheckListEdit.value[i] = {
            userName: userCheckListEdit.value[i].userName,
            userEmail: userCheckListEdit.value[i].userEmail
          }
        }
        console.log('get User checklist for name, email [EDIT]')
        console.log(userCheckListEdit.value)
    })
    return {  users,loading,message, getUsers, 
              userCheckListCreate, getUserCheckListCreate,
              userCheckListEdit, getUserCheckListEdit}
})

if (import.meta.hot){
    import.meta.hot.accept(acceptHMRUpdate(useListUser, import.meta.hot))
}
