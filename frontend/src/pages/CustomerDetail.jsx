import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { customersApi, petsApi, caseRecordsApi, wellnessLogsApi, doctorsApi, appointmentsApi } from '../api'
import { useAuth } from '../AuthContext'
import Modal from '../components/Modal'

const cls = "w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"

const STATUS_COLORS = {
  WEIGHT: 'bg-blue-100 text-blue-700',
  VACCINE: 'bg-green-100 text-green-700',
  DEWORMING: 'bg-purple-100 text-purple-700',
  OTHER: 'bg-slate-100 text-slate-700',
}

const APPT_STATUS_COLORS = {
  PENDING: 'bg-amber-100 text-amber-700',
  CONFIRMED: 'bg-blue-100 text-blue-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700',
}

const isWithin30Min = (scheduledTime) => {
  if (!scheduledTime) return false
  return Math.abs(new Date(scheduledTime) - Date.now()) <= 30 * 60 * 1000
}

function PetForm({ initial, onSave, onClose }) {
  const [form, setForm] = useState(initial || { name: '', breed: '', gender: '', birthday: '', photoUrl: '' })
  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))
  return (
    <form onSubmit={e => { e.preventDefault(); onSave(form) }} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Name</label>
          <input required value={form.name} onChange={e => set('name', e.target.value)} className={cls} />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Breed</label>
          <input value={form.breed || ''} onChange={e => set('breed', e.target.value)} className={cls} />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Gender</label>
          <select value={form.gender || ''} onChange={e => set('gender', e.target.value)} className={cls}>
            <option value="">Select...</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Birthday</label>
          <input type="date" value={form.birthday || ''} onChange={e => set('birthday', e.target.value)} className={cls} />
        </div>
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Photo URL</label>
        <input value={form.photoUrl || ''} onChange={e => set('photoUrl', e.target.value)} className={cls} placeholder="https://..." />
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

function CaseRecordForm({ petId, doctors, onSave, onClose }) {
  const [form, setForm] = useState({ doctorId: '', chiefComplaint: '', diagnosis: '', prescription: '', notes: '' })
  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))
  return (
    <form onSubmit={e => { e.preventDefault(); onSave({ ...form, petId }) }} className="space-y-3">
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Doctor</label>
        <select required value={form.doctorId} onChange={e => set('doctorId', e.target.value)} className={cls}>
          <option value="">Select doctor...</option>
          {doctors.map(d => <option key={d.id} value={d.id}>Dr. {d.name}{d.specialty ? ` (${d.specialty})` : ''}</option>)}
        </select>
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Chief Complaint</label>
        <textarea rows={2} value={form.chiefComplaint} onChange={e => set('chiefComplaint', e.target.value)} className={cls + ' resize-none'} />
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Diagnosis</label>
        <textarea rows={2} value={form.diagnosis} onChange={e => set('diagnosis', e.target.value)} className={cls + ' resize-none'} />
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Prescription</label>
        <textarea rows={2} value={form.prescription} onChange={e => set('prescription', e.target.value)} className={cls + ' resize-none'} />
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Notes</label>
        <textarea rows={2} value={form.notes} onChange={e => set('notes', e.target.value)} className={cls + ' resize-none'} />
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

function WellnessLogForm({ petId, onSave, onClose }) {
  const [form, setForm] = useState({ type: 'WEIGHT', value: '', notes: '' })
  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))
  return (
    <form onSubmit={e => { e.preventDefault(); onSave({ ...form, petId }) }} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Type</label>
          <select value={form.type} onChange={e => set('type', e.target.value)} className={cls}>
            <option value="WEIGHT">Weight</option>
            <option value="VACCINE">Vaccine</option>
            <option value="DEWORMING">Deworming</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Value</label>
          <input value={form.value} onChange={e => set('value', e.target.value)} className={cls} placeholder="e.g. 5.2 kg, Rabies" />
        </div>
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Notes</label>
        <textarea rows={2} value={form.notes} onChange={e => set('notes', e.target.value)} className={cls + ' resize-none'} />
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

function PetDetail({ pet, doctors, isAdmin, onDelete }) {
  const [tab, setTab] = useState('records')
  const [records, setRecords] = useState([])
  const [logs, setLogs] = useState([])
  const [recordModal, setRecordModal] = useState(false)
  const [logModal, setLogModal] = useState(false)

  const loadRecords = () => caseRecordsApi.getByPet(pet.id).then(r => setRecords(r.data)).catch(() => {})
  const loadLogs = () => wellnessLogsApi.getByPet(pet.id).then(r => setLogs(r.data)).catch(() => {})

  useEffect(() => { loadRecords(); loadLogs() }, [pet.id])

  const handleSaveRecord = async (form) => {
    await caseRecordsApi.create(form)
    setRecordModal(false)
    loadRecords()
  }

  const handleSaveLog = async (form) => {
    await wellnessLogsApi.create(form)
    setLogModal(false)
    loadLogs()
  }

  const fmtDate = (dt) => dt ? new Date(dt).toLocaleDateString() : ''

  return (
    <div className="mt-4">
      <div className="flex gap-2 mb-3 border-b">
        {['records', 'wellness'].map(t => (
          <button key={t} onClick={() => setTab(t)}
            className={`px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors ${
              tab === t ? 'border-blue-600 text-blue-600' : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}>
            {t === 'records' ? `Case Records (${records.length})` : `Wellness Logs (${logs.length})`}
          </button>
        ))}
      </div>

      {tab === 'records' && (
        <div>
          <div className="flex justify-end mb-2">
            <button onClick={() => setRecordModal(true)} className="text-xs text-blue-600 hover:underline font-medium">+ Add Record</button>
          </div>
          {records.length === 0 && <p className="text-xs text-slate-400 py-3">No case records.</p>}
          {records.map(r => (
            <div key={r.id} className="border border-slate-100 rounded-lg p-4 mb-3">
              <div className="flex justify-between items-start mb-2">
                <div className="text-xs text-slate-400">{fmtDate(r.visitedAt)} · Dr. {r.doctor?.name}</div>
                {isAdmin && <button onClick={async () => { await caseRecordsApi.delete(r.id); loadRecords() }} className="text-xs text-red-400 hover:text-red-600">Delete</button>}
              </div>
              {r.chiefComplaint && <p className="text-xs text-slate-500 mb-1"><span className="font-medium">Complaint:</span> {r.chiefComplaint}</p>}
              {r.diagnosis && <p className="text-xs text-slate-500 mb-1"><span className="font-medium">Diagnosis:</span> {r.diagnosis}</p>}
              {r.prescription && <p className="text-xs text-slate-500 mb-1"><span className="font-medium">Rx:</span> {r.prescription}</p>}
              {r.notes && <p className="text-xs text-slate-500"><span className="font-medium">Notes:</span> {r.notes}</p>}
            </div>
          ))}
        </div>
      )}

      {tab === 'wellness' && (
        <div>
          <div className="flex justify-end mb-2">
            <button onClick={() => setLogModal(true)} className="text-xs text-blue-600 hover:underline font-medium">+ Add Log</button>
          </div>
          {logs.length === 0 && <p className="text-xs text-slate-400 py-3">No wellness logs.</p>}
          {logs.map(log => (
            <div key={log.id} className="flex items-center justify-between py-2 border-b border-slate-50">
              <div className="flex items-center gap-3">
                <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${STATUS_COLORS[log.type] || 'bg-slate-100 text-slate-600'}`}>{log.type}</span>
                <span className="text-sm text-slate-700">{log.value}</span>
                {log.notes && <span className="text-xs text-slate-400">— {log.notes}</span>}
              </div>
              <div className="flex items-center gap-3">
                <span className="text-xs text-slate-400">{fmtDate(log.loggedAt)}</span>
                <button onClick={async () => { await wellnessLogsApi.delete(log.id); loadLogs() }} className="text-xs text-red-400 hover:text-red-600">×</button>
              </div>
            </div>
          ))}
        </div>
      )}

      {recordModal && (
        <Modal title={`Add Case Record — ${pet.name}`} onClose={() => setRecordModal(false)}>
          <CaseRecordForm petId={pet.id} doctors={doctors} onSave={handleSaveRecord} onClose={() => setRecordModal(false)} />
        </Modal>
      )}
      {logModal && (
        <Modal title={`Add Wellness Log — ${pet.name}`} onClose={() => setLogModal(false)}>
          <WellnessLogForm petId={pet.id} onSave={handleSaveLog} onClose={() => setLogModal(false)} />
        </Modal>
      )}
    </div>
  )
}

