import {createRouter, createWebHistory} from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import DiaryView from '../views/DiaryView.vue'
import BillView from '../views/BillView.vue'
import EssayView from '../views/EssayView.vue'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', name: 'login', component: LoginView },
        { path: '/home', name: 'home', component: HomeView, meta: { requiresAuth: true } },
        { path: '/diary', name: 'diary', component: DiaryView, meta: { requiresAuth: true } },
        { path: '/bill', name: 'bill', component: BillView, meta: { requiresAuth: true } },
        { path: '/essay', name: 'essay', component: EssayView, meta: { requiresAuth: true } },
        { path: '/:pathMatch(.*)*', redirect: '/login' }
    ]
})

router.beforeEach((to) => {
    if (to.meta.requiresAuth && !localStorage.getItem('love_token')) {
        return { name: 'login' }
    }
})

export default router
