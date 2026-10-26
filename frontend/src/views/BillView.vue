<template>
    <div class="bill-page">
        <header class="topbar">
            <div class="brand-mark"><span>一页</span></div>
            <div class="user">
                <span class="user-avatar">{{ user.avatar || '一' }}</span>
                <span class="user-name">{{ displayName }}</span>
                <button class="logout" type="button" @click="goHome">返回</button>
            </div>
        </header>

        <main class="bill-main">
            <header class="bill-head">
                <div>
                    <p class="bill-kicker">LEDGER</p>
                    <h1>账本</h1>
                    <p class="bill-sub">收入和支出，心里有数。</p>
                </div>
                <button class="btn-primary" type="button" @click="openCreate">记一笔</button>
            </header>

            <section class="bill-summary">
                <div class="summary-item">
                    <span class="summary-label">本月支出</span>
                    <strong class="summary-value expense">¥ {{ formatMoney(summary.expense) }}</strong>
                </div>
                <div class="summary-item">
                    <span class="summary-label">本月收入</span>
                    <strong class="summary-value income">¥ {{ formatMoney(summary.income) }}</strong>
                </div>
                <div class="summary-item">
                    <span class="summary-label">本月结余</span>
                    <strong class="summary-value">¥ {{ formatMoney(summary.balance) }}</strong>
                </div>
            </section>

            <div v-if="loading" class="bill-state">正在读取…</div>

            <div v-else-if="bills.length === 0" class="bill-empty">
                <p class="empty-mark">—</p>
                <h2>还没有记录</h2>
                <p>记下第一笔收支，从这里开始。</p>
                <button class="btn-primary" type="button" @click="openCreate">记一笔</button>
            </div>

            <div v-else class="bill-list">
                <article
                    v-for="item in bills"
                    :key="item.id"
                    class="bill-item"
                    @click="openEdit(item)"
                >
                    <span class="bill-date">{{ formatDate(item.billDate) }}</span>
                    <span class="bill-category">{{ item.category || '未分类' }}</span>
                    <span class="bill-remark">{{ item.remark || '—' }}</span>
                    <span class="bill-amount" :class="item.type === 1 ? 'expense' : 'income'">
                        {{ item.type === 1 ? '-' : '+' }} ¥{{ formatMoney(item.amount) }}
                    </span>
                    <button class="bill-delete" type="button" @click.stop="handleDelete(item)">删除</button>
                </article>
            </div>
        </main>

        <!-- 记账抽屉 -->
        <transition name="drawer">
            <div v-if="drawerOpen" class="drawer-mask" @click.self="closeDrawer">
                <aside class="diary-drawer bill-drawer">
                    <header class="drawer-head">
                        <h2>{{ editing.id ? '编辑记录' : '记一笔' }}</h2>
                        <button class="drawer-close" type="button" @click="closeDrawer">×</button>
                    </header>

                    <div class="drawer-body">
                        <div class="type-switch">
                            <button
                                type="button"
                                :class="{ active: form.type === 1 }"
                                @click="switchType(1)"
                            >支出</button>
                            <button
                                type="button"
                                :class="{ active: form.type === 2 }"
                                @click="switchType(2)"
                            >收入</button>
                        </div>

                        <div class="amount-field">
                            <span>¥</span>
                            <input
                                v-model="form.amount"
                                type="number"
                                min="0"
                                step="0.01"
                                inputmode="decimal"
                                placeholder="0.00"
                            >
                        </div>

                        <div class="bill-field">
                            <label>分类</label>
                            <div class="category-row">
                                <button
                                    v-for="category in categories"
                                    :key="category"
                                    type="button"
                                    :class="{ active: form.category === category }"
                                    @click="toggleCategory(category)"
                                >{{ category }}</button>
                            </div>
                        </div>

                        <div class="bill-field">
                            <label>日期</label>
                            <input v-model="form.date" type="date">
                        </div>

                        <div class="bill-field">
                            <label>备注</label>
                            <input v-model="form.remark" type="text" maxlength="255" placeholder="写点什么">
                        </div>
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
import { createBill, deleteBill, listBills, updateBill } from '../api/bill'
import { useToast } from '../composables/useToast'
import '../assets/styles/app.css'
import '../assets/styles/diary.css'
import '../assets/styles/bill.css'

