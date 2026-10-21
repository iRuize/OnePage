import { request } from './request'

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
