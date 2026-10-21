<template>
    <div class="diary-page">
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
                    <p class="diary-kicker">DIARY</p>
                    <h1>日记</h1>
                    <p class="diary-sub">把一天里值得记住的片段，留在这里。</p>
                </div>
                <button class="btn-primary" type="button" @click="openCreate">写一篇</button>
            </header>

            <div v-if="loading" class="diary-state">正在读取…</div>

            <div v-else-if="diaries.length === 0" class="diary-empty">
                <p class="empty-mark">—</p>
                <h2>还没有日记</h2>
                <p>从今天开始，写下第一页。</p>
                <button class="btn-primary" type="button" @click="openCreate">写第一篇</button>
            </div>

            <div v-else class="diary-list">
                <article
                    v-for="item in diaries"
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
                            <span v-if="item.mood" class="diary-mood">{{ moodLabel(item.mood) }}</span>
                            <span class="diary-week">{{ formatWeek(item.createdAt) }}</span>
                        </div>
                        <p>{{ excerpt(item.content) }}</p>
                    </div>

                    <div v-if="item.images.length" class="diary-thumb">
                        <img :src="item.images[0]" :alt="item.title">
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
                        <h2>{{ editing.id ? '编辑日记' : '写一篇' }}</h2>
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

                        <div class="mood-row">
                            <button
                                v-for="mood in moods"
                                :key="mood.value"
                                type="button"
                                class="mood-btn"
                                :class="{ active: form.mood === mood.value }"
                                @click="toggleMood(mood.value)"
                            >{{ mood.label }}</button>
                        </div>

                        <textarea
                            v-model="form.content"
                            class="content-input"
                            rows="10"
                            placeholder="今天发生了什么？"
                            maxlength="20000"
                        ></textarea>

                        <div class="upload-grid">
                            <div v-for="(image, index) in form.images" :key="image" class="upload-item">
                                <img :src="image" alt="">
                                <button class="upload-remove" type="button" @click="removeImage(index)">×</button>
                            </div>

                            <label v-if="form.images.length < 9" class="upload-add" :class="{ uploading }">
                                <input type="file" accept="image/*" multiple hidden @change="handleUpload">
                                <span>{{ uploading ? '上传中…' : '+ 图片' }}</span>
                            </label>
                        </div>

                        <p v-if="uploadError" class="upload-error">{{ uploadError }}</p>
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
import { createDiary, deleteDiary, listDiaries, updateDiary, uploadImage } from '../api/diary'
import { useToast } from '../composables/useToast'
import '../assets/styles/app.css'
import '../assets/styles/diary.css'

const router = useRouter()
const { showToast } = useToast()

const user = ref(JSON.parse(localStorage.getItem('love_user') || 'null') || {})
const displayName = computed(() => user.value.nickname || user.value.username || '朋友')

const diaries = ref([])
const loading = ref(true)
const drawerOpen = ref(false)
const saving = ref(false)
const uploading = ref(false)
const uploadError = ref('')

const editing = reactive({ id: null, createdAt: null })
const form = reactive({ title: '', content: '', mood: '', images: [] })

const moods = [
    { value: 'calm', label: '平静' },
    { value: 'happy', label: '开心' },
    { value: 'down', label: '低落' },
    { value: 'tired', label: '疲惫' },
    { value: 'hope', label: '期待' }
]

const MOOD_MAP = {
    calm: '平静',
    happy: '开心',
    down: '低落',
    tired: '疲惫',
    hope: '期待'
}

const editingDate = computed(() => formatFull(editing.createdAt || Date.now()))

function moodLabel(value) {
    return MOOD_MAP[value] || value
}

function formatDay(timestamp) {
    return String(new Date(timestamp).getDate()).padStart(2, '0')
}

function formatMonth(timestamp) {
    const date = new Date(timestamp)
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}`
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
        diaries.value = await listDiaries()
    } catch (e) {
        showToast(e.message || '日记加载失败', 'error')
    } finally {
        loading.value = false
    }
}

function resetForm() {
    editing.id = null
    editing.createdAt = null
    form.title = ''
    form.content = ''
    form.mood = ''
    form.images = []
    uploadError.value = ''
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
    form.mood = item.mood || ''
    form.images = [...item.images]
    uploadError.value = ''
    drawerOpen.value = true
}

function closeDrawer() {
    if (saving.value) {
        return
    }
    drawerOpen.value = false
}

function toggleMood(value) {
    form.mood = form.mood === value ? '' : value
}

async function handleUpload(event) {
    const files = Array.from(event.target.files || [])
    event.target.value = ''
    if (files.length === 0) {
        return
    }

    uploadError.value = ''
    uploading.value = true
    try {
        for (const file of files) {
            if (form.images.length >= 9) {
                showToast('最多上传 9 张图片', 'error')
                break
            }
            const url = await uploadImage(file)
            form.images.push(url)
        }
    } catch (e) {
        uploadError.value = e.message || '图片上传失败'
    } finally {
        uploading.value = false
    }
}

function removeImage(index) {
    form.images.splice(index, 1)
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
        content: form.content,
        mood: form.mood || null,
        images: form.images
    }

    try {
        if (editing.id) {
            await updateDiary(editing.id, payload)
            showToast('已保存修改', 'success')
        } else {
            await createDiary(payload)
            showToast('日记已保存', 'success')
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
        await deleteDiary(item.id)
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
