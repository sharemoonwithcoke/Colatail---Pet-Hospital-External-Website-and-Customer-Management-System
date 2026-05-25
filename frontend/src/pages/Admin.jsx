import { useState, useEffect } from 'react'
import { adminApi, doctorsApi, authApi } from '../api'
import Modal from '../components/Modal'
import QRCode from 'qrcode'

const cls = "w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
const tabs = ['Users', 'Doctors', 'Audit Log', 'My Account']

// ── Users tab ────────────────────────────────────────────
function UsersTab() {
  const [users, setUsers] = useState([])
  const [modal, setModal] = useState(null)
  const [form, setForm] = useState({ username: '', password: '', role: 'STAFF' })

  const load = () => adminApi.listUsers().then(r => setUsers(r.data)).catch(() => {})
  useEffect(() => { load() }, [])

  const handleCreate = async (e) => {
    e.preventDefault()
    await adminApi.createUser(form)
    setModal(null)
    setForm({ username: '', password: '', role: 'STAFF' })
    load()
  }

  const handleDelete = async (id, username) => {
    if (!confirm(`Delete user "${username}"? This cannot be undone.`)) return
    if (!confirm(`Are you sure? User "${username}" will be permanently deleted.`)) return
    await adminApi.deleteUser(id)
    load()
  }

  const handleToggleActive = async (user) => {
    await adminApi.setActive(user.id, !user.active)
    load()
  }

  const handleChangeRole = async (user) => {
    const newRole = user.role === 'ADMIN' ? 'STAFF' : 'ADMIN'
    if (!confirm(`Change ${user.username}'s role to ${newRole}?`)) return
    await adminApi.changeRole(user.id, newRole)
    load()
  }

  return (
    <div>
      <div className="flex justify-end mb-4">
        <button onClick={() => setModal('add')} className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">
          + Create User
        </button>
      </div>
      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Username</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Role</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">MFA</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Status</th>
              <th className="px-5 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {users.map(u => (
              <tr key={u.id} className="hover:bg-slate-50">
                <td className="px-5 py-3.5 font-medium text-slate-700">{u.username}</td>
                <td className="px-5 py-3.5">
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${u.role === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-slate-100 text-slate-600'}`}>
                    {u.role}
                  </span>
                </td>
                <td className="px-5 py-3.5">
                  <span className={`text-xs ${u.mfaEnabled ? 'text-green-600' : 'text-slate-400'}`}>
                    {u.mfaEnabled ? 'Enabled' : 'Disabled'}
                  </span>
                </td>
                <td className="px-5 py-3.5">
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${u.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                    {u.active ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="px-5 py-3.5 text-right flex items-center justify-end gap-3">
                  <button onClick={() => handleChangeRole(u)} className="text-xs text-slate-400 hover:text-slate-700">
                    {u.role === 'ADMIN' ? '→STAFF' : '→ADMIN'}
                  </button>
                  <button onClick={() => handleToggleActive(u)} className="text-xs text-slate-400 hover:text-slate-700">
                    {u.active ? 'Disable' : 'Enable'}
                  </button>
                  <button onClick={() => handleDelete(u.id, u.username)} className="text-xs text-red-400 hover:text-red-600">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal === 'add' && (
        <Modal title="Create User" onClose={() => setModal(null)}>
          <form onSubmit={handleCreate} className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Username</label>
              <input required value={form.username} onChange={e => setForm(f => ({ ...f, username: e.target.value }))} className={cls} />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Password</label>
              <input required type="password" value={form.password} onChange={e => setForm(f => ({ ...f, password: e.target.value }))} className={cls} />
            </div>
            <p className="text-xs text-slate-400">New accounts are created as Staff. Role can be promoted after creation.</p>
            <div className="flex justify-end gap-2 pt-2">
              <button type="button" onClick={() => setModal(null)} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
              <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Create</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}

// ── Doctors tab ──────────────────────────────────────────
function DoctorsTab() {
  const [doctors, setDoctors] = useState([])
  const [modal, setModal] = useState(null)

  const load = () => doctorsApi.getAll().then(r => setDoctors(r.data)).catch(() => {})
  useEffect(() => { load() }, [])

  const handleSave = async (form) => {
    modal === 'add' ? await doctorsApi.create(form) : await doctorsApi.update(modal.editing.id, form)
    setModal(null)
    load()
  }

  const handleDelete = async (id, name) => {
    if (!confirm(`Delete Dr. ${name}?`)) return
    await doctorsApi.delete(id)
    load()
  }

  return (
    <div>
      <div className="flex justify-end mb-4">
        <button onClick={() => setModal('add')} className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">
          + Add Doctor
        </button>
      </div>
      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Name</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Specialty</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Status</th>
              <th className="px-5 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {doctors.map(d => (
              <tr key={d.id} className="hover:bg-slate-50">
                <td className="px-5 py-3.5 font-medium text-slate-700">Dr. {d.name}</td>
                <td className="px-5 py-3.5 text-slate-600">{d.specialty || '—'}</td>
                <td className="px-5 py-3.5">
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${d.active ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-500'}`}>
                    {d.active ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="px-5 py-3.5 text-right flex items-center justify-end gap-3">
                  <button onClick={() => setModal({ editing: d })} className="text-xs text-slate-400 hover:text-slate-700">Edit</button>
                  <button onClick={() => handleDelete(d.id, d.name)} className="text-xs text-red-400 hover:text-red-600">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <Modal title={modal === 'add' ? 'Add Doctor' : `Edit Dr. ${modal.editing.name}`} onClose={() => setModal(null)}>
          <DoctorForm initial={modal !== 'add' ? modal.editing : null} onSave={handleSave} onClose={() => setModal(null)} />
        </Modal>
      )}
    </div>
  )
}

function DoctorForm({ initial, onSave, onClose }) {
  const [form, setForm] = useState(initial || { name: '', specialty: '', active: true })
  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))
  return (
    <form onSubmit={e => { e.preventDefault(); onSave(form) }} className="space-y-3">
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Name</label>
        <input required value={form.name} onChange={e => set('name', e.target.value)} className={cls} />
      </div>
      <div>
        <label className="block text-xs font-medium text-slate-600 mb-1">Specialty</label>
        <input value={form.specialty || ''} onChange={e => set('specialty', e.target.value)} className={cls} />
      </div>
      <div className="flex items-center gap-2">
        <input type="checkbox" id="active" checked={form.active} onChange={e => set('active', e.target.checked)} />
        <label htmlFor="active" className="text-sm text-slate-600">Active</label>
      </div>
      <div className="flex justify-end gap-2 pt-2">
        <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-lg">Cancel</button>
        <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
      </div>
    </form>
  )
}

// ── Audit Log tab ────────────────────────────────────────
function AuditLogTab() {
  const [logs, setLogs] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const load = (p = 0) => adminApi.getAuditLogs(p).then(r => {
    setLogs(r.data.content)
    setTotalPages(r.data.totalPages)
  }).catch(() => {})

  useEffect(() => { load(page) }, [page])

  return (
    <div>
      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Time</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">User</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Action</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-slate-500 uppercase">Detail</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {logs.length === 0 && (
              <tr><td colSpan={4} className="px-5 py-8 text-center text-slate-400 text-sm">No audit logs.</td></tr>
            )}
            {logs.map(log => (
              <tr key={log.id} className="hover:bg-slate-50">
                <td className="px-5 py-3.5 text-slate-400 text-xs whitespace-nowrap">
                  {log.createdAt ? new Date(log.createdAt).toLocaleString() : ''}
                </td>
                <td className="px-5 py-3.5 font-medium text-slate-700">{log.username}</td>
                <td className="px-5 py-3.5">
                  <span className="bg-slate-100 text-slate-600 text-xs px-2 py-0.5 rounded font-mono">{log.action}</span>
                </td>
                <td className="px-5 py-3.5 text-slate-500 text-xs">{log.detail}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {totalPages > 1 && (
        <div className="flex gap-2 justify-center mt-4">
          <button disabled={page === 0} onClick={() => setPage(p => p - 1)} className="px-3 py-1 text-sm border rounded disabled:opacity-40">←</button>
          <span className="px-3 py-1 text-sm text-slate-600">{page + 1} / {totalPages}</span>
          <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)} className="px-3 py-1 text-sm border rounded disabled:opacity-40">→</button>
        </div>
      )}
    </div>
  )
}

// ── My Account tab ───────────────────────────────────────
function AccountTab() {
  const [pwForm, setPwForm] = useState({ currentPassword: '', newPassword: '', confirm: '' })
  const [mfaStep, setMfaStep] = useState(null)
  const [mfaData, setMfaData] = useState(null)
  const [mfaCode, setMfaCode] = useState('')
  const [qrUrl, setQrUrl] = useState(null)
  const [me, setMe] = useState(null)
  const [msg, setMsg] = useState('')

  const loadMe = () => authApi.me().then(r => setMe(r.data)).catch(() => {})

  useEffect(() => {
    if (mfaData?.otpauthUri) {
      QRCode.toDataURL(mfaData.otpauthUri, { width: 200, margin: 2 })
        .then(url => setQrUrl(url))
        .catch(() => setQrUrl(null))
    } else {
      setQrUrl(null)
    }
  }, [mfaData])
  useEffect(() => { loadMe() }, [])

  const handleChangePassword = async (e) => {
    e.preventDefault()
    if (pwForm.newPassword !== pwForm.confirm) { setMsg('Passwords do not match'); return }
    try {
      await authApi.changePassword({ currentPassword: pwForm.currentPassword, newPassword: pwForm.newPassword })
      setMsg('Password changed successfully')
      setPwForm({ currentPassword: '', newPassword: '', confirm: '' })
    } catch (err) {
      setMsg(err.response?.data?.error || 'Failed')
    }
  }

  const handleSetupMfa = async () => {
    const res = await authApi.mfaSetup()
    setMfaData(res.data)
    setMfaStep('confirm')
  }

  const handleConfirmMfa = async () => {
    try {
      await authApi.mfaConfirm(parseInt(mfaCode))
      setMfaStep(null); setMfaCode(''); setMfaData(null)
      loadMe(); setMsg('MFA enabled')
    } catch { setMsg('Invalid code') }
  }

  const handleDisableMfa = async () => {
    if (!mfaCode) return
    try {
      await authApi.mfaDisable(parseInt(mfaCode))
      setMfaStep(null); setMfaCode('')
      loadMe(); setMsg('MFA disabled')
    } catch { setMsg('Invalid code') }
  }

  return (
    <div className="max-w-md space-y-6">
      {msg && <div className="bg-blue-50 border border-blue-200 text-blue-700 text-sm px-4 py-2.5 rounded-lg">{msg}</div>}

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5">
        <h3 className="font-semibold text-slate-700 mb-4 text-sm">Change Password</h3>
        <form onSubmit={handleChangePassword} className="space-y-3">
          <div>
            <label className="block text-xs font-medium text-slate-600 mb-1">Current Password</label>
            <input required type="password" value={pwForm.currentPassword} onChange={e => setPwForm(f => ({ ...f, currentPassword: e.target.value }))} className={cls} />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-600 mb-1">New Password</label>
            <input required type="password" value={pwForm.newPassword} onChange={e => setPwForm(f => ({ ...f, newPassword: e.target.value }))} className={cls} />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-600 mb-1">Confirm New Password</label>
            <input required type="password" value={pwForm.confirm} onChange={e => setPwForm(f => ({ ...f, confirm: e.target.value }))} className={cls} />
          </div>
          <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">Change Password</button>
        </form>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5">
        <h3 className="font-semibold text-slate-700 mb-1 text-sm">Two-Factor Authentication</h3>
        <p className="text-xs text-slate-400 mb-4">Status: {me?.mfaEnabled ? 'Enabled' : 'Disabled'}</p>

        {!mfaStep && (
          <button onClick={() => me?.mfaEnabled ? setMfaStep('disable') : handleSetupMfa()}
            className={`px-4 py-2 rounded-lg text-sm font-medium ${me?.mfaEnabled ? 'bg-red-50 text-red-600 hover:bg-red-100' : 'bg-green-50 text-green-700 hover:bg-green-100'}`}>
            {me?.mfaEnabled ? 'Disable MFA' : 'Enable MFA'}
          </button>
        )}

        {mfaStep === 'confirm' && mfaData && (
          <div className="space-y-3">
            <p className="text-xs text-slate-600">Scan the QR code with Google Authenticator or Authy, then enter the 6-digit code to confirm:</p>
            {qrUrl
              ? <img src={qrUrl} alt="MFA QR Code" className="w-48 h-48 mx-auto rounded-lg border border-slate-200 p-1 bg-white" />
              : <div className="bg-slate-50 rounded-lg p-3 font-mono text-xs break-all text-slate-600">{mfaData.secret}</div>
            }
            <p className="text-xs text-slate-400 text-center">Can't scan? Enter this secret manually: <span className="font-mono">{mfaData.secret}</span></p>
            <input value={mfaCode} onChange={e => setMfaCode(e.target.value)} maxLength={6} inputMode="numeric"
              placeholder="Enter 6-digit code" className={cls} />
            <div className="flex gap-2">
              <button onClick={handleConfirmMfa} className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm">Confirm</button>
              <button onClick={() => { setMfaStep(null); setMfaCode('') }} className="text-slate-500 px-4 py-2 text-sm hover:bg-slate-100 rounded-lg">Cancel</button>
            </div>
          </div>
        )}

        {mfaStep === 'disable' && (
          <div className="space-y-3">
            <input value={mfaCode} onChange={e => setMfaCode(e.target.value)} maxLength={6} inputMode="numeric"
              placeholder="Enter current 6-digit code" className={cls} />
            <div className="flex gap-2">
              <button onClick={handleDisableMfa} className="bg-red-600 text-white px-4 py-2 rounded-lg text-sm">Disable MFA</button>
              <button onClick={() => { setMfaStep(null); setMfaCode('') }} className="text-slate-500 px-4 py-2 text-sm hover:bg-slate-100 rounded-lg">Cancel</button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

// ── Main Admin page ──────────────────────────────────────
export default function Admin() {
  const [tab, setTab] = useState(0)

  return (
    <div>
      <h2 className="text-2xl font-bold text-slate-800 mb-5">Admin Panel</h2>
      <div className="flex gap-1 mb-6 border-b">
        {tabs.map((t, i) => (
          <button key={t} onClick={() => setTab(i)}
            className={`px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors ${
              tab === i ? 'border-blue-600 text-blue-600' : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}>{t}</button>
        ))}
      </div>
      {tab === 0 && <UsersTab />}
      {tab === 1 && <DoctorsTab />}
      {tab === 2 && <AuditLogTab />}
      {tab === 3 && <AccountTab />}
    </div>
  )
}
