import { useState } from 'react'
import type { Task } from './components/types'

function App() {
  const [task, setTask] = useState<Task>({
    title: '',
    description: '',
    status: '',
    dueDate: ''
  })

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    try {
      const response = await fetch('http://localhost:8080/api/task/create', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(task)
        }
      )

      const data = await response.json()
      alert(JSON.stringify(data, null, 2))

    } catch (error) {
      console.error('Error occurred: ', error)
    }
  }

  return (
    <div className='min-h-screen bg-gray-100 p-4'>
      <h1 className='font-bold text-4xl p-2 mb-4'>Create a task</h1>

      <div className='rounded-md shadow-md bg-white h-fit w-fit flex flex-col p-2'>
        <form onSubmit={handleSubmit} className='border flex flex-col space-y-4 p-2 w-96'>
          <input
            className='p-2 rounded-lg border border-gray-100'
            type="text"
            value={task.title}
            placeholder="Title"
            required
            onChange={(e) => setTask({...task, title: e.target.value})}
          />

          <input
            className='p-2 rounded-lg border border-gray-100'
            type="text"
            value={task.description}
            placeholder="Description"
            onChange={(e) => setTask({...task, description: e.target.value})}
          />

          <select
            value={task.status}
            className='p-2 rounded-lg border border-gray-100'
            onChange={(e) => {setTask({...task, status: e.target.value})}}
            required
          >
            <option value="" disabled>Status</option>
            <option value="To Do">To Do</option>
            <option value="In Progress">In Progress</option>
            <option value="Complete">Complete</option>
          </select>

          <input
            className='p-2 rounded-lg border border-gray-100'
            type="date"
            value={task.dueDate}
            onChange={(e) => setTask({...task, dueDate: e.target.value})}
            required
          />

          <button
            type="submit"
            className="bg-blue-600 text-white font-semibold py-2 rounded-md hover:bg-blue-700"
          >Create
          </button>
        </form>
      </div>
    </div>
  )
}

export default App
