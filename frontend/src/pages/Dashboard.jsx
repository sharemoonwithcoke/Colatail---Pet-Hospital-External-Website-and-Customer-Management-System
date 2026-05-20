import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { customersApi, appointmentsApi, todosApi } from '../api'

function StatCard({ label, value, icon, color, linkTo }) {
  const content = (
    <div className={`bg-white rounded-xl p-5 shadow-sm border border-slate-100 ${linkTo ? 'hover:shadow-md transition-shadow cursor-pointer' : ''}`}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-slate-500 text-xs font-medium uppercase tracking-wide">{label}</p>
          <p className={`text-3xl font-bold mt-1 ${color}`}>{value}</p>
        </div>
        <span className="text-3xl opacity-80">{icon}</span>
      </div>
    </div>
  )
  return linkTo ? <Link to={linkTo}>{content}</Link> : content
}

const STATUS_COLORS = {
  PENDING: 'bg-amber-100 text-amber-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700'
}

export default function Dashboard() {
  const [stats, setStats] = useState({ customers: 0, appointments: 0, pending: 0, todos: 0 })
  const [todayAppts, setTodayAppts] = useState([])

  useEffect(() => {
    const today = new Date().toISOString().split('T')[0]
    Promise.all([
      customersApi.getAll(),
      appointmentsApi.getAll(),
      todosApi.getAll()
    ]).then(([customers, appts, todos]) => {
      const todayList = appts.data.filter(a => a.date === today)
      setTodayAppts(todayList)
      setStats({
        customers: customers.data.length,
        appointments: appts.data.length,
        pending: appts.data.filter(a => a.status === 'PENDING').length,
        todos: todos.data.filter(t => !t.completed).length
      })
    }).catch(() => {})
  }, [])

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-800">Dashboard</h2>
        <p className="text-slate-500 text-sm mt-1">{new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard label="Total Customers" value={stats.customers} icon="👤" color="text-blue-600" linkTo="/customers" />
        <StatCard label="Total Appointments" value={stats.appointments} icon="📅" color="text-purple-600" linkTo="/appointments" />
        <StatCard label="Pending" value={stats.pending} icon="⏳" color="text-amber-600" linkTo="/appointments" />
        <StatCard label="Open Tasks" value={stats.todos} icon="✅" color="text-green-600" linkTo="/todos" />
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5">
        <h3 className="font-semibold text-slate-700 mb-4">Today's Appointments</h3>
        {todayAppts.length === 0 ? (
          <p className="text-slate-400 text-sm py-4 text-center">No appointments scheduled for today.</p>
        ) : (
          <div className="divide-y">
            {todayAppts.map(a => (
              <div key={a.appointmentId} className="py-3 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <span className="text-slate-400 text-sm w-16 shrink-0">{a.time}</span>
                  <div>
                    <span className="font-medium text-slate-700 text-sm">{a.person?.firstName} {a.person?.lastName}</span>
                    <span className="text-slate-400 text-xs mx-1.5">•</span>
                    <span className="text-slate-500 text-sm">{a.pet?.name}</span>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-slate-500 text-xs">Dr. {a.doctor}</span>
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
