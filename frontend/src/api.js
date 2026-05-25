import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

export const customersApi = {
  getAll: (search) => api.get('/customers', { params: search ? { search } : {} }),
  getById: (id) => api.get(`/customers/${id}`),
  create: (data) => api.post('/customers', data),
  update: (id, data) => api.put(`/customers/${id}`, data),
  delete: (id) => api.delete(`/customers/${id}`)
}

export const petsApi = {
  getAll: () => api.get('/pets'),
  getByOwner: (ownerId) => api.get(`/pets/owner/${ownerId}`),
  create: (data) => api.post('/pets', data),
  update: (id, data) => api.put(`/pets/${id}`, data),
  delete: (id) => api.delete(`/pets/${id}`)
}

export const caseRecordsApi = {
  getByPet: (petId) => api.get(`/case-records/pet/${petId}`),
  create: (data) => api.post('/case-records', data),
  delete: (id) => api.delete(`/case-records/${id}`)
}

export const appointmentsApi = {
  getAll: (params) => api.get('/appointments', { params }),
  create: (data) => api.post('/appointments', data),
  update: (id, data) => api.put(`/appointments/${id}`, data),
  delete: (id) => api.delete(`/appointments/${id}`)
}

export const todosApi = {
  getAll: () => api.get('/todos'),
  create: (title) => api.post('/todos', { title }),
  toggle: (id) => api.put(`/todos/${id}/toggle`),
  delete: (id) => api.delete(`/todos/${id}`)
}
