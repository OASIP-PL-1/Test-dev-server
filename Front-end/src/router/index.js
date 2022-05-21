import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ViewEvent from '../views/ViewEvent.vue'
import ViewEventCategory from '../views/ViewEventCategory.vue'
import NotFoundPage from '../views/NotFoundPage.vue'
import ThisEvent from '../views/ThisEvent.vue'
import AddNewEvent from '../views/AddNewEvent.vue'
import About from '../views/About.vue'

const history = createWebHistory('/pl1/')
const routes = [
  {
    path: '/:catchNotMatchPath(.*)',
    name: 'NotFound',
    component: NotFoundPage
  },
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/view-event',
    name: 'ViewEvent',
    component: ViewEvent
  },
  {
    path: '/view-event-category',
    name: 'ViewEventCategory',
    component: ViewEventCategory
  },
  {
    path: '/view-event/:eventId',
    name: 'ThisEvent',
    component: ThisEvent
  },
  {
    path: '/create-new-event',
    name: 'AddNewEvent',
    component: AddNewEvent
  },
  {
    path: '/about',
    name: 'About',
    component: About
  }
]

const router = createRouter({ history, routes })
export default router