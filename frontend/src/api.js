import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  r => r,
  err => {
    // Only redirect to login for 401s on protected routes — never on the login request itself
    if (err.response?.status === 401 && !err.config?.url?.includes('/auth/')) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  me: () => api.get('/auth/me'),
  changePassword: (data) => api.post('/auth/change-password', data),
  mfaSetup: () => api.post('/auth/mfa/setup'),
  mfaConfirm: (code) => api.post('/auth/mfa/confirm', { code }),
  mfaDisable: (code) => api.post('/auth/mfa/disable', { code }),
}

export const customersApi = {
  getAll: (search) => api.get('/customers', { params: search ? { search } : {} }),
  getById: (id) => api.get(`/customers/${id}`),
  create: (data) => api.post('/customers', data),
  update: (id, data) => api.put(`/customers/${id}`, data),
  delete: (id) => api.delete(`/admin/customers/${id}`),
}

export const petsApi = {
  getAll: () => api.get('/pets'),
  getById: (id) => api.get(`/pets/${id}`),
  getByOwner: (ownerId) => api.get(`/pets/owner/${ownerId}`),
  create: (data) => api.post('/pets', data),
  update: (id, data) => api.put(`/pets/${id}`, data),
  delete: (id) => api.delete(`/pets/${id}`),
  getCaseRecords: (id) => api.get(`/pets/${id}/case-records`),
  getWellnessLogs: (id) => api.get(`/pets/${id}/wellness-logs`),
}

export const caseRecordsApi = {
  getByPet: (petId) => api.get(`/case-records/pet/${petId}`),
  create: (data) => api.post('/case-records', data),
  update: (id, data) => api.put(`/case-records/${id}`, data),
  delete: (id) => api.delete(`/case-records/${id}`),
}

export const wellnessLogsApi = {
  getByPet: (petId) => api.get(`/wellness-logs/pet/${petId}`),
  create: (data) => api.post('/wellness-logs', data),
  delete: (id) => api.delete(`/wellness-logs/${id}`),
}

export const appointmentsApi = {
  getAll: (params) => api.get('/appointments', { params }),
  getById: (id) => api.get(`/appointments/${id}`),
  create: (data) => api.post('/appointments', data),
  update: (id, data) => api.put(`/appointments/${id}`, data),
  delete: (id) => api.delete(`/appointments/${id}`),
}

export const doctorsApi = {
  getAll: (activeOnly = false) => api.get('/doctors', { params: { activeOnly } }),
  create: (data) => api.post('/doctors', data),
  update: (id, data) => api.put(`/doctors/${id}`, data),
  delete: (id) => api.delete(`/doctors/${id}`),
}

export const adminApi = {
  listUsers: () => api.get('/admin/users'),
  createUser: (data) => api.post('/admin/users', data),
  changeRole: (id, role) => api.put(`/admin/users/${id}/role`, { role }),
  setActive: (id, active) => api.put(`/admin/users/${id}/active`, { active }),
  resetPassword: (id, password) => api.put(`/admin/users/${id}/reset-password`, { password }),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  getAuditLogs: (page = 0) => api.get('/admin/audit-logs', { params: { page, size: 50 } }),
}
