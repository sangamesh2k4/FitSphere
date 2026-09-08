import { useEffect, useState, useContext } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { exerciseService } from '../services/exerciseService'
import { formatEnum } from '../utils/formatEnum'

// Contexts & Hooks
import { useAuth } from '../hooks/useAuth'
import { useFavorites } from '../hooks/useFavorites'
import { AuthModalContext } from '../context/AuthModalContext'

// Components
import { ExerciseBadge } from '../components/ExerciseBadge'
import { ExerciseMedia } from '../components/exercises/ExerciseMedia/ExerciseMedia'
import { ExerciseMetadata } from '../components/exercises/ExerciseMetadata/ExerciseMetadata'
import { InstructionList } from '../components/exercises/InstructionList/InstructionList'
import { InfoListCard } from '../components/exercises/InfoListCard/InfoListCard'
import { VideoCard } from '../components/exercises/VideoCard/VideoCard'
import { ExerciseDetailSkeleton } from '../components/skeleton/ExerciseDetailSkeleton'

import '../css/ExerciseDetail.css'

function ExerciseDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  
  const [exercise, setExercise] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  // Auth & Favorites Context
  const { isAuthenticated } = useAuth()
  const { openLogin } = useContext(AuthModalContext)
  const { isExerciseFavorite, toggleExerciseFavorite } = useFavorites()
  
  // Single source of truth for favorite status
  const exerciseId = Number(id);
  const isFavorite = isExerciseFavorite(exerciseId);

  useEffect(() => {
    const fetchExercise = async () => {
      setLoading(true)
      setError(false)
      try {
        const data = await exerciseService.getExerciseById(id)
        setExercise(data)
      } catch (err) {
        console.error("Failed to fetch exercise details", err)
        setError(true)
      } finally {
        setLoading(false)
      }
    }
    if (id) fetchExercise()
  }, [id])

  const handleToggleFavorite = async () => {
    if (!isAuthenticated) {
      openLogin()
      return
    }
    await toggleExerciseFavorite(exerciseId)
  }

  if (loading) {
    return <ExerciseDetailSkeleton />;
  }

  if (error || !exercise) {
    return (
      <div className="exercise-detail-wrapper">
        <div className="empty-state">
          <h2>404</h2>
          <p>We couldn't find that exercise.</p>
          <button className="back-button" onClick={() => navigate(-1)} style={{ margin: '1rem auto 0' }}>
            <i className="ti ti-arrow-left"></i> Back
          </button>
        </div>
      </div>
    )
  }

  return (
    <main className="exercise-detail-wrapper">
      <button className="back-button" onClick={() => navigate(-1)}>
        <i className="ti ti-arrow-left"></i> Back
      </button>

      <header className="detail-header">
        {exercise.category && (
          <p className="exercise-category">{formatEnum(exercise.category)}</p>
        )}
        
        <div className="title-row">
          <h1>{exercise.name}</h1>
          <button 
            className={`favorite-btn ${isFavorite ? 'is-favorite' : ''}`}
            onClick={handleToggleFavorite}
            aria-label="Toggle Favorite"
          >
            <i className={isFavorite ? "ti ti-heart-filled" : "ti ti-heart"}></i>
            {isFavorite ? "Remove from Favorites" : "Add to Favorites"}
          </button>
        </div>

        <div className="badge-group">
          <ExerciseBadge label={formatEnum(exercise.difficulty)} type="auto" />
          <ExerciseBadge label={formatEnum(exercise.equipment)} type="info" />
          <ExerciseBadge label={formatEnum(exercise.exerciseType)} type="info" />
        </div>
      </header>

      <section className="detail-main">
        <div className="detail-grid">
          <aside className="detail-sidebar">
            <ExerciseMedia 
              videoUrl={exercise.videoUrl} 
              imageUrl={exercise.imageUrl} 
              altText={exercise.name} 
            />
            <ExerciseMetadata exercise={exercise} />
          </aside>

          <section className="detail-content">
            {exercise.description && (
              <div className="content-card">
                <h2 className="card-heading">DESCRIPTION</h2>
                <p className="card-text">{exercise.description}</p>
              </div>
            )}

            <InstructionList instructions={exercise.instructions} />

            {(exercise.tips?.length > 0 || exercise.commonMistakes?.length > 0) && (
              <div className="tips-mistakes-grid">
                <InfoListCard 
                  title="PRO TIPS" 
                  icon="bulb" 
                  colorClass="icon-green" 
                  items={exercise.tips} 
                />
                <InfoListCard 
                  title="MISTAKES TO AVOID" 
                  icon="alert-triangle" 
                  colorClass="icon-red" 
                  items={exercise.commonMistakes} 
                />
              </div>
            )}
          </section>
        </div>
      </section>

      {exercise.recommendedVideos?.length > 0 && (
        <section className="videos-section">
          <h2 className="card-heading">RECOMMENDED VIDEOS</h2>
          <div className="videos-grid">
            {exercise.recommendedVideos.map((video) => (
              <VideoCard key={video.id || video.videoUrl} video={video} />
            ))}
          </div>
        </section>
      )}
    </main>
  )
}

export default ExerciseDetail