const router = useRouter()
const { showToast } = useToast()

const user = ref(JSON.parse(localStorage.getItem('love_user') || 'null') || {})
const displayName = computed(() => user.value.nickname || user.value.username || '朋友')

const EXPENSE_CATEGORIES = ['餐饮', '交通', '购物', '居住', '娱乐', '医疗', '学习', '其他']
const INCOME_CATEGORIES = ['工资', '奖金', '兼职', '理财', '其他']

const bills = ref([])
const loading = ref(true)
const drawerOpen = ref(false)
const saving = ref(false)

const editing = reactive({ id: null })
const form = reactive({
    type: 1,
    amount: '',
    category: '',
    date: toDateInput(Date.now()),
    remark: ''
})

const categories = computed(() => (form.type === 1 ? EXPENSE_CATEGORIES : INCOME_CATEGORIES))

const summary = computed(() => {
    const now = new Date()
    let expense = 0
    let income = 0

    bills.value.forEach((item) => {
        const date = new Date(item.billDate)
        if (date.getFullYear() !== now.getFullYear() || date.getMonth() !== now.getMonth()) {
            return
        }
        const amount = Number(item.amount)
        if (item.type === 1) {
            expense += amount
        } else {
            income += amount
        }
    })

    return { expense, income, balance: income - expense }
})

function pad(value) {
    return String(value).padStart(2, '0')
}

function toDateInput(timestamp) {
    const date = new Date(timestamp)
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function fromDateInput(value) {
    const parts = value.split('-').map(Number)
    return new Date(parts[0], parts[1] - 1, parts[2]).getTime()
}

function formatDate(timestamp) {
    const date = new Date(timestamp)
    return `${date.getFullYear()}.${pad(date.getMonth() + 1)}.${pad(date.getDate())}`
}

function formatMoney(value) {
    return Number(value || 0).toFixed(2)
}

async function load() {
    loading.value = true
    try {
        bills.value = await listBills()
    } catch (e) {
        showToast(e.message || '账本加载失败', 'error')
    } finally {
        loading.value = false
    }
}

function resetForm() {
    editing.id = null
    form.type = 1
    form.amount = ''
    form.category = ''
    form.date = toDateInput(Date.now())
    form.remark = ''
}

function openCreate() {
    resetForm()
    drawerOpen.value = true
}

function openEdit(item) {
    editing.id = item.id
    form.type = item.type
    form.amount = String(item.amount)
    form.category = item.category || ''
    form.date = toDateInput(item.billDate)
    form.remark = item.remark || ''
    drawerOpen.value = true
}

function closeDrawer() {
    if (saving.value) {
        return
    }
    drawerOpen.value = false
}

function switchType(type) {
    if (form.type === type) {
        return
    }
    form.type = type
    form.category = ''
}

function toggleCategory(category) {
    form.category = form.category === category ? '' : category
}

async function handleSave() {
    if (saving.value) {
        return
    }

    const amount = Number(form.amount)
    if (!form.amount || Number.isNaN(amount) || amount <= 0) {
        showToast('请输入正确的金额', 'error')
        return
    }
    if (!form.date) {
        showToast('请选择日期', 'error')
        return
    }

    saving.value = true
    const payload = {
        type: form.type,
        amount: Number(amount.toFixed(2)),
        category: form.category || null,
        remark: form.remark.trim() || null,
        billDate: fromDateInput(form.date)
    }

    try {
        if (editing.id) {
            await updateBill(editing.id, payload)
            showToast('已保存修改', 'success')
        } else {
            await createBill(payload)
            showToast('已记一笔', 'success')
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
    const label = `${item.category || '未分类'} ¥${formatMoney(item.amount)}`
    if (!window.confirm(`确定删除「${label}」吗？`)) {
        return
    }
    try {
        await deleteBill(item.id)
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
