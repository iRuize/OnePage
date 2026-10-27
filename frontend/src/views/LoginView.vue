<template>
    <div class="login-page">
        <div class="login-split" aria-hidden="true"></div>

        <main class="shell">
            <section class="brand">
                <div class="brand-mark"><span>一页</span></div>

                <h1>把日子，<br>写成一页。</h1>

                <p class="brand-sub">日记、账本与随笔，安静地留在这里。</p>

                <div class="brand-meta">
                    <span>日记</span><i></i><span>账本</span><i></i><span>随笔</span>
                </div>

                <div class="brand-foot">留给以后的自己</div>
            </section>

            <section class="auth-card" :class="{ shake }">
                <!-- 登录 / 注册 -->
                <div v-if="!currentUser">
                    <header class="card-head">
                        <h2 class="card-title">{{ tabCopy.title }}</h2>
                        <p class="card-sub">{{ tabCopy.sub }}</p>
                    </header>

                    <div class="segmented" :data-active="tab">
                        <button
                            type="button"
                            class="seg-btn"
                            :class="{ active: tab === 'login' }"
                            @click="switchTab('login')"
                        >登录</button>
                        <button
                            type="button"
                            class="seg-btn"
                            :class="{ active: tab === 'register' }"
                            @click="switchTab('register')"
                        >注册</button>
                    </div>

                    <!-- 登录表单 -->
                    <form v-if="tab === 'login'" class="form" novalidate @submit.prevent="submitLogin">
                        <div class="field" :class="{ invalid: errors.loginUser }">
                            <label for="loginUser">用户名</label>
                            <input
                                id="loginUser"
                                v-model="loginForm.username"
                                name="username"
                                type="text"
                                autocomplete="username"
                                maxlength="20"
                                @input="errors.loginUser = false"
                            >
                        </div>

                        <div class="field" :class="{ invalid: errors.loginPass }">
                            <label for="loginPass">密码</label>
                            <div class="field-row">
                                <input
                                    id="loginPass"
                                    v-model="loginForm.password"
                                    name="password"
                                    :type="showLoginPass ? 'text' : 'password'"
                                    autocomplete="current-password"
                                    maxlength="32"
                                    @input="errors.loginPass = false"
                                >
                                <button
                                    type="button"
                                    class="eye"
                                    :aria-label="showLoginPass ? '隐藏密码' : '显示密码'"
                                    @click="showLoginPass = !showLoginPass"
                                >{{ showLoginPass ? '隐藏' : '显示' }}</button>
                            </div>
                        </div>

                        <div class="row-between">
                            <label class="check">
                                <input v-model="loginForm.remember" type="checkbox" id="remember">
                                <span class="box"></span>记住我
                            </label>
                            <span class="link-muted">安全登录</span>
                        </div>

                        <button type="submit" class="btn-primary" :class="{ loading }" :disabled="loading">
                            <span class="btn-text">登 录</span>
                            <span class="spinner"></span>
                        </button>

                        <p class="form-tip">
                            还没有账号？
                            <button type="button" class="link" @click="switchTab('register')">注册一个</button>
                        </p>
                    </form>

                    <!-- 注册表单 -->
                    <form v-else class="form" novalidate @submit.prevent="submitRegister">
                        <div class="field" :class="{ invalid: errors.regUser }">
                            <label for="regUser">用户名（3-20 位字母、数字或下划线）</label>
                            <input
                                id="regUser"
                                v-model="registerForm.username"
                                name="username"
                                type="text"
                                autocomplete="username"
                                maxlength="20"
                                @input="errors.regUser = false"
                            >
                        </div>

                        <div class="field">
                            <label for="regNick">昵称（选填）</label>
                            <input
                                id="regNick"
                                v-model="registerForm.nickname"
                                name="nickname"
                                type="text"
                                autocomplete="nickname"
                                maxlength="16"
                            >
                        </div>

                        <div class="field" :class="{ invalid: errors.regPass }">
                            <label for="regPass">密码（6-32 位）</label>
                            <div class="field-row">
                                <input
                                    id="regPass"
                                    v-model="registerForm.password"
                                    name="password"
                                    :type="showRegPass ? 'text' : 'password'"
                                    autocomplete="new-password"
                                    maxlength="32"
                                    @input="errors.regPass = false"
                                >
                                <button
                                    type="button"
                                    class="eye"
                                    :aria-label="showRegPass ? '隐藏密码' : '显示密码'"
                                    @click="showRegPass = !showRegPass"
                                >{{ showRegPass ? '隐藏' : '显示' }}</button>
                            </div>
                        </div>

                        <div class="field" :class="{ invalid: errors.regConfirm }">
                            <label for="regPass2">确认密码</label>
                            <div class="field-row">
                                <input
                                    id="regPass2"
                                    v-model="registerForm.confirm"
                                    name="confirm"
                                    :type="showRegConfirm ? 'text' : 'password'"
                                    autocomplete="new-password"
                                    maxlength="32"
                                    @input="errors.regConfirm = false"
                                >
                                <button
                                    type="button"
                                    class="eye"
                                    :aria-label="showRegConfirm ? '隐藏密码' : '显示密码'"
                                    @click="showRegConfirm = !showRegConfirm"
                                >{{ showRegConfirm ? '隐藏' : '显示' }}</button>
                            </div>
                        </div>

                        <button type="submit" class="btn-primary" :class="{ loading }" :disabled="loading">
                            <span class="btn-text">创建账号</span>
                            <span class="spinner"></span>
                        </button>

                        <p class="form-tip">
                            已有账号？
                            <button type="button" class="link" @click="switchTab('login')">直接登录</button>
                        </p>
                    </form>
                </div>

                <!-- 已登录 -->
                <div v-else class="welcome">
                    <div class="welcome-avatar">{{ currentUser.avatar || '一' }}</div>
                    <h2>欢迎回来，<span>{{ displayName }}</span></h2>
                    <p>你的记录还在继续</p>
                    <button type="button" class="btn-primary" @click="continueToHome">
                        <span class="btn-text">进入我的空间</span>
                        <span class="spinner"></span>
                    </button>
                    <button type="button" class="btn-ghost" @click="handleLogout">退出登录</button>
                </div>
            </section>
        </main>

        <!-- 登录成功遮罩 -->
        <div class="success-overlay" :class="{ show: showSuccess }" aria-hidden="true">
            <div class="success-mark">一页</div>
            <div class="success-title">欢迎回来</div>
            <div class="success-sub">正在进入你的空间…</div>
        </div>
    </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {login, logout, me, register} from '../api/auth'
