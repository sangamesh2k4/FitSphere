import { Pencil, Power } from "lucide-react";
import "./AdminExerciseCard.css";

function AdminExerciseCard({ exercise, onEdit, onToggle }) {
    const handleEdit = () => {
        onEdit(exercise);
    };

    const handleToggle = () => {
        onToggle(exercise);
    };

    // Format category string (e.g., "FULL_BODY" -> "Full Body", "CORE" -> "Core")
    const formatCategory = (cat) => {
        if (!cat) return "";
        return cat
            .toLowerCase()
            .replace(/_/g, " ")
            .replace(/\b\w/g, (char) => char.toUpperCase());
    };
    return (
        <article className="admin-exercise-card">
            <div className="admin-exercise-left">
                <div className="admin-exercise-image">
                    {exercise.imageUrl ? (
                        <img src={exercise.imageUrl} alt={exercise.name} />
                    ) : (
                        <div className="admin-exercise-image-placeholder" />
                    )}
                </div>

                <h3 className="admin-exercise-name">{exercise.name}</h3>
            </div>

            <div className="admin-exercise-right">
                {exercise.category && (
                    <span className="admin-exercise-category">
                        {formatCategory(exercise.category)}
                    </span>
                )}

                <span
                    className={`admin-exercise-status ${
                        exercise.active ? "active" : "disabled"
                    }`}
                >
                    {exercise.active ? "Active" : "Disabled"}
                </span>

                <div className="admin-exercise-actions">
                    <button
                        type="button"
                        className="admin-exercise-action edit-btn"
                        onClick={handleEdit}
                        aria-label={`Edit ${exercise.name}`}
                    >
                        <Pencil size={16} />
                    </button>

                    <button
                        type="button"
                        className={`admin-exercise-action power-btn ${
                            exercise.active ? "active" : "disabled"
                        }`}
                        onClick={handleToggle}
                        aria-label={
                            exercise.active
                                ? `Disable ${exercise.name}`
                                : `Enable ${exercise.name}`
                        }
                    >
                        <Power size={16} />
                    </button>
                </div>
            </div>
        </article>
    );
}

export default AdminExerciseCard;