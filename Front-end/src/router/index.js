import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ViewEvent from '../views/ViewEvent.vue'
import ViewEventCategory from '../views/ViewEventCategory.vue'
import ThisEvent from '../views/ThisEvent.vue'
import CreateEvent from '../views/CreateEvent.vue'
import About from '../views/About.vue'
import ViewUser from '../views/ViewUser.vue'
import ThisUser from '../views/ThisUser.vue'
import SignUp from '../views/SignUp.vue'
import LogIn from '../views/LogIn.vue'
import { useSignIn } from '../state/signIn.js'
import Error401 from '../views/error/401.vue'
import Error403 from '../views/error/403.vue'
import Error404 from '../views/error/404.vue'
import Error500 from '../views/error/500.vue'

const history = createWebHistory('/pl1/')
const routes = [
  {
    path:'/401',
    name: 'Error401',
    component: Error401
  },
  {
    path:'/403',
    name: 'Error403',
    component: Error403
  },
  {
    path: '/404',
    name: 'Error404',
    component: Error404
  },
  {
    path:'/500',
    name: 'Error500',
    component: Error500
  },
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/view-event',
    name: 'ViewEvent',
    component: ViewEvent,
    beforeEnter: (to, from, nextTick) => {
      checkError401(nextTick)
    }
  },
  {
    path: '/view-event-category',
    name: 'ViewEventCategory',
    component: ViewEventCategory
  },
  {
    path: '/view-event/:eventId',
    name: 'ThisEvent',
    component: ThisEvent,
    beforeEnter: (to, from, nextTick) => {
      checkError401(nextTick)
    }
    
  },
  {
    path: '/create-event',
    name: 'CreateEvent',
    component: CreateEvent,
    beforeEnter: (to, from, nextTick) => {
      const signIn = useSignIn()
      //ถ้าเป็น lecture สร้าง Event ไม่ได้
      if(signIn.statusLogin === true && signIn.user.role === 'lecturer'){nextTick({name:'Error403'})}
      else{nextTick()}
    }
  },
  {
    path: '/view-user',
    name: 'ViewUser',
    component: ViewUser,
    beforeEnter: (to, from, nextTick) => {
      const signIn = useSignIn()
      //ถ้ายังไม่ login จะ link ไปหน้า Error 401
      if(signIn.statusLogin === false){nextTick({ name:'Error401'})}
      else if(signIn.statusLogin === true && signIn.user.role !== 'admin'){nextTick({name:'Error403'})}
      else{nextTick()}
    }
  },
  {
    path: '/view-user/:userId',
    name: 'ThisUser',
    component: ThisUser,
    beforeEnter: (to, from, nextTick) => {
      // reject the navigation
      console.log(to.params.userId)
      const signIn = useSignIn()
      if(signIn.statusLogin === false){nextTick({ name:'Error401'})}
      else if(signIn.statusLogin === true && signIn.user.role !== 'admin'){nextTick({name:'Error403'})}
      else{nextTick()}
     //ถ้า จะดู detail ได้เฉพาะ user ตัวเอง
      // if (signIn.user.id == to.params.userId){
      //   console.log('user id match')
      // }else{
      //   console.log('user id NOT match')
      // }
      // return false
    },
  },
  {
    path: '/sign-up',
    name: 'SignUp',
    component: SignUp,
    beforeEnter: (to, from, nextTick) => {
      const signIn = useSignIn()
      //ถ้ายังไม่ login จะ link ไปหน้า Error 401
      if(signIn.statusLogin === false){nextTick({ name:'Error401'})}
      else if(signIn.statusLogin === true && signIn.user.role !== 'admin'){nextTick({name:'Error403'})}
      else{nextTick()}
    }
  },
  {
    path: '/log-in',
    name: 'LogIn',
    component: LogIn,
    beforeEnter: (to, from, nextTick) => {
      const signIn = useSignIn()
      //ถ้า login แล้วไม่ใช่ admin จะ link ไปหน้า Error 403
      if(signIn.statusLogin === true && signIn.user.role !== 'admin'){nextTick({name:'Error403'})}
      else{nextTick()}
    }
  },
  {
    path: '/about',
    name: 'About',
    component: About
  }
  // ,
  // {
  //   name: 'Error401',
  //   component: Error401
  // },
  // {
  //   name: 'Error403',
  //   component: Error403
  // }
 
]

const router = createRouter({ history, routes })

// router.afterEach((to, from, next) => {
//   console.log(to)
//   console.log(from)
//   console.log(next)
//       //ถ้ายังไม่ login จะบังคับให้กดดูหน้าอื่นไม่ได้ จนกว่าจะ login
//   // const signIn = useSignIn()
//   // if(to.name === 'Home'|| to.name !== 'LoginUser'){
//   //   next({name:'LoginUser'})
//   // }
//   // if (to.name !== 'LoginUser' && signIn.statusLogin===false) next({ name: 'LoginUser' })
//   // else next()
// })
function checkError401 (nextTick){
  const signIn = useSignIn()
  //ถ้ายังไม่ login จะ link ไปหน้า Error 401
  if (signIn.statusLogin === false){nextTick({ name:'Error401'})}
  else{nextTick()}
}


export default router
