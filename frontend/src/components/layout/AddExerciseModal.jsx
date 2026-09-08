import { useState, useEffect } from 'react'
import { workoutService } from '../services/workoutService'
import '../css/AddExerciseModal.css'

const API_BASE_URL = 'http://localhost:8080/api'

function AddExerciseModal({
  workoutId,
  onClose,
  onExerciseAdded
}) {
  const [search, setSearch] = useState('')
  const [exercises, setExercises] = useState([])
  const [loading, setLoading] = useState(false)
  const [adding, setAdding] = useState(false)

  const formatEnum = (value) => {
    if (!value) return ''

    return value
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, letter => letter.toUpperCase())
  }

  useEffect(() => {
    const fetchExercises = async () => {
      setLoading(true)

      try {
        const response = await fetch(
          `${API_BASE_URL}/exercises/filter?keyword=${encodeURIComponent(search)}&page=0`
        )

        if (!response.ok) {
          throw new Error('Failed to load exercises')
        }

        const data = await response.json()

        setExercises(data.content || [])
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    const timer = setTimeout(fetchExercises, 300)

    return () => clearTimeout(timer)
  }, [search])

  const handleAddExercise = async (exerciseId) => {
    if (adding) return

    setAdding(true)

    try {
      await workoutService.addExercise(workoutId, {
        exerciseId
      })

      onExerciseAdded()
      onClose()
    } catch (err) {
      console.error(err)
      alert('Failed to add exercise.')
    } finally {
      setAdding(false)
    }
  }

  return (
    <div
      className="modal-overlay"
      onClick={() => {
        if (!adding) {
          onClose()
        }
      }}
    >
      <div
        className="modal-content large"
        onClick={e => e.stopPropagation()}
      >
        <div className="modal-header">
          <h2>Select Exercise</h2>

          <button
            className="icon-button"
            disabled={adding}
            onClick={onClose}
          >
            ✕
          </button>
        </div>

        <input
          type="text"
          className="search-input"
          placeholder="Search exercises..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          autoFocus
          disabled={adding}
        />

        <div className="exercise-results">
          {loading ? (
            <p className="loading-text">
              Searching...
            </p>
          ) : exercises.length === 0 ? (
            <p className="empty-text">
              No exercises found.
            </p>
          ) : (
            exercises.map(exercise => (
              <div
                key={exercise.id}
                className="exercise-result-item"
              >
                <div className="exercise-info">
                  <strong>{exercise.name}</strong>

                  <span className="exercise-target">
                    {formatEnum(exercise.primaryMuscle)}
                  </span>
                </div>

                <button
                  className="secondary-button small"
                  disabled={adding}
                  onClick={() =>
                    handleAddExercise(exercise.id)
                  }
                >
                  {adding ? 'Adding...' : 'Add'}
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