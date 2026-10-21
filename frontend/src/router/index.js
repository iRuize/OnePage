import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', name: 'login', component: LoginView },
        { path: '/home', name: 'home', component: HomeView, meta: { requiresAuth: true } },
        { path: '/:pathMatch(.*)*', redirect: '/login' }
    ]
})

router.beforeEach((to) => {
    if (to.meta.requiresAuth && !localStorage.getItem('love_token')) {
        return { name: 'login' }
    }
})

export default router
