<template>
    <div class="diary-page essay-page">
        <header class="topbar">
            <div class="brand-mark"><span>一页</span></div>
            <div class="user">
                <span class="user-avatar">{{ user.avatar || '一' }}</span>
                <span class="user-name">{{ displayName }}</span>
                <button class="logout" type="button" @click="goHome">返回</button>
            </div>
        </header>

        <main class="diary-main">
            <header class="diary-head">
                <div>
                    <p class="diary-kicker">ESSAY</p>
                    <h1>随笔</h1>
                    <p class="diary-sub">没有主题的念头，也值得留下。</p>
                </div>
                <button class="btn-primary" type="button" @click="openCreate">写一段</button>
            </header>

            <div v-if="loading" class="diary-state">正在读取…</div>

            <div v-else-if="essays.length === 0" class="diary-empty">
                <p class="empty-mark">—</p>
                <h2>还没有随笔</h2>
                <p>想到什么，就写什么。</p>
                <button class="btn-primary" type="button" @click="openCreate">写第一段</button>
            </div>

            <div v-else class="diary-list">
                <article
                    v-for="item in essays"
                    :key="item.id"
                    class="diary-item"
                    @click="openEdit(item)"
                >
                    <div class="diary-date">
                        <span class="day">{{ formatDay(item.createdAt) }}</span>
                        <span class="month">{{ formatMonth(item.createdAt) }}</span>
                    </div>

                    <div class="diary-body">
                        <div class="diary-title-row">
                            <h2>{{ item.title }}</h2>
                            <span class="diary-week">{{ formatWeek(item.createdAt) }}</span>
                        </div>
                        <p>{{ excerpt(item.content) }}</p>
                    </div>

                    <button class="diary-delete" type="button" @click.stop="handleDelete(item)">删除</button>
                </article>
            </div>
        </main>

        <!-- 编辑抽屉 -->
        <transition name="drawer">
            <div v-if="drawerOpen" class="drawer-mask" @click.self="closeDrawer">
                <aside class="diary-drawer">
                    <header class="drawer-head">
                        <h2>{{ editing.id ? '编辑随笔' : '写一段' }}</h2>
                        <button class="drawer-close" type="button" @click="closeDrawer">×</button>
                    </header>

                    <div class="drawer-body">
                        <p class="drawer-date">{{ editingDate }}</p>

                        <input
                            v-model="form.title"
                            class="title-input"
                            type="text"
                            placeholder="标题"
                            maxlength="100"
                        >

                        <textarea
                            v-model="form.content"
                            class="content-input"
                            rows="12"
                            placeholder="想到什么，就写什么。"
                            maxlength="20000"
                        ></textarea>
                    </div>

                    <footer class="drawer-foot">
                        <button class="btn-ghost" type="button" @click="closeDrawer">取消</button>
                        <button class="btn-primary" type="button" :disabled="saving" @click="handleSave">
                            {{ saving ? '保存中…' : '保存' }}
                        </button>
                    </footer>
                </aside>
            </div>
        </transition>
    </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createEssay, deleteEssay, listEssays, updateEssay } from '../api/essay'
import { useToast } from '../composables/useToast'
import '../assets/styles/app.css'
import '../assets/styles/diary.css'
import '../assets/styles/essay.css'

const router = useRouter()
const { showToast } = useToast()

const user = ref(JSON.parse(localStorage.getItem('love_user') || 'null') || {})
const displayName = computed(() => user.value.nickname || user.value.username || '朋友')

const essays = ref([])
const loading = ref(true)
const drawerOpen = ref(false)
const saving = ref(false)

const editing = reactive({ id: null, createdAt: null })
const form = reactive({ title: '', content: '' })

const editingDate = computed(() => formatFull(editing.createdAt || Date.now()))

function pad(value) {
    return String(value).padStart(2, '0')
}

function formatDay(timestamp) {
    return pad(new Date(timestamp).getDate())
}

function formatMonth(timestamp) {
    const date = new Date(timestamp)
    return `${date.getFullYear()}.${pad(date.getMonth() + 1)}`
}

function formatWeek(timestamp) {
    const week = ['日', '一', '二', '三', '四', '五', '六'][new Date(timestamp).getDay()]
    return `星期${week}`
}

function formatFull(timestamp) {
    const date = new Date(timestamp)
    return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日 · ${formatWeek(timestamp)}`
}

function excerpt(content) {
    if (!content) {
        return '（无正文）'
    }
    const text = content.replace(/\s+/g, ' ').trim()
    return text.length > 80 ? `${text.slice(0, 80)}…` : text
}

async function load() {
    loading.value = true
    try {
        essays.value = await listEssays()
    } catch (e) {
        showToast(e.message || '随笔加载失败', 'error')
    } finally {
        loading.value = false
    }
}

function resetForm() {
    editing.id = null
    editing.createdAt = null
    form.title = ''
    form.content = ''
}

function openCreate() {
    resetForm()
    drawerOpen.value = true
}

function openEdit(item) {
    editing.id = item.id
    editing.createdAt = item.createdAt
    form.title = item.title
    form.content = item.content || ''
    drawerOpen.value = true
}

function closeDrawer() {
    if (saving.value) {
        return
    }
    drawerOpen.value = false
}

async function handleSave() {
    if (saving.value) {
        return
    }
    if (!form.title.trim()) {
        showToast('请输入标题', 'error')
        return
    }

    saving.value = true
    const payload = {
        title: form.title.trim(),
        content: form.content
    }

    try {
        if (editing.id) {
            await updateEssay(editing.id, payload)
            showToast('已保存修改', 'success')
        } else {
            await createEssay(payload)
            showToast('随笔已保存', 'success')
        }
        drawerOpen.value = false
        await load()
    } catch (e) {
        showToast(e.message || '保存失败', 'error')
    } finally {
        saving.value = false
    }
}

async function handleDelete(item) {
    if (!window.confirm(`确定删除「${item.title}」吗？`)) {
        return
    }
    try {
        await deleteEssay(item.id)
        showToast('已删除', 'info')
        await load()
    } catch (e) {
        showToast(e.message || '删除失败', 'error')
    }
}

function goHome() {
    router.push('/home')
}

onMounted(load)
</script>
