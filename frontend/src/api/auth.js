const API_BASE = (import.meta.env.VITE_API_BASE || '/api').replace(/\/+$/, '')
const TOKEN_KEY = 'love_token'

async function request(path, options = {}) {
    const { auth = true, body, headers: extraHeaders, ...rest } = options

    const headers = { 'Content-Type': 'application/json', ...extraHeaders }
    if (auth) {
        const token = localStorage.getItem(TOKEN_KEY)
        if (token) {
            headers.Authorization = `Bearer ${token}`
        }
    }

    let response
    try {
        response = await fetch(API_BASE + path, {
            ...rest,
            headers,
            body: body === undefined ? undefined : JSON.stringify(body)
        })
    } catch (e) {
        throw new Error('无法连接服务器，请确认后端已启动')
    }

    const result = await response.json().catch(() => null)
    if (!response.ok || !result || result.code !== 0) {
        throw new Error((result && result.message) || `请求失败（${response.status}）`)
    }
    return result.data
}

export function login(username, password, remember) {
    return request('/auth/login', {
        method: 'POST',
        auth: false,
        body: { username, password, remember }
    })
}

export function register(username, password, nickname) {
    return request('/auth/register', {
        method: 'POST',
        auth: false,
        body: { username, password, nickname }
    })
}

export function me() {
    return request('/auth/me')
}

export function logout() {
    return request('/auth/logout', { method: 'POST' })
}
