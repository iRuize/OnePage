<template>
    <div class="home-page">
        <header class="topbar">
            <div class="brand-mark"><span>一页</span></div>
            <div class="user">
                <span class="user-avatar">{{ user.avatar || '一' }}</span>
                <span class="user-name">{{ displayName }}</span>
                <button class="logout" type="button" @click="handleLogout">退出</button>
            </div>
        </header>

        <main class="home">
            <p class="today">{{ today }}</p>
            <h1 class="greeting">{{ greeting }}，<span>{{ displayName }}</span>。</h1>
            <p class="home-sub">今天想留下点什么？</p>

            <div class="entries">
                <a
                    v-for="entry in entries"
                    :key="entry.index"
                    class="entry"
                    href="javascript:void(0)"
                    @click="openFeature(entry)"
                >
                    <span class="entry-index">{{ entry.index }}</span>
                    <h2>{{ entry.name }}</h2>
                    <p>{{ entry.desc }}</p>
                    <span class="entry-arrow">→</span>
                </a>
            </div>

            <p class="home-foot">一页 · 安静的记录</p>
        </main>
    </div>
</template>

<script setup>
import {computed, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useToast} from '../composables/useToast'
import '../assets/styles/app.css'

const router = useRouter()
const { showToast } = useToast()

const user = ref(JSON.parse(localStorage.getItem('onepage_user') || 'null') || {})
const displayName = computed(() => user.value.nickname || user.value.username || '朋友')

const entries = [
    { index: '01', name: '日记', desc: '记录一天里值得记住的片段。', path: '/diary' },
    { index: '02', name: '账本', desc: '收入与支出，心里有数。', path: '/bill' },
    { index: '03', name: '随笔', desc: '没有主题的念头，也值得留下。', path: '/essay' }
]

const now = new Date()
const week = ['日', '一', '二', '三', '四', '五', '六'][now.getDay()]
const today = `${now.getFullYear()} 年 ${now.getMonth() + 1} 月 ${now.getDate()} 日 · 星期${week}`

const hour = now.getHours()
const greeting = hour < 5 ? '夜深了'
    : hour < 11 ? '早安'
        : hour < 14 ? '午安'
            : hour < 18 ? '下午好' : '晚上好'

function openFeature(entry) {
    if (entry.path) {
        router.push(entry.path)
        return
    }
    showToast(`${entry.name}功能正在准备中`)
}

function handleLogout() {
    localStorage.removeItem('onepage_token')
    localStorage.removeItem('onepage_user')
    router.replace('/login')
}
</script>
