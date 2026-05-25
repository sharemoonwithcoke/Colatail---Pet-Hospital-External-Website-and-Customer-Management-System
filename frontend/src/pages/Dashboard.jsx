import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { customersApi, appointmentsApi, petsApi } from '../api'

const STATUS_COLORS = {
  PENDING: 'bg-amber-100 text-amber-700',
  CONFIRMED: 'bg-blue-100 text-blue-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700',
}

export default function Dashboard() {
  const [stats, setStats] = useState({ customers: 0, pets: 0, appointments: 0, pending: 0 })
  const [todayAppts, setTodayAppts] = useState([])

  useEffect(() => {
    const today = new Date().toISOString().split('T')[0]
    Promise.all([
      customersApi.getAll(),
      petsApi.getAll(),
      appointmentsApi.getAll(),
    ]).then(([c, p, a]) => {
      const todays = a.data.filter(x => x.scheduledTime?.startsWith(today))
      setTodayAppts(todays)
      setStats({
        customers: c.data.length,
        pets: p.data.length,
        appointments: a.data.length,
        pending: a.data.filter(x => x.status === 'PENDING').length,
      })
    }).catch(() => {})
  }, [])

  const cards = [
    { label: 'Customers', value: stats.customers, icon: '👤', to: '/customers', color: 'text-blue-600' },
    { label: 'Pets', value: stats.pets, icon: '🐾', to: '/customers', color: 'text-green-600' },
    { label: 'Appointments', value: stats.appointments, icon: '📅', to: '/appointments', color: 'text-purple-600' },
    { label: 'Pending', value: stats.pending, icon: '⏳', to: '/appointments', color: 'text-amber-600' },
  ]

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-800">Dashboard</h2>
        <p className="text-slate-500 text-sm mt-1">
          {new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
        </p>
      </div>

      <div className="grid grid-cols-4 gap-4 mb-8">
        {cards.map(c => (
          <Link key={c.label} to={c.to}
            className="bg-white rounded-xl shadow-sm border border-slate-100 p-5 hover:shadow-md transition-shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-slate-500 text-xs font-medium uppercase tracking-wide">{c.label}</p>
                <p className={`text-3xl font-bold mt-1 ${c.color}`}>{c.value}</p>
              </div>
              <span className="text-3xl opacity-80">{c.icon}</span>
            </div>
          </Link>
        ))}
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5">
        <h3 className="font-semibold text-slate-700 mb-4">Today's Appointments</h3>
        {todayAppts.length === 0 ? (
          <p className="text-slate-400 text-sm py-4 text-center">No appointments scheduled for today.</p>
        ) : (
          <div className="divide-y">
            {todayAppts.map(a => (
              <div key={a.id} className="py-3 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <span className="text-slate-400 text-sm w-14 shrink-0">
                    {a.scheduledTime ? new Date(a.scheduledTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''}
                  </span>
                  <div>
                    <span className="font-medium text-slate-700 text-sm">{a.person?.name}</span>
                    <span className="text-slate-400 text-xs mx-1.5">·</span>
                    <span className="text-slate-500 text-sm">{a.pet?.name}</span>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  {a.doctor && <span className="text-slate-500 text-xs">Dr. {a.doctor.name}</span>}
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${STATUS_COLORS[a.status] || 'bg-slate-100 text-slate-600'}`}>
                    {a.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
