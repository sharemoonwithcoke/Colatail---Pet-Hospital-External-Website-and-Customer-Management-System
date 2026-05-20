import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { customersApi } from '../api'
import Modal from '../components/Modal'

const EMPTY = {
  firstName: '', lastName: '', phoneNumber: '', email: '',
  address: { street: '', city: '', state: '', zipCode: '' }
}

function Field({ label, children }) {
  return (
    <div>
      <label className="block text-xs font-medium text-slate-600 mb-1">{label}</label>
      {children}
    </div>
  )
}

const cls = "w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"

function CustomerForm({ initial, onSave, onClose }) {
  const [form, setForm] = useState(initial || EMPTY)
  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))
  const setAddr = (f, v) => setForm(p => ({ ...p, address: { ...p.address, [f]: v } }))

  return (
    <form onSubmit={e => { e.preventDefault(); onSave(form) }} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        <Field label="First Name"><input required value={form.firstName} onChange={e => set('firstName', e.target.value)} className={cls} /></Field>
        <Field label="Last Name"><input required value={form.lastName} onChange={e => set('lastName', e.target.value)} className={cls} /></Field>
      </div>
      <Field label="Email"><input required type="email" value={form.email} onChange={e => set('email', e.target.value)} className={cls} /></Field>
      <Field label="Phone"><input required value={form.phoneNumber} onChange={e => set('phoneNumber', e.target.value)} className={cls} /></Field>
      <div className="grid grid-cols-2 gap-3">
        <Field label="Street"><input value={form.address?.street || ''} onChange={e => setAddr('street', e.target.value)} className={cls} /></Field>
        <Field label="City"><input value={form.address?.city || ''} onChange={e => setAddr('city', e.target.value)} className={cls} /></Field>
        <Field label="State"><input value={form.address?.state || ''} onChange={e => setAddr('state', e.target.value)} className={cls} /></Field>
        <Field label="Zip Code"><input value={form.address?.zipCode || ''} onChange={e => setAddr('zipCode', e.target.value)} className={cls} /></Field>
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

export default function Customers() {
  const [customers, setCustomers] = useState([])
  const [search, setSearch] = useState('')
  const [modal, setModal] = useState(null)

  const load = (q) => customersApi.getAll(q || undefined).then(r => setCustomers(r.data)).catch(() => {})

  useEffect(() => { load() }, [])

  const handleSearch = (e) => {
    const q = e.target.value
    setSearch(q)
    load(q)
  }

  const handleSave = async (form) => {
    modal === 'add' ? await customersApi.create(form) : await customersApi.update(modal.editing.id, form)
    setModal(null)
    load(search)
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this customer? All their pets and records will also be removed.')) return
    await customersApi.delete(id)
    load(search)
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h2 className="text-2xl font-bold text-slate-800">Customers</h2>
        <button onClick={() => setModal('add')} className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">
          + Add Customer
        </button>
      </div>

      <div className="mb-4">
        <input type="text" placeholder="Search by name, email, or phone..."
          value={search} onChange={handleSearch}
          className="w-full max-w-sm border border-slate-200 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left px-5 py-3 font-medium text-slate-500 text-xs uppercase">Name</th>
              <th className="text-left px-5 py-3 font-medium text-slate-500 text-xs uppercase">Email</th>
              <th className="text-left px-5 py-3 font-medium text-slate-500 text-xs uppercase">Phone</th>
              <th className="text-left px-5 py-3 font-medium text-slate-500 text-xs uppercase">Pets</th>
              <th className="px-5 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {customers.length === 0 && (
              <tr><td colSpan={5} className="px-5 py-8 text-center text-slate-400 text-sm">No customers found.</td></tr>
            )}
            {customers.map(c => (
              <tr key={c.id} className="hover:bg-slate-50">
                <td className="px-5 py-3.5">
                  <Link to={`/customers/${c.id}`} className="font-medium text-blue-600 hover:underline">
                    {c.firstName} {c.lastName}
                  </Link>
                </td>
                <td className="px-5 py-3.5 text-slate-600">{c.email}</td>
                <td className="px-5 py-3.5 text-slate-600">{c.phoneNumber}</td>
                <td className="px-5 py-3.5">
                  <span className="bg-slate-100 text-slate-600 text-xs px-2 py-0.5 rounded-full">{c.pets?.length || 0} pets</span>
                </td>
                <td className="px-5 py-3.5 text-right">
                  <button onClick={() => setModal({ editing: c })} className="text-slate-400 hover:text-slate-700 text-xs mr-3">Edit</button>
                  <button onClick={() => handleDelete(c.id)} className="text-red-400 hover:text-red-600 text-xs">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <Modal title={modal === 'add' ? 'Add Customer' : 'Edit Customer'} onClose={() => setModal(null)}>
          <CustomerForm initial={modal !== 'add' ? modal.editing : undefined} onSave={handleSave} onClose={() => setModal(null)} />
        </Modal>
      )}
    </div>
  )
}
