import { useState, useEffect } from 'react'
import { appointmentsApi, customersApi, petsApi } from '../api'
import Modal from '../components/Modal'

const DOCTORS = ['Clair', 'Michell', 'Jay', 'Alex', 'Cam']
const STATUSES = ['PENDING', 'CANCELLED', 'COMPLETED']
const STATUS_COLORS = {
  PENDING: 'bg-amber-100 text-amber-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700'
}

const cls = "border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"

function AppointmentForm({ initial, customers, pets, onSave, onClose }) {
  const [form, setForm] = useState({
    date: initial?.date || '',
    time: initial?.time || '',
    reason: initial?.reason || '',
    doctor: initial?.doctor || DOCTORS[0],
    status: initial?.status || 'PENDING',
    person: { id: initial?.person?.id || '' },
    pet: { id: initial?.pet?.id || '' }
  })
  const [filteredPets, setFilteredPets] = useState([])

  useEffect(() => {
    const ownerId = parseInt(form.person.id)
    if (ownerId) setFilteredPets(pets.filter(p => p.owner?.id === ownerId))
    else setFilteredPets(pets)
  }, [form.person.id, pets])

  const handleCustomerChange = (cid) => {
    setForm(f => ({ ...f, person: { id: cid }, pet: { id: '' } }))
  }

  return (
    <form onSubmit={e => { e.preventDefault(); onSave(form) }} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Date</label>
          <input required type="date" value={form.date} onChange={e => setForm(f => ({ ...f, date: e.target.value }))} className={cls + ' w-full'} />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Time</label>
          <input required type="time" value={form.time} onChange={e => setForm(f => ({ ...f, time: e.target.value }))} className={cls + ' w-full'} />
        </div>
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Customer</label>
        <select required value={form.person.id} onChange={e => handleCustomerChange(e.target.value)} className={cls + ' w-full'}>
          <option value="">Select customer...</option>
          {customers.map(c => <option key={c.id} value={c.id}>{c.firstName} {c.lastName}</option>)}
        </select>
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Pet</label>
        <select required value={form.pet.id} onChange={e => setForm(f => ({ ...f, pet: { id: e.target.value } }))} className={cls + ' w-full'}>
          <option value="">Select pet...</option>
          {filteredPets.map(p => <option key={p.id} value={p.id}>{p.name} ({p.type})</option>)}
        </select>
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Doctor</label>
          <select value={form.doctor} onChange={e => setForm(f => ({ ...f, doctor: e.target.value }))} className={cls + ' w-full'}>
            {DOCTORS.map(d => <option key={d}>{d}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Status</label>
          <select value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))} className={cls + ' w-full'}>
            {STATUSES.map(s => <option key={s}>{s}</option>)}
          </select>
        </div>
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Reason</label>
        <input required value={form.reason} onChange={e => setForm(f => ({ ...f, reason: e.target.value }))} className={cls + ' w-full'} />
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

export default function Appointments() {
  const [appointments, setAppointments] = useState([])
  const [customers, setCustomers] = useState([])
  const [pets, setPets] = useState([])
  const [filters, setFilters] = useState({ status: '', doctor: '', date: '' })
  const [modal, setModal] = useState(null)

  const load = (f = filters) => {
    const params = {}
    if (f.status) params.status = f.status
    if (f.doctor) params.doctor = f.doctor
    if (f.date) params.date = f.date
    appointmentsApi.getAll(params).then(r => setAppointments(r.data)).catch(() => {})
  }

  useEffect(() => { load() }, [filters])
  useEffect(() => {
    customersApi.getAll().then(r => setCustomers(r.data)).catch(() => {})
    petsApi.getAll().then(r => setPets(r.data)).catch(() => {})
  }, [])

  const handleSave = async (form) => {
    modal === 'add' ? await appointmentsApi.create(form) : await appointmentsApi.update(modal.editing.appointmentId, form)
    setModal(null)
    load()
  }

  const handleStatusChange = async (appt, status) => {
    await appointmentsApi.update(appt.appointmentId, {
      ...appt,
      status,
      person: { id: appt.person?.id },
      pet: { id: appt.pet?.id }
    })
    load()
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this appointment?')) return
    await appointmentsApi.delete(id)
    load()
  }

  const setFilter = (key, val) => setFilters(f => ({ ...f, [key]: val }))

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h2 className="text-2xl font-bold text-slate-800">Appointments</h2>
        <button onClick={() => setModal('add')} className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">
          + New Appointment
        </button>
      </div>

      <div className="flex gap-3 mb-4 flex-wrap">
        <select value={filters.status} onChange={e => setFilter('status', e.target.value)} className={cls}>
          <option value="">All Status</option>
          {STATUSES.map(s => <option key={s}>{s}</option>)}
        </select>
        <select value={filters.doctor} onChange={e => setFilter('doctor', e.target.value)} className={cls}>
          <option value="">All Doctors</option>
          {DOCTORS.map(d => <option key={d}>{d}</option>)}
        </select>
        <input type="date" value={filters.date} onChange={e => setFilter('date', e.target.value)} className={cls} />
        {(filters.status || filters.doctor || filters.date) && (
          <button onClick={() => setFilters({ status: '', doctor: '', date: '' })}
            className="text-xs text-slate-500 hover:text-slate-700 px-2">Clear filters</button>
        )}
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Date / Time</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Customer</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Pet</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Doctor</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Reason</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Status</th>
              <th className="px-5 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {appointments.length === 0 && (
              <tr><td colSpan={7} className="px-5 py-8 text-center text-slate-400 text-sm">No appointments found.</td></tr>
            )}
            {appointments.map(a => (
              <tr key={a.appointmentId} className="hover:bg-slate-50">
                <td className="px-5 py-3.5">
                  <div className="font-medium text-slate-700">{a.date}</div>
                  <div className="text-slate-400 text-xs">{a.time}</div>
                </td>
                <td className="px-5 py-3.5 text-slate-600">{a.person?.firstName} {a.person?.lastName}</td>
                <td className="px-5 py-3.5 text-slate-600">{a.pet?.name}</td>
                <td className="px-5 py-3.5 text-slate-600">Dr. {a.doctor}</td>
                <td className="px-5 py-3.5 text-slate-500 max-w-[180px] truncate">{a.reason}</td>
                <td className="px-5 py-3.5">
                  <select value={a.status} onChange={e => handleStatusChange(a, e.target.value)}
                    className={`text-xs px-2 py-1 rounded-full font-medium border-0 cursor-pointer ${STATUS_COLORS[a.status] || ''}`}>
                    {STATUSES.map(s => <option key={s}>{s}</option>)}
                  </select>
                </td>
                <td className="px-5 py-3.5 text-right whitespace-nowrap">
                  <button onClick={() => setModal({ editing: a })} className="text-xs text-slate-400 hover:text-slate-700 mr-3">Edit</button>
                  <button onClick={() => handleDelete(a.appointmentId)} className="text-xs text-red-400 hover:text-red-600">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <Modal title={modal === 'add' ? 'New Appointment' : 'Edit Appointment'} onClose={() => setModal(null)}>
          <AppointmentForm
            initial={modal !== 'add' ? modal.editing : undefined}
            customers={customers}
            pets={pets}
            onSave={handleSave}
            onClose={() => setModal(null)}
          />
        </Modal>
      )}
    </div>
  )
}
