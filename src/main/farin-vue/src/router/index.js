import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AjaxTestView from '../views/AjaxTestView.vue'
import StateTestView from '../views/StateTestView.vue'
import TcpIpSendTestView from '../views/TcpIpSendTestView.vue'
import ChildTest from '../views/ChildTest.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/ajaxTest',
      name: 'ajax테스트',
      component: AjaxTestView
    },
    {
      path: '/stateTest',
      name: 'state테스트',
      component: StateTestView
    },
    {
      path: '/tcpIpSendTest',
      name: '소켓송신',
      component: TcpIpSendTestView
    },
    {
      path: '/childTest',
      name: 'Child상속테스트',
      component: ChildTest
    }
  ]
})

export default router
