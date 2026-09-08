import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { workoutService } from "../../services/workoutService";
import "../../styles/workout/WorkoutSummary.css";
import LoadingSkeleton from "../../components/skeleton/LoadingSkeleton/LoadingSkeleton";

function formatDuration(startedAt, completedAt) {
    if (!startedAt || !completedAt) {
        return "--:--";
    }

    const start = new Date(startedAt).getTime();
    const end = new Date(completedAt).getTime();

    if (Number.isNaN(start) || Number.isNaN(end)) {
        return "--:--";
    }

    const totalSeconds = Math.max(
        0,
        Math.floor((end - start) / 1000)
    );

    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    if (hours > 0) {
        return (
            `${String(hours).padStart(2, "0")}:` +
            `${String(minutes).padStart(2, "0")}:` +
            `${String(seconds).padStart(2, "0")}`
        );
    }

    return (
        `${String(minutes).padStart(2, "0")}:` +
        `${String(seconds).padStart(2, "0")}`
    );
}

function formatDate(dateValue) {
    if (!dateValue) {
        return "";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return "";
    }

    return date.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric"
    });
}

function WorkoutSummary() {
    const navigate = useNavigate();
    const location = useLocation();

    const [workout, setWorkout] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadSummary = async () => {

            // -----------------------------------------
            // Edited workout → fetch fresh data
            // -----------------------------------------
            const workoutId = location.state?.workoutId;

            if (workoutId) {
                try {
                    setLoading(true);

                    const data = await workoutService.getWorkoutById(workoutId);

                    setWorkout(data);

                } catch (err) {
                    console.error("Failed to load updated workout:", err);
                    navigate("/app/workouts");

                } finally {
                    setLoading(false);
                }

                return;
            }

            // -----------------------------------------
            // Normal workout completion
            // -----------------------------------------
            const stored = sessionStorage.getItem("completedWorkoutSummary");

            if (!stored) {
                navigate("/app/workouts");
                return;
            }

            try {
                setWorkout(JSON.parse(stored));

            } catch (err) {
                console.error("Failed to load workout summary:", err);
                sessionStorage.removeItem("completedWorkoutSummary");
                navigate("/app/workouts");

            } finally {
                setLoading(false);
            }
        };

        loadSummary();

    }, [location.state, navigate]);

    const exercises = workout?.exercises ?? [];

    const totalSets = useMemo(() => {
        return exercises.reduce(
            (total, exercise) => total + (exercise.sets?.length ?? 0),
            0
        );
    }, [exercises]);

    const personalRecords = useMemo(() => {
        return exercises.flatMap(exercise => {
            const records = (exercise.sets ?? []).filter(set => set.personalRecord);

            return records.map(set => ({
                ...set,
                exerciseName:
                    exercise.exerciseName ||
                    exercise.exercise?.name ||
                    "Exercise"
            }));
        });
    }, [exercises]);

    const handleDone = () => {
        // Only clear session storage if we aren't viewing a specific edited workout
        if (!location.state?.workoutId) {
            sessionStorage.removeItem("completedWorkoutSummary");
        }
        navigate("/app/workouts");
    };

    if (loading) {
        return (
            <main className="workout-summary-page">
                <LoadingSkeleton />
            </main>
        );
    }

    if (!workout) {
        return null;
    }

    return (
        <main className="workout-summary-page">
            <div className="workout-summary-container">

                {/* Top Success Icon */}
                <div className="summary-success-wrapper">
                    <div className="summary-success-icon">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <polyline points="20 6 9 17 4 12"></polyline>
                        </svg>
                    </div>
                </div>

                {/* Header */}
                <header className="summary-header">
                    <h1>Workout Complete</h1>
                    <p>
                        {workout.name || "Workout"}
                        {" · "}
                        {formatDate(workout.completedAt || workout.startedAt)}
                    </p>
                </header>

                {/* Dark Stats Box */}
                <section className="summary-stats-box">
                    <div className="summary-stat">
                        <strong>
                            {formatDuration(workout.startedAt, workout.completedAt)}
                        </strong>
                        <span>DURATION</span>
                    </div>

                    <div className="summary-stat">
                        <strong>
                            {Number(workout.totalVolume ?? 0).toLocaleString()}
                            <small>kg</small>
                        </strong>
                        <span>VOLUME</span>
                    </div>

                    <div className="summary-stat">
                        <strong>{totalSets}</strong>
                        <span>SETS</span>
                    </div>

                    <div className="summary-stat summary-pr-stat">
                        <strong>{personalRecords.length}</strong>
                        <span>PRS</span>
                    </div>
                </section>

                {/* Personal Records Box */}
                {personalRecords.length > 0 && (
                    <section className="summary-pr-box">
                        <div className="summary-pr-header">
                            <svg className="trophy-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M8 21h8"></path><path d="M12 17v4"></path><path d="M7 4h10v6a5 5 0 0 1-10 0V4Z"></path><path d="M7 4H3v3a5 5 0 0 0 5 5h0"></path><path d="M17 4h4v3a5 5 0 0 1-5 5h0"></path>
                            </svg>
                            <span>NEW PERSONAL RECORDS</span>
                        </div>

                        <div className="summary-pr-list">
                            {personalRecords.map((record, index) => (
                                <div
                                    className="summary-pr-row"
                                    key={record.id ?? `${record.exerciseName}-${index}`}
                                >
                                    <div className="pr-exercise-name">
                                        {record.exerciseName}
                                    </div>
                                    <div className="pr-record-value">
                                        <strong>{record.weight} kg</strong>
                                        <small>1RM</small>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </section>
                )}

                {/* Exercise Breakdown Box */}
                <section className="summary-breakdown-box">
                    <div className="summary-breakdown-header">
                        EXERCISE BREAKDOWN
                    </div>

                    <div className="summary-breakdown-list">
                        {exercises.map((exercise, index) => {
                            const setLen = exercise.sets?.length ?? 0;
                            return (
                                <div
                                    className="summary-exercise-row"
                                    key={exercise.id ?? index}
                                >
                                    <div className="summary-exercise-left">
                                        <strong>
                                            {exercise.exerciseName || exercise.exercise?.name || "Exercise"}
                                        </strong>
                                        <span>
                                            {setLen} {setLen === 1 ? "set" : "sets"}
                                        </span>
                                    </div>

                                    <div className="summary-exercise-right">
                                        <strong>
                                            {Number(exercise.totalVolume ?? 0).toLocaleString()} kg
                                        </strong>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </section>

                {/* Bottom Done Button */}
                <div className="summary-action-wrapper">
                    <button
                        type="button"
                        className="summary-done-button"
                        onClick={handleDone}
                        aria-label="Done"
                    >
                        Done
                    </button>
                </div>

            </div>
        </main>
    );
}

export default WorkoutSummary;