import { formatEnum } from '../../../utils/formatEnum'
import "./ExerciseMetadata.css"

export function ExerciseMetadata({ exercise }) {
  return (
    <div className="metadata-card">
      <div className="metadata-row">
        <span className="meta-label">Primary Muscle</span>
        <span className="meta-value">{formatEnum(exercise.primaryMuscle)}</span>
      </div>
      
      {exercise.secondaryMuscles?.length > 0 && (
        <div className="metadata-row">
          <span className="meta-label">Secondary Muscles</span>
          <span className="meta-value">
            {exercise.secondaryMuscles.map(formatEnum).join(', ')}
          </span>
        </div>
      )}

      {exercise.equipment && (
        <div className="metadata-row">
          <span className="meta-label">Equipment</span>
          <span className="meta-value">{formatEnum(exercise.equipment)}</span>
        </div>
      )}

      {exercise.difficulty && (
        <div className="metadata-row no-border">
          <span className="meta-label">Difficulty</span>
          <span className="meta-value">{formatEnum(exercise.difficulty)}</span>
        </div>
      )}
    </div>
  );
}