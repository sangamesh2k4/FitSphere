import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { workoutService } from '../services/workoutService'
import '../css/StartWorkoutModal.css'

// 2. Moved outside the component to avoid recreation on every render
const getDefaultName = () => {
  const hour = new Date().getHours()
  if (hour < 12) return 'Morning Workout'
  if (hour < 17) return 'Afternoon Workout'
  return 'Evening Workout'
}

function StartWorkoutModal({ onClose }) {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [workoutName, setWorkoutName] = useState(getDefaultName())

  useEffect(() => {
    const handleEscape = (event) => {
      if (event.key === 'Escape' && !loading) {
        onClose()
      }
    }

    window.addEventListener('keydown', handleEscape)
    return () => window.removeEventListener('keydown', handleEscape)
  }, [loading, onClose])

  const handleStartWorkout = async (e) => {
    e.preventDefault()
    if (!workoutName.trim() || loading) return

    setLoading(true)
    setError(null)

    try {
      const response = await workoutService.startWorkout({ name: workoutName.trim() })
      onClose()
      navigate(`/workouts/${response.id}/active`)
    } catch (err) {
      console.error("Failed to start workout:", err)
      // 3. More robust error fallback
      setError(
        err.response?.data?.message || 
        err.message || 
        'Failed to start workout. Please try again.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      className="modal-overlay"
      onClick={() => !loading && onClose()}
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
              onChange={(e) => {
                // 1. Clear error immediately when user starts typing
                setWorkoutName(e.target.value)
                if (error) setError(null)
              }}
              placeholder="e.g., Leg Day, Push Workout..."
              disabled={loading}
              autoFocus
            />
          </div>

          <div className="modal-actions">
            <button 
              type="button" 
              className="secondary-button" 
              onClick={onClose} 
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="primary-button"
              disabled={loading || !workoutName.trim()}
            >
              {loading ? 'Starting...' : 'Start Session'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default StartWorkoutModal