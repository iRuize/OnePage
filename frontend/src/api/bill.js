import {request} from './request'

export function listBills() {
    return request('/bills')
}

export function createBill(data) {
    return request('/bills', { method: 'POST', body: data })
}

export function updateBill(id, data) {
    return request(`/bills/${id}`, { method: 'PUT', body: data })
}

export function deleteBill(id) {
    return request(`/bills/${id}`, { method: 'DELETE' })
}
