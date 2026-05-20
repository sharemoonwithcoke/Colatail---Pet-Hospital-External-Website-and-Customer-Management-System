import { Link, Outlet, useLocation } from 'react-router-dom'

const navItems = [
  { path: '/', label: 'Dashboard', icon: '📊' },
  { path: '/customers', label: 'Customers', icon: '👤' },
  { path: '/appointments', label: 'Appointments', icon: '📅' },
  { path: '/todos', label: 'To-Do List', icon: '✅' }
]

export default function Layout() {
  const location = useLocation()

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
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  active
                    ? 'bg-blue-600 text-white'
                    : 'text-slate-300 hover:bg-slate-700 hover:text-white'
                }`}
              >
                <span className="text-base">{item.icon}</span>
                {item.label}
              </Link>
            )
          })}
        </nav>
        <div className="p-4 border-t border-slate-700">
          <p className="text-slate-500 text-xs">Internal Tool v1.0</p>
        </div>
      </aside>
      <main className="flex-1 overflow-auto p-8">
        <Outlet />
      </main>
    </div>
  )
}
