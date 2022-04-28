import { createRouter, createWebHistory } from 'vue-router'
import ViewEvent from '../views/ViewEvent.vue'
import NotFoundPage from '../views/NotFoundPage.vue'
import ThisEvent from '../views/ThisEvent.vue'

const history = createWebHistory()
const routes = [
  {
    path: '/:catchNotMatchPath(.*)',
    name: 'NotFound',
    component: NotFoundPage
  },
  {
    path: '/',
    name: 'ViewEvent',
    component: ViewEvent
  },
  {
    path: '/view-event/:eventId',
    name: 'ThisEvent',
    component: ThisEvent
  }
]

const router = createRouter({ history, routes })
export default router