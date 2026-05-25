import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authApi } from '../api'
import { useAuth } from '../AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '', totpCode: '' })
  const [needMfa, setNeedMfa] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const set = (f, v) => setForm(p => ({ ...p, [f]: v }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const payload = { username: form.username, password: form.password }
      if (needMfa && form.totpCode) payload.totpCode = parseInt(form.totpCode)
      const res = await authApi.login(payload)
      if (res.status === 206) { setNeedMfa(true); setLoading(false); return }
      login({ username: res.data.username, role: res.data.role, mfaEnabled: res.data.mfaEnabled }, res.data.token)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-sm">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-slate-800">Colatail</h1>
          <p className="text-slate-400 text-sm mt-1">Pet Hospital System</p>
        </div>

        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-2.5 rounded-lg">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {!needMfa ? (
            <>
              <div>
                <label className="block text-xs font-medium text-slate-600 mb-1">Username</label>
                <input required value={form.username} onChange={e => set('username', e.target.value)}
                  className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter username" autoFocus />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-600 mb-1">Password</label>
                <input required type="password" value={form.password} onChange={e => set('password', e.target.value)}
                  className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter password" />
              </div>
            </>
          ) : (
            <div>
              <p className="text-sm text-slate-600 mb-3">Enter the 6-digit code from your authenticator app.</p>
              <label className="block text-xs font-medium text-slate-600 mb-1">Authenticator Code</label>
              <input required value={form.totpCode} onChange={e => set('totpCode', e.target.value)}
                maxLength={6} inputMode="numeric"
                className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm text-center text-lg tracking-widest focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="000000" autoFocus />
            </div>
          )}

          <button type="submit" disabled={loading}
            className="w-full bg-blue-600 text-white rounded-lg py-2.5 text-sm font-medium hover:bg-blue-700 disabled:opacity-60 transition-colors">
            {loading ? 'Signing in...' : needMfa ? 'Verify' : 'Sign In'}
          </button>

          {needMfa && (
            <button type="button" onClick={() => setNeedMfa(false)}
              className="w-full text-slate-500 text-xs hover:text-slate-700">
              ← Back
            </button>
          )}
        </form>
      </div>
    </div>
  )
}
