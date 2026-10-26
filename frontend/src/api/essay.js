import { request } from './request'

export function listEssays() {
    return request('/essays')
}

export function createEssay(data) {
    return request('/essays', { method: 'POST', body: data })
}

export function updateEssay(id, data) {
    return request(`/essays/${id}`, { method: 'PUT', body: data })
}

export function deleteEssay(id) {
    return request(`/essays/${id}`, { method: 'DELETE' })
}
