import { useState, useEffect } from 'react'
import { appointmentsApi, customersApi, petsApi, doctorsApi } from '../api'
import Modal from '../components/Modal'

const STATUSES = ['PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED']
const STATUS_COLORS = {
  PENDING: 'bg-amber-100 text-amber-700',
  CONFIRMED: 'bg-blue-100 text-blue-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700',
}

const cls = "border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"

const isWithin30Min = (scheduledTime) => {
  if (!scheduledTime) return false
  return Math.abs(new Date(scheduledTime) - Date.now()) <= 30 * 60 * 1000
}

function AppointmentForm({ initial, customers, pets, doctors, onSave, onClose }) {
  const initDate = initial?.scheduledTime ? initial.scheduledTime.slice(0, 10) : ''
  const initTime = initial?.scheduledTime ? initial.scheduledTime.slice(11, 16) : ''
  const [form, setForm] = useState({
    date: initDate,
    time: initTime,
    personId: initial?.person?.id || '',
    petId: initial?.pet?.id || '',
    doctorId: initial?.doctor?.id || '',
    status: initial?.status || 'PENDING',
    notes: initial?.notes || '',
  })
  const [filteredPets, setFilteredPets] = useState([])

  useEffect(() => {
    if (form.personId) {
      setFilteredPets(pets.filter(p => p.owner?.id === form.personId))
    } else {
      setFilteredPets([])
    }
  }, [form.personId, pets])

  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))

  const handleSubmit = (e) => {
    e.preventDefault()
    const scheduledTime = form.date && form.time ? `${form.date}T${form.time}:00` : null
    onSave({ personId: form.personId, petId: form.petId, doctorId: form.doctorId || null, scheduledTime, status: form.status, notes: form.notes })
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Date</label>
          <input required type="date" value={form.date} onChange={e => set('date', e.target.value)} className={cls + ' w-full'} />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Time</label>
          <input required type="time" value={form.time} onChange={e => set('time', e.target.value)} className={cls + ' w-full'} />
        </div>
      </div>

      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Customer</label>
        <select required value={form.personId}
          onChange={e => { set('personId', e.target.value); set('petId', '') }}
          className={cls + ' w-full'}>
          <option value="">Select customer first...</option>
          {customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
      </div>

      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Pet</label>
        <select required value={form.petId} onChange={e => set('petId', e.target.value)}
          disabled={!form.personId}
          className={cls + ' w-full disabled:bg-slate-50 disabled:text-slate-400 disabled:cursor-not-allowed'}>
          <option value="">{form.personId ? 'Select pet...' : 'Select a customer first'}</option>
          {filteredPets.map(p => <option key={p.id} value={p.id}>{p.name}{p.breed ? ` (${p.breed})` : ''}</option>)}
        </select>
        {form.personId && filteredPets.length === 0 && (
          <p className="text-xs text-amber-600 mt-1">This customer has no registered pets yet.</p>
        )}
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Doctor</label>
          <select value={form.doctorId} onChange={e => set('doctorId', e.target.value)} className={cls + ' w-full'}>
            <option value="">No doctor assigned</option>
            {doctors.map(d => <option key={d.id} value={d.id}>Dr. {d.name}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Status</label>
          <select value={form.status} onChange={e => set('status', e.target.value)} className={cls + ' w-full'}>
            {STATUSES.map(s => <option key={s}>{s}</option>)}
          </select>
        </div>
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Notes</label>
        <textarea rows={2} value={form.notes} onChange={e => set('notes', e.target.value)} className={cls + ' w-full resize-none'} />
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
  const [doctors, setDoctors] = useState([])
  const [filterStatus, setFilterStatus] = useState('')
  const [modal, setModal] = useState(null)

  const load = (status = filterStatus) => {
    const params = status ? { status } : {}
    appointmentsApi.getAll(params).then(r => setAppointments(r.data)).catch(() => {})
  }

  useEffect(() => { load() }, [filterStatus])
  useEffect(() => {
    customersApi.getAll().then(r => setCustomers(r.data)).catch(() => {})
    petsApi.getAll().then(r => setPets(r.data)).catch(() => {})
    doctorsApi.getAll(true).then(r => setDoctors(r.data)).catch(() => {})
  }, [])

  const handleSave = async (form) => {
    modal === 'add' ? await appointmentsApi.create(form) : await appointmentsApi.update(modal.editing.id, form)
    setModal(null)
    load()
  }

  const handleStatusChange = async (appt, status) => {
    await appointmentsApi.update(appt.id, {
      personId: appt.person?.id, petId: appt.pet?.id,
      doctorId: appt.doctor?.id || null, scheduledTime: appt.scheduledTime,
      status, notes: appt.notes
    })
    load()
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this appointment?')) return
    await appointmentsApi.delete(id)
    load()
  }

  const fmtDT = (dt) => {
    if (!dt) return '—'
    const d = new Date(dt)
    return d.toLocaleDateString() + ' ' + d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h2 className="text-2xl font-bold text-slate-800">Appointments</h2>
        <button onClick={() => setModal('add')} className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">
          + New Appointment
        </button>
      </div>

      <div className="flex gap-3 mb-4 items-center">
        <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)} className={cls}>
          <option value="">All Status</option>
          {STATUSES.map(s => <option key={s}>{s}</option>)}
        </select>
        {filterStatus && (
          <button onClick={() => setFilterStatus('')} className="text-xs text-slate-500 hover:text-slate-700 px-2">Clear</button>
        )}
        <span className="ml-auto flex items-center gap-1.5 text-xs text-amber-600">
          <span className="inline-block w-3 h-3 rounded bg-amber-100 border border-amber-300"></span>
          Within 30 min
        </span>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Scheduled</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Customer</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Pet</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Doctor</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Status</th>
              <th className="px-5 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {appointments.length === 0 && (
              <tr><td colSpan={6} className="px-5 py-8 text-center text-slate-400 text-sm">No appointments found.</td></tr>
            )}
            {appointments.map(a => {
              const soon = isWithin30Min(a.scheduledTime)
              return (
                <tr key={a.id} className={soon ? 'bg-amber-50 border-l-4 border-l-amber-400' : 'hover:bg-slate-50'}>
                  <td className="px-5 py-3.5 whitespace-nowrap">
                    <span className={`font-medium ${soon ? 'text-amber-700' : 'text-slate-700'}`}>{fmtDT(a.scheduledTime)}</span>
                    {soon && <span className="ml-2 text-xs bg-amber-200 text-amber-800 px-1.5 py-0.5 rounded-full font-medium">Soon</span>}
                  </td>
                  <td className="px-5 py-3.5 text-slate-600">{a.person?.name}</td>
                  <td className="px-5 py-3.5 text-slate-600">{a.pet?.name}</td>
                  <td className="px-5 py-3.5 text-slate-600">{a.doctor ? `Dr. ${a.doctor.name}` : '—'}</td>
                  <td className="px-5 py-3.5">
                    <select value={a.status} onChange={e => handleStatusChange(a, e.target.value)}
                      className={`text-xs px-2 py-1 rounded-full font-medium border-0 cursor-pointer outline-none ${STATUS_COLORS[a.status] || ''}`}>
                      {STATUSES.map(s => <option key={s}>{s}</option>)}
                    </select>
                  </td>
                  <td className="px-5 py-3.5 text-right whitespace-nowrap">
                    <button onClick={() => setModal({ editing: a })} className="text-xs text-slate-400 hover:text-slate-700 mr-3">Edit</button>
                    <button onClick={() => handleDelete(a.id)} className="text-xs text-red-400 hover:text-red-600">Delete</button>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>

      {modal && (
        <Modal title={modal === 'add' ? 'New Appointment' : 'Edit Appointment'} onClose={() => setModal(null)}>
          <AppointmentForm
            initial={modal !== 'add' ? modal.editing : undefined}
            customers={customers} pets={pets} doctors={doctors}
            onSave={handleSave} onClose={() => setModal(null)}
          />
        </Modal>
      )}
    </div>
  )
}
