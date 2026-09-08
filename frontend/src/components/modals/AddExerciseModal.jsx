import { useState, useEffect } from 'react'
import { exerciseService } from '../services/exerciseService'
import { workoutService } from '../services/workoutService'
import { formatEnum } from '../utils'
import '../css/AddExerciseModal.css'

function AddExerciseModal({ workoutId, onClose, onExerciseAdded }) {
  const [search, setSearch] = useState('')
  const [exercises, setExercises] = useState([])
  
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  
  const [adding, setAdding] = useState(false)
  const [addError, setAddError] = useState(null)

  useEffect(() => {
    const fetchExercises = async () => {
      setLoading(true)
      setError(null)
      
      try {
        const data = await exerciseService.searchExercises(search)
        setExercises(data || [])
      } catch (err) {
        console.error("Failed to search exercises", err)
        setError("Failed to load exercises. Please check your connection.")
      } finally {
        setLoading(false)
      }
    }

    if (search.trim()) {
      const timer = setTimeout(() => fetchExercises(), 300)
      return () => clearTimeout(timer)
    } else {
      setExercises([])
    }
  }, [search])

  const handleAddExercise = async (exerciseId) => {
    if (adding) return
    
    setAdding(true)
    setAddError(null)

    try {
      await workoutService.addExercise(workoutId, { exerciseId })
      onExerciseAdded() 
      onClose() 
    } catch (err) {
      console.error("Failed to add exercise to workout", err)
      setAddError("Failed to add exercise. Please try again.")
    } finally {
      // Always reset adding state regardless of success/failure
      setAdding(false)
    }
  }

  return (
    <div 
      className="modal-overlay" 
      onClick={() => !adding && onClose()}
    >
      <div className="modal-content large" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Select Exercise</h2>
          <button 
            className="icon-button" 
            onClick={onClose}
            disabled={adding}
          >
            ✕
          </button>
        </div>

        <input
          type="text"
          placeholder="Search exercises (e.g., Bench Press)..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value)
            if (addError) setAddError(null)
          }}
          className="search-input"
          autoFocus
          disabled={adding}
        />

        {addError && <p className="error-message">{addError}</p>}

        <div className="exercise-results">
          {loading ? (
            <p className="loading-text">Searching...</p>
          ) : error ? (
            <p className="empty-text error-text">{error}</p>
          ) : !search.trim() ? (
            <p className="empty-text">Start typing to search exercises.</p>
          ) : exercises.length === 0 ? (
            <p className="empty-text">No exercises found.</p>
          ) : (
            exercises.map(ex => (
              <div 
                key={ex.id} 
                className="exercise-result-item" 
                onClick={() => !adding && handleAddExercise(ex.id)}
              >
                <div className="exercise-info">
                  <strong>{ex.name}</strong>
                  <span className="exercise-target">{formatEnum(ex.primaryMuscle)}</span>
                </div>
                <button 
                  className="secondary-button small" 
                  disabled={adding}
                >
                  {adding ? '...' : 'Add'}
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}

export default AddExerciseModal