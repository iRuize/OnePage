import {ref} from 'vue'

const toasts = ref([])
let seed = 0

/**
 * 全局 Toast（模块级单例，跨路由共享）
 */
export function useToast() {
    function showToast(message, type = 'info', duration = 2400) {
        const id = ++seed
        toasts.value.push({ id, message, type })
        window.setTimeout(() => {
            toasts.value = toasts.value.filter((item) => item.id !== id)
        }, duration)
    }

    return { toasts, showToast }
}
