import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'

export default function Layout() {
  const location = useLocation()
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()

  const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/customers', label: 'Customers', icon: '👤' },
    { path: '/appointments', label: 'Appointments', icon: '📅' },
    ...(isAdmin ? [{ path: '/admin', label: 'Admin Panel', icon: '⚙️' }] : []),
  ]

  const handleLogout = () => { logout(); navigate('/login') }

  return (
    <div className="flex h-screen bg-slate-50">
      <aside className="w-60 bg-slate-800 flex flex-col flex-shrink-0">
        <div className="p-5 border-b border-slate-700">
          <h1 className="text-white font-bold text-lg">Colatail</h1>
          <p className="text-slate-400 text-xs mt-0.5">Pet Hospital System</p>
        </div>
        <nav className="flex-1 p-3 space-y-0.5">
          {navItems.map(item => {
            const active = location.pathname === item.path ||
              (item.path !== '/' && location.pathname.startsWith(item.path))
            return (
              <Link key={item.path} to={item.path}
                className={`flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  active ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-700 hover:text-white'
                }`}>
                <span className="text-base">{item.icon}</span>
                {item.label}
              </Link>
            )
          })}
        </nav>
        <div className="p-4 border-t border-slate-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-slate-300 text-xs font-medium">{user?.username}</p>
              <p className="text-slate-500 text-xs">{user?.role}</p>
            </div>
            <button onClick={handleLogout}
              className="text-slate-400 hover:text-white text-xs px-2 py-1 rounded hover:bg-slate-700 transition-colors">
              Sign out
            </button>
          </div>
        </div>
      </aside>
      <main className="flex-1 overflow-auto p-8">
        <Outlet />
      </main>
    </div>
  )
}