import {useToast} from '../composables/useToast'
import '../assets/styles/login.css'

const router = useRouter()
const { showToast } = useToast()

const tab = ref('login')
const loading = ref(false)
const shake = ref(false)
const showSuccess = ref(false)
const currentUser = ref(null)

const loginForm = ref({ username: '', password: '', remember: true })
const registerForm = ref({ username: '', nickname: '', password: '', confirm: '' })
const errors = ref({})

const showLoginPass = ref(false)
const showRegPass = ref(false)
const showRegConfirm = ref(false)

const TAB_COPY = {
    login: { title: '欢迎回来', sub: '登录后继续你的记录' },
    register: { title: '开始记录', sub: '创建账号，留下属于你的第一页' }
}

const tabCopy = computed(() => TAB_COPY[tab.value])
const displayName = computed(() => {
    if (!currentUser.value) {
        return ''
    }
    return currentUser.value.nickname || currentUser.value.username
})

function switchTab(next) {
    tab.value = next
    errors.value = {}
}

function triggerShake() {
    shake.value = false
    window.setTimeout(() => {
        shake.value = true
    }, 0)
    window.setTimeout(() => {
        shake.value = false
    }, 450)
}

async function submitLogin() {
    if (loading.value) {
        return
    }
    errors.value = {}

    const username = loginForm.value.username.trim()
    const password = loginForm.value.password

    if (!username) {
        errors.value.loginUser = true
        showToast('请输入用户名', 'error')
        return
    }
    if (!password) {
        errors.value.loginPass = true
        showToast('请输入密码', 'error')
        return
    }

    loading.value = true
    try {
        const data = await login(username, password, loginForm.value.remember)
        handleAuthSuccess(data)
    } catch (e) {
        showToast(e.message || '登录失败，请稍后再试', 'error')
        triggerShake()
    } finally {
        loading.value = false
    }
}

async function submitRegister() {
    if (loading.value) {
        return
    }
    errors.value = {}

    const username = registerForm.value.username.trim()
    const password = registerForm.value.password
    const confirm = registerForm.value.confirm

    if (!username) {
        errors.value.regUser = true
        showToast('请输入用户名', 'error')
        return
    }
    if (!/^[a-zA-Z0-9_]{3,20}$/.test(username)) {
        errors.value.regUser = true
        showToast('用户名为 3-20 位字母、数字或下划线', 'error')
        return
    }
    if (password.length < 6 || password.length > 32) {
        errors.value.regPass = true
        showToast('密码长度为 6-32 位', 'error')
        return
    }
    if (password !== confirm) {
        errors.value.regConfirm = true
        showToast('两次输入的密码不一致', 'error')
        return
    }

    loading.value = true
    try {
        const data = await register(username, password, registerForm.value.nickname.trim())
        handleAuthSuccess(data)
    } catch (e) {
        showToast(e.message || '注册失败，请稍后再试', 'error')
        triggerShake()
    } finally {
        loading.value = false
    }
}

function handleAuthSuccess(data) {
    localStorage.setItem('love_token', data.token)
    localStorage.setItem('love_user', JSON.stringify(data.user))
    currentUser.value = data.user
    showSuccess.value = true
    window.setTimeout(() => {
        router.push('/home')
    }, 1100)
}

function continueToHome() {
    router.push('/home')
}

function handleLogout() {
    logout().catch(() => {
        // Token 可能已过期，仍然清除本地状态
    })
    localStorage.removeItem('love_token')
    localStorage.removeItem('love_user')
    currentUser.value = null
    switchTab('login')
    showToast('已退出登录', 'info')
}

onMounted(async () => {
    if (!localStorage.getItem('love_token')) {
        return
    }
    try {
        currentUser.value = await me()
    } catch (e) {
        localStorage.removeItem('love_token')
    }
})
</script>
