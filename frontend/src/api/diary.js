import { request } from './request'

export function listDiaries() {
    return request('/diaries')
}

export function createDiary(data) {
    return request('/diaries', { method: 'POST', body: data })
}

export function updateDiary(id, data) {
    return request(`/diaries/${id}`, { method: 'PUT', body: data })
}

export function deleteDiary(id) {
    return request(`/diaries/${id}`, { method: 'DELETE' })
}

export function uploadImage(file) {
    const form = new FormData()
    form.append('file', file)
    return request('/upload/image', { method: 'POST', body: form })
}