export default function CustomerDetail() {
  const { id } = useParams()
  const { isAdmin } = useAuth()
  const [customer, setCustomer] = useState(null)
  const [pets, setPets] = useState([])
  const [selectedPet, setSelectedPet] = useState(null)
  const [doctors, setDoctors] = useState([])
  const [petModal, setPetModal] = useState(null)
  const [appointments, setAppointments] = useState([])

  const loadCustomer = () => customersApi.getById(id).then(r => setCustomer(r.data)).catch(() => {})
  const loadPets = () => petsApi.getByOwner(id).then(r => setPets(r.data)).catch(() => {})
  const loadDoctors = () => doctorsApi.getAll(true).then(r => setDoctors(r.data)).catch(() => {})
  const loadAppointments = () => appointmentsApi.getAll({ personId: id }).then(r => setAppointments(r.data)).catch(() => {})

  useEffect(() => { loadCustomer(); loadPets(); loadDoctors(); loadAppointments() }, [id])

  const handleSavePet = async (form) => {
    if (petModal === 'add') {
      await petsApi.create({ ...form, ownerId: id })
    } else {
      await petsApi.update(petModal.editing.id, form)
    }
    setPetModal(null)
    loadPets()
  }

  const handleDeletePet = async (petId) => {
    if (!confirm('Delete this pet?')) return
    await petsApi.delete(petId)
    if (selectedPet?.id === petId) setSelectedPet(null)
    loadPets()
  }

  if (!customer) return <div className="text-slate-400 text-sm">Loading...</div>

  return (
    <div>
      <div className="mb-5">
        <Link to="/customers" className="text-xs text-blue-500 hover:underline">← Back to Customers</Link>
        <h2 className="text-2xl font-bold text-slate-800 mt-1">{customer.name}</h2>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5 mb-5">
        <h3 className="font-semibold text-slate-700 mb-3 text-sm">Contact Information</h3>
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div><span className="text-slate-400 text-xs block">Email</span><span className="text-slate-700">{customer.email}</span></div>
          <div><span className="text-slate-400 text-xs block">Phone</span><span className="text-slate-700">{customer.phone || '—'}</span></div>
          <div><span className="text-slate-400 text-xs block">Address</span><span className="text-slate-700">{customer.address || '—'}</span></div>
        </div>
      </div>

      <div className="grid grid-cols-5 gap-5">
        <div className="col-span-2 bg-white rounded-xl shadow-sm border border-slate-100">
          <div className="flex items-center justify-between px-5 py-4 border-b">
            <h3 className="font-semibold text-slate-700 text-sm">Pets ({pets.length})</h3>
            <button onClick={() => setPetModal('add')} className="text-xs text-blue-600 hover:underline font-medium">+ Add</button>
          </div>
          <div className="divide-y">
            {pets.length === 0 && <p className="p-5 text-xs text-slate-400">No pets registered yet.</p>}
            {pets.map(pet => (
              <div key={pet.id} onClick={() => setSelectedPet(selectedPet?.id === pet.id ? null : pet)}
                className={`p-4 flex items-center justify-between cursor-pointer hover:bg-slate-50 transition-colors ${
                  selectedPet?.id === pet.id ? 'bg-blue-50 border-l-2 border-l-blue-500' : ''
                }`}>
                <div>
                  <p className="font-medium text-slate-700 text-sm">{pet.name}</p>
                  <p className="text-xs text-slate-400">{[pet.breed, pet.gender].filter(Boolean).join(' · ')}</p>
                </div>
                <div className="flex gap-2" onClick={e => e.stopPropagation()}>
                  <button onClick={() => setPetModal({ editing: pet })} className="text-xs text-slate-400 hover:text-slate-700">Edit</button>
                  {isAdmin && <button onClick={() => handleDeletePet(pet.id)} className="text-xs text-red-400 hover:text-red-600">Del</button>}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="col-span-3 bg-white rounded-xl shadow-sm border border-slate-100 p-5">
          {!selectedPet ? (
            <p className="text-sm text-slate-400 py-4">Select a pet to view records and wellness logs.</p>
          ) : (
            <>
              <div className="flex items-center justify-between mb-2">
                <h3 className="font-semibold text-slate-700">{selectedPet.name}</h3>
                <button onClick={() => setSelectedPet(null)} className="text-xs text-slate-400 hover:text-slate-600">× Close</button>
              </div>
              <PetDetail pet={selectedPet} doctors={doctors} isAdmin={isAdmin} />
            </>
          )}
        </div>
      </div>

      <div className="mt-5 bg-white rounded-xl shadow-sm border border-slate-100">
        <div className="px-5 py-4 border-b flex items-center justify-between">
          <h3 className="font-semibold text-slate-700 text-sm">Appointment History ({appointments.length})</h3>
          <span className="flex items-center gap-1.5 text-xs text-amber-600">
            <span className="inline-block w-3 h-3 rounded bg-amber-100 border border-amber-300"></span>
            Within 30 min
          </span>
        </div>
        {appointments.length === 0 ? (
          <p className="px-5 py-6 text-sm text-slate-400">No appointments found for this customer.</p>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-slate-50 border-b">
              <tr>
                <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Scheduled</th>
                <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Pet</th>
                <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Doctor</th>
                <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Status</th>
                <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Notes</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {[...appointments].sort((a, b) => new Date(b.scheduledTime) - new Date(a.scheduledTime)).map(a => {
                const soon = isWithin30Min(a.scheduledTime)
                const isPast = a.scheduledTime && new Date(a.scheduledTime) < Date.now()
                const dt = a.scheduledTime ? new Date(a.scheduledTime) : null
                const fmtDT = dt ? dt.toLocaleDateString() + ' ' + dt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '—'
                return (
                  <tr key={a.id} className={soon ? 'bg-amber-50 border-l-4 border-l-amber-400' : isPast ? '' : 'bg-blue-50/30'}>
                    <td className="px-5 py-3 whitespace-nowrap">
                      <span className={`font-medium ${soon ? 'text-amber-700' : isPast ? 'text-slate-500' : 'text-blue-700'}`}>{fmtDT}</span>
                      {soon && <span className="ml-2 text-xs bg-amber-200 text-amber-800 px-1.5 py-0.5 rounded-full font-medium">Soon</span>}
                      {!soon && !isPast && <span className="ml-2 text-xs bg-blue-100 text-blue-700 px-1.5 py-0.5 rounded-full font-medium">Upcoming</span>}
                    </td>
                    <td className="px-5 py-3 text-slate-600">{a.pet?.name || '—'}</td>
                    <td className="px-5 py-3 text-slate-600">{a.doctor ? `Dr. ${a.doctor.name}` : '—'}</td>
                    <td className="px-5 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${APPT_STATUS_COLORS[a.status] || 'bg-slate-100 text-slate-600'}`}>
                        {a.status}
                      </span>
                    </td>
                    <td className="px-5 py-3 text-slate-400 text-xs">{a.notes || '—'}</td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>

      {petModal && (
        <Modal title={petModal === 'add' ? 'Add Pet' : `Edit ${petModal.editing.name}`} onClose={() => setPetModal(null)}>
          <PetForm initial={petModal !== 'add' ? petModal.editing : undefined} onSave={handleSavePet} onClose={() => setPetModal(null)} />
        </Modal>
      )}
    </div>
  )
}
