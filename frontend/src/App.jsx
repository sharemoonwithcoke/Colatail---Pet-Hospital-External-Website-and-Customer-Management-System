import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import Customers from './pages/Customers'
import CustomerDetail from './pages/CustomerDetail'
import Appointments from './pages/Appointments'
import Todos from './pages/Todos'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="customers" element={<Customers />} />
          <Route path="customers/:id" element={<CustomerDetail />} />
          <Route path="appointments" element={<Appointments />} />
          <Route path="todos" element={<Todos />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
