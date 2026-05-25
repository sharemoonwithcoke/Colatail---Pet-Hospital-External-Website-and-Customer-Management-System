import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { customersApi, petsApi, caseRecordsApi } from '../api'
import Modal from '../components/Modal'

const cls = "w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
const EMPTY_PET = { name: '', type: '', species: '', color: '', gender: '', birthday: '' }

function PetForm({ initial, onSave, onClose }) {
  const [form, setForm] = useState(initial || EMPTY_PET)
  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))
  return (
    <form onSubmit={e => { e.preventDefault(); onSave(form) }} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Name</label>
          <input required value={form.name} onChange={e => set('name', e.target.value)} className={cls} />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Type</label>
          <select value={form.type} onChange={e => set('type', e.target.value)} className={cls}>
            <option value="">Select...</option>
            <option>Dog</option><option>Cat</option><option>Bird</option><option>Rabbit</option><option>Other</option>
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Breed / Species</label>
          <input value={form.species} onChange={e => set('species', e.target.value)} className={cls} />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Color</label>
          <input value={form.color} onChange={e => set('color', e.target.value)} className={cls} />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Gender</label>
          <select value={form.gender} onChange={e => set('gender', e.target.value)} className={cls}>
            <option value="">Select...</option><option>Male</option><option>Female</option>
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Birthday</label>
          <input type="date" value={form.birthday || ''} onChange={e => set('birthday', e.target.value)} className={cls} />
        </div>
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

function RecordForm({ petName, onSave, onClose }) {
  const [form, setForm] = useState({ date: new Date().toISOString().split('T')[0], description: '' })
  return (
    <form onSubmit={e => { e.preventDefault(); onSave(form) }} className="space-y-3">
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Date</label>
        <input required type="date" value={form.date} onChange={e => setForm(p => ({ ...p, date: e.target.value }))} className={cls} />
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Description</label>
        <textarea required rows={4} value={form.description} onChange={e => setForm(p => ({ ...p, description: e.target.value }))}
          className={cls + ' resize-none'} placeholder="Describe the visit, diagnosis, treatment..." />
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

export default function CustomerDetail() {
  const { id } = useParams()
  const [customer, setCustomer] = useState(null)
  const [pets, setPets] = useState([])
  const [selectedPet, setSelectedPet] = useState(null)
  const [records, setRecords] = useState([])
  const [petModal, setPetModal] = useState(null)
  const [recordModal, setRecordModal] = useState(false)

  const loadCustomer = () => customersApi.getById(id).then(r => setCustomer(r.data)).catch(() => {})
  const loadPets = () => petsApi.getByOwner(id).then(r => setPets(r.data)).catch(() => {})
  const loadRecords = (petId) => caseRecordsApi.getByPet(petId).then(r => setRecords(r.data)).catch(() => {})

  useEffect(() => { loadCustomer(); loadPets() }, [id])

  const selectPet = (pet) => { setSelectedPet(pet); loadRecords(pet.id) }

  const handleSavePet = async (form) => {
    petModal === 'add'
      ? await petsApi.create({ ...form, ownerId: parseInt(id) })
      : await petsApi.update(petModal.editing.id, { ...form, ownerId: parseInt(id) })
    setPetModal(null)
    loadPets()
  }

  const handleDeletePet = async (petId) => {
    if (!confirm('Delete this pet?')) return
    await petsApi.delete(petId)
    if (selectedPet?.id === petId) { setSelectedPet(null); setRecords([]) }
    loadPets()
  }

  const handleSaveRecord = async (form) => {
    await caseRecordsApi.create({ ...form, petId: selectedPet.id })
    setRecordModal(false)
    loadRecords(selectedPet.id)
  }

  const handleDeleteRecord = async (rid) => {
    if (!confirm('Delete this record?')) return
    await caseRecordsApi.delete(rid)
    loadRecords(selectedPet.id)
  }

  if (!customer) return <div className="text-slate-400 text-sm">Loading...</div>

  return (
    <div>
      <div className="mb-5">
        <Link to="/customers" className="text-xs text-blue-500 hover:underline">← Back to Customers</Link>
        <h2 className="text-2xl font-bold text-slate-800 mt-1">{customer.firstName} {customer.lastName}</h2>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5 mb-5">
        <h3 className="font-semibold text-slate-700 mb-3 text-sm">Contact Information</h3>
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div><span className="text-slate-400 text-xs block">Email</span><span className="text-slate-700">{customer.email}</span></div>
          <div><span className="text-slate-400 text-xs block">Phone</span><span className="text-slate-700">{customer.phoneNumber}</span></div>
          {customer.address && (
            <div>
              <span className="text-slate-400 text-xs block">Address</span>
              <span className="text-slate-700">
                {[customer.address.street, customer.address.city, customer.address.state, customer.address.zipCode].filter(Boolean).join(', ')}
              </span>
            </div>
          )}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-5">
        <div className="bg-white rounded-xl shadow-sm border border-slate-100">
          <div className="flex items-center justify-between px-5 py-4 border-b">
            <h3 className="font-semibold text-slate-700 text-sm">Pets ({pets.length})</h3>
            <button onClick={() => setPetModal('add')} className="text-xs text-blue-600 hover:underline font-medium">+ Add Pet</button>
          </div>
          <div className="divide-y">
            {pets.length === 0 && <p className="p-5 text-xs text-slate-400">No pets registered yet.</p>}
            {pets.map(pet => (
              <div key={pet.id} onClick={() => selectPet(pet)}
                className={`p-4 flex items-center justify-between cursor-pointer hover:bg-slate-50 transition-colors ${selectedPet?.id === pet.id ? 'bg-blue-50 border-l-2 border-l-blue-500' : ''}`}>
                <div>
                  <p className="font-medium text-slate-700 text-sm">{pet.name}</p>
                  <p className="text-xs text-slate-400">{pet.type}{pet.species ? ` · ${pet.species}` : ''}</p>
                </div>
                <div className="flex gap-2">
                  <button onClick={e => { e.stopPropagation(); setPetModal({ editing: pet }) }}
                    className="text-xs text-slate-400 hover:text-slate-700">Edit</button>
                  <button onClick={e => { e.stopPropagation(); handleDeletePet(pet.id) }}
                    className="text-xs text-red-400 hover:text-red-600">Del</button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-slate-100">
          <div className="flex items-center justify-between px-5 py-4 border-b">
            <h3 className="font-semibold text-slate-700 text-sm">
              {selectedPet ? `Case Records — ${selectedPet.name}` : 'Case Records'}
            </h3>
            {selectedPet && (
              <button onClick={() => setRecordModal(true)} className="text-xs text-blue-600 hover:underline font-medium">+ Add Record</button>
            )}
          </div>
          {!selectedPet ? (
            <p className="p-5 text-xs text-slate-400">Select a pet to view medical records.</p>
          ) : (
            <div className="divide-y">
              {records.length === 0 && <p className="p-5 text-xs text-slate-400">No records found.</p>}
              {records.map(r => (
                <div key={r.id} className="p-4 flex justify-between gap-3">
                  <div>
                    <p className="text-xs text-slate-400 mb-1">{r.date}</p>
                    <p className="text-sm text-slate-700 whitespace-pre-wrap">{r.description}</p>
                  </div>
                  <button onClick={() => handleDeleteRecord(r.id)} className="text-xs text-red-400 hover:text-red-600 shrink-0">Del</button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {petModal && (
        <Modal title={petModal === 'add' ? 'Add Pet' : `Edit ${petModal.editing.name}`} onClose={() => setPetModal(null)}>
          <PetForm initial={petModal !== 'add' ? petModal.editing : undefined} onSave={handleSavePet} onClose={() => setPetModal(null)} />
        </Modal>
      )}

      {recordModal && (
        <Modal title={`Add Record for ${selectedPet.name}`} onClose={() => setRecordModal(false)}>
          <RecordForm petName={selectedPet.name} onSave={handleSaveRecord} onClose={() => setRecordModal(false)} />
        </Modal>
      )}
    </div>
  )
}
