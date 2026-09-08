import { useNavigate } from 'react-router-dom'
import { formatDate, formatNumber } from "../../utils";
import '../../css/WorkoutCard.css'

function WorkoutCard({ workout }) {
  const navigate = useNavigate()

  const handleCardClick = () => {
    if (!workout.completed) {
      navigate(`/workouts/${workout.id}/active`)
    } else {
      navigate(`/workouts/${workout.id}`)
    }
  }

  return (
    <article className="workout-card" onClick={handleCardClick}>
      <div className="workout-card-top">
        <span className="workout-date">{formatDate(workout.startedAt)}</span>
        
        {!workout.completed && (
          <span className="badge in-progress-badge">In Progress</span>
        )}
      </div>

      <h2>{workout.name}</h2>

      <div className="workout-metrics">
        <div className="metric">
          <span className="metric-label">Exercises</span>
          <span className="metric-value">{workout.exercises?.length || 0}</span>
        </div>
        
        {workout.totalVolume != null && workout.totalVolume > 0 && (
          <div className="metric">
            <span className="metric-label">Volume</span>
            <span className="metric-value">{formatNumber(workout.totalVolume)} kg</span>
          </div>
        )}
      </div>

      <div className="workout-card-bottom">
        <button 
          className="workout-view-button"
          onClick={(e) => {
            e.stopPropagation() 
            handleCardClick()
          }}
        >
          {workout.completed ? 'View Details →' : 'Continue Workout →'}
        </button>
      </div>
    </article>
  )
}

export default WorkoutCard