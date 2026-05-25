import { useState, useEffect } from 'react'
import { todosApi } from '../api'

export default function Todos() {
  const [todos, setTodos] = useState([])
  const [newTitle, setNewTitle] = useState('')

  const load = () => todosApi.getAll().then(r => setTodos(r.data)).catch(() => {})
  useEffect(() => { load() }, [])

  const handleAdd = async (e) => {
    e.preventDefault()
    if (!newTitle.trim()) return
    await todosApi.create(newTitle.trim())
    setNewTitle('')
    load()
  }

  const handleToggle = async (id) => { await todosApi.toggle(id); load() }
  const handleDelete = async (id) => { await todosApi.delete(id); load() }

  const pending = todos.filter(t => !t.completed)
  const done = todos.filter(t => t.completed)

  const TodoRow = ({ todo }) => (
    <div className="flex items-center gap-3 px-5 py-3.5 border-b last:border-b-0 hover:bg-slate-50 group">
      <input type="checkbox" checked={todo.completed} onChange={() => handleToggle(todo.id)}
        className="w-4 h-4 rounded accent-blue-600 cursor-pointer" />
      <span className={`flex-1 text-sm ${todo.completed ? 'line-through text-slate-400' : 'text-slate-700'}`}>{todo.title}</span>
      <span className="text-xs text-slate-400 hidden group-hover:block">{todo.createdAt}</span>
      <button onClick={() => handleDelete(todo.id)} className="text-xs text-red-400 hover:text-red-600 opacity-0 group-hover:opacity-100 transition-opacity">Delete</button>
    </div>
  )

  return (
    <div className="max-w-2xl">
      <h2 className="text-2xl font-bold text-slate-800 mb-5">To-Do List</h2>

      <form onSubmit={handleAdd} className="flex gap-2 mb-5">
        <input type="text" placeholder="Add a new task..." value={newTitle} onChange={e => setNewTitle(e.target.value)}
          className="flex-1 border border-slate-200 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">Add Task</button>
      </form>

      <div className="bg-white rounded-xl shadow-sm border border-slate-100 overflow-hidden">
        {todos.length === 0 && (
          <p className="p-8 text-center text-slate-400 text-sm">No tasks yet. Add one above!</p>
        )}
        {pending.map(t => <TodoRow key={t.id} todo={t} />)}
        {done.length > 0 && (
          <>
            <div className="px-5 py-2 bg-slate-50 border-t border-b text-xs font-medium text-slate-500 uppercase tracking-wide">
              Completed ({done.length})
            </div>
            {done.map(t => <TodoRow key={t.id} todo={t} />)}
          </>
        )}
      </div>
    </div>
  )
}
