const API_BASE = (import.meta.env.VITE_API_BASE || '/api').replace(/\/+$/, '')

export const TOKEN_KEY = 'love_token'

export const USER_KEY = 'love_user'

/**
 * 统一请求封装：JSON、Bearer Token、业务错误处理
 */
export async function request(path, options = {}) {
    const { auth = true, body, headers: extraHeaders, ...rest } = options

    const headers = { ...extraHeaders }
    if (body !== undefined && !(body instanceof FormData)) {
        headers['Content-Type'] = 'application/json'
    }
    if (auth) {
        const token = localStorage.getItem(TOKEN_KEY)
        if (token) {
            headers.Authorization = `Bearer ${token}`
        }
    }

    const controller = new AbortController()
    const timer = window.setTimeout(() => controller.abort(), 15000)

    let response
    try {
        response = await fetch(API_BASE + path, {
            ...rest,
            headers,
            signal: controller.signal,
            body: body === undefined
                ? undefined
                : (body instanceof FormData ? body : JSON.stringify(body))
        })
    } catch (e) {
        if (e.name === 'AbortError') {
            throw new Error('请求超时，请检查后端服务是否正常')
        }
        throw new Error('无法连接服务器，请确认后端已启动')
    } finally {
        window.clearTimeout(timer)
    }

    const result = await response.json().catch(() => null)
    if (!response.ok || !result || result.code !== 0) {
        throw new Error((result && result.message) || `请求失败（${response.status}）`)
    }
    return result.data
}
