
import { useNavigate } from 'react-router-dom'
import { workoutService } from '../services/workoutService'
import '../css/StartWorkoutModal.css' // Assuming you'll style this later
import { useEffect, useState } from 'react'

function StartWorkoutModal({ onClose }) {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  
  
  // Auto-generate a default name based on the current time
  const getDefaultName = () => {
    const hour = new Date().getHours()
    if (hour < 12) return 'Morning Workout'
    if (hour < 17) return 'Afternoon Workout'
    return 'Evening Workout'
  }

  useEffect(() => {
  const handleEscape = (event) => {
    if (event.key === 'Escape' && !loading) {
      onClose()
    }
  }

  window.addEventListener('keydown', handleEscape)

  return () => {
    window.removeEventListener('keydown', handleEscape)
  }
}, [loading, onClose])

  const [workoutName, setWorkoutName] = useState(getDefaultName())

 const handleStartWorkout = async (e) => {
  e.preventDefault()

  setLoading(true)
  setError(null)

  try {
    const payload = {
      name: workoutName.trim()
    }

    const response = await workoutService.startWorkout(payload)

    onClose()

    navigate(`/workouts/${response.id}/active`)
  } catch (err) {
    console.error(err)
    setError('Failed to start workout. Please try again.')
  } finally {
    setLoading(false)
  }
}

  return (
   <div
  className="modal-overlay"
  onClick={() => {
    if (!loading) {
      onClose()
    }
  }}
>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h2>Start New Workout</h2>
        
        {error && <p className="error-message">{error}</p>}
        
        <form onSubmit={handleStartWorkout}>
          <div className="form-group">
            <label htmlFor="workoutName">Workout Name</label>
            <input
              type="text"
              id="workoutName"
              value={workoutName}
              onChange={(e) => setWorkoutName(e.target.value)}
              placeholder="e.g., Leg Day, Push Workout..."
              disabled={loading}
              autoFocus
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="secondary-button" onClick={onClose} disabled={loading}>
              Cancel
            </button>
          <button
           type="submit"
       className="primary-button"
            disabled={loading || !workoutName.trim()}
>
               {loading ? 'Starting Workout...' : 'Start Session'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default StartWorkoutModal