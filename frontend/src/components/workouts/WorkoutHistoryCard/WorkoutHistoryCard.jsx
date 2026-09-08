import { useNavigate } from "react-router-dom";
import "../WorkoutHistoryCard/WorkoutHistoryCard.css";
function WorkoutHistoryCard({ workout }) {
    const navigate = useNavigate();

    const exerciseCount = workout.exercises?.length ?? 0;

    const totalSets = workout.exercises?.reduce(
        (total, exercise) => total + (exercise.sets?.length ?? 0),
        0
    ) ?? 0;

    const duration = getWorkoutDuration(workout.startedAt, workout.completedAt);

    const handleOpen = () => {
        navigate(`/app/workouts/${workout.id}`);
    };

    return (
        <article
            className="workout-history-card"
            onClick={handleOpen}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                    handleOpen();
                }
            }}
        >
            <div className="workout-history-left">
                <h3>{workout.name || "Workout"}</h3>
                <p className="workout-history-subtext">
                    {formatDate(workout.startedAt)}
                    {" · "}
                    {duration}
                </p>
            </div>

            {/* Exercises, Sets, and Volume in a single inline row */}
            <div className="workout-history-stats-inline">
                <span><strong>{exerciseCount}</strong> exercises</span>
                <span className="dot-separator">·</span>
                <span><strong>{totalSets}</strong> sets</span>
                <span className="dot-separator">·</span>
                <span><strong>{workout.totalVolume ?? 0}</strong> kg volume</span>
            </div>

            <div className="workout-history-arrow">
                →
            </div>
        </article>
    );
}

// =====================================================
// Helpers
// =====================================================

function formatDate(date) {
    if (!date) {
        return "";
    }
    return new Date(date).toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric"
    });
}

function getWorkoutDuration(startedAt, completedAt) {
    if (!startedAt || !completedAt) {
        return "-";
    }

    const start = new Date(startedAt);
    const end = new Date(completedAt);
    const totalMinutes = Math.round((end - start) / (1000 * 60));

    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;

    if (hours > 0) {
        return `${hours}h ${minutes}m`;
    }
    return `${minutes} min`;
}

export default WorkoutHistoryCard;