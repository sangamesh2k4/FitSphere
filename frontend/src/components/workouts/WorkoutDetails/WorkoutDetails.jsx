import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { workoutService } from "../../../services/workoutService";
import { formatNumber } from "../../../utils";
import "./WorkoutDetails.css";
import LoadingSkeleton from "../../skeleton/LoadingSkeleton/LoadingSkeleton";

function WorkoutDetails() {
    const { workoutId } = useParams();
    const navigate = useNavigate();
    const [workout, setWorkout] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);

    useEffect(() => {
        const loadWorkout = async () => {
            try {
                setLoading(true);
                setError("");
                const data = await workoutService.getWorkoutById(workoutId);
                setWorkout(data);
            } catch  {
                setError("Failed to load workout.");
            } finally {
                setLoading(false);
            }
        };
        loadWorkout();
    }, [workoutId]);

    const handleDeleteWorkout = async () => {
        if (isDeleting) {
            return;
        }

        try {
            setIsDeleting(true);
            await workoutService.deleteWorkout(workout.id);
            navigate("/app/workouts");
        } catch  {
            setIsDeleting(false);
            setShowDeleteConfirm(false);
        }
    };

    if (loading) {
        return (
            <main className="workout-details-page">
                <LoadingSkeleton />
            </main>
        );
    }

    if (error || !workout) {
        return (
            <main className="workout-details-page">
                <div className="workout-details-state error">
                    <p>{error || "Workout not found."}</p>
                    <button
                        type="button"
                        onClick={() => navigate("/app/workouts")}
                    >
                        Back to Workouts
                    </button>
                </div>
            </main>
        );
    }

    const totalSets = workout.exercises?.reduce(
        (total, exercise) => total + (exercise.sets?.length ?? 0),
        0
    ) ?? 0;

    const duration = getWorkoutDuration(
        workout.startedAt,
        workout.completedAt
    );

    return (
        <main className="workout-details-page">
            {/* Delete Confirmation Modal */}
            {showDeleteConfirm && (
                <div className="delete-modal-overlay">
                    <div className="delete-modal">
                        <h3>Delete Workout?</h3>
                        <p>
                            Are you sure you want to permanently delete this workout?
                        </p>

                        <div className="delete-modal-actions">
                            <button
                                type="button"
                                className="delete-cancel-button"
                                onClick={() => setShowDeleteConfirm(false)}
                                disabled={isDeleting}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="delete-confirm-button"
                                onClick={handleDeleteWorkout}
                                disabled={isDeleting}
                            >
                                {isDeleting ? "Deleting..." : "Delete"}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Header */}
            <div className="workout-details-header">
                <button
                    type="button"
                    className="back-button"
                    onClick={() => navigate("/app/workouts")}
                >
                    ← Back
                </button>
                <h1>{workout.name || "Workout"}</h1>
                <span className="completed-badge">
                    Completed
                </span>
            </div>

            {/* Summary */}
            <div className="workout-summary">
                <div className="summary-item">
                    <span className="summary-label">STARTED</span>
                    <strong>{formatDateTime(workout.startedAt)}</strong>
                </div>
                <div className="summary-item">
                    <span className="summary-label">COMPLETED</span>
                    <strong>{formatDateTime(workout.completedAt)}</strong>
                </div>
                <div className="summary-item">
                    <span className="summary-label">DURATION</span>
                    <strong>{duration}</strong>
                </div>
                <div className="summary-item">
                    <span className="summary-label">VOLUME</span>
                    <strong>{formatNumber(workout.totalVolume ?? 0)} kg</strong>
                </div>
                <div className="summary-item">
                    <span className="summary-label">EXERCISES</span>
                    <strong>{workout.exercises?.length ?? 0}</strong>
                </div>
                <div className="summary-item">
                    <span className="summary-label">SETS</span>
                    <strong>{totalSets}</strong>
                </div>
            </div>

            {/* Exercises */}
            <section className="workout-details-exercises">
                {workout.exercises?.map((exercise, index) => (
                    <WorkoutExerciseDetails
                        key={exercise.id}
                        exercise={exercise}
                        index={index}
                    />
                ))}
            </section>

            {/* Footer with Edit and Delete buttons */}
            <div className="workout-details-footer">
                <button
                    type="button"
                    className="edit-button"
                    onClick={() =>
                        navigate(`/app/workouts/${workout.id}/edit`)
                    }
                >
                    Edit
                </button>

                <button
                    type="button"
                    className="delete-button"
                    onClick={() => setShowDeleteConfirm(true)}
                    disabled={isDeleting}
                >
                    Delete Workout
                </button>
            </div>
        </main>
    );
}

// =====================================================
// Exercise Details
// =====================================================
function WorkoutExerciseDetails({ exercise, index }) {
    const sets = exercise.sets ?? [];

    return (
        <article className="workout-details-exercise">
            <div className="exercise-details-header">
                <h2>
                    <span>{exercise.exerciseOrder ?? index + 1}. </span>
                    {exercise.exerciseName}
                </h2>
            </div>

            {sets.length === 0 ? (
                <p className="no-sets">No sets recorded.</p>
            ) : (
                <div className="workout-sets-table">
                    <div className="workout-set-header">
                        <span>SET</span>
                        <span>WEIGHT</span>
                        <span>REPS</span>
                        <span>VOLUME</span>
                        <span>1RM</span>
                        <span>PR</span>
                    </div>

                    {sets.map((set, index) => (
                        <div className="workout-set-row" key={set.id}>
                            <span>{set.setNumber ?? index + 1}</span>
                            <span>{formatNumber(set.weight ?? 0)} kg</span>
                            <span>{set.reps ?? 0}</span>
                            <span>{formatNumber(set.volume ?? 0)} kg</span>
                            <span>
                                {set.estimatedOneRepMax != null
                                    ? `${formatNumber(set.estimatedOneRepMax)} kg`
                                    : "-"}
                            </span>
                            <span className="pr-cell">
                                {set.personalRecord ? "🔥" : "-"}
                            </span>
                        </div>
                    ))}
                </div>
            )}
        </article>
    );
}

// =====================================================
// Helpers
// =====================================================
function formatDateTime(date) {
    if (!date) {
        return "-";
    }
    return new Date(date).toLocaleString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: "numeric",
        minute: "2-digit"
    });
}

function getWorkoutDuration(startedAt, completedAt) {
    if (!startedAt || !completedAt) {
        return "-";
    }

    const start = new Date(startedAt).getTime();
    const end = new Date(completedAt).getTime();
    const totalSeconds = Math.max(0, Math.floor((end - start) / 1000));

    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    if (hours > 0) {
        return `${hours}h ${minutes}m`;
    }
    if (minutes > 0) {
        return `${minutes}m ${seconds}s`;
    }
    return `${seconds}s`;
}

export default WorkoutDetails;