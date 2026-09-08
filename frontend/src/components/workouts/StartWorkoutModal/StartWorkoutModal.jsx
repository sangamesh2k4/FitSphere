import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useWorkout } from "../../../context/WorkoutContext";

import "../StartWorkoutModal/StartWorkoutModal.css";

function StartWorkoutModal({ onClose }) {
    const navigate = useNavigate();
    const { startWorkout } = useWorkout();

    const [name, setName] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        const workoutName = name.trim();

        if (!workoutName) {
            setError("Please enter a workout name.");
            return;
        }

        try {
            setLoading(true);
            setError("");
            await startWorkout(workoutName);
            onClose();
            navigate("/app/workouts/active");
        } catch (err) {
            console.error("Failed to start workout:", err);
            setError("Failed to start workout. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleOverlayClick = (e) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    return (
        <div
            className="start-workout-modal-overlay"
            onMouseDown={handleOverlayClick}
        >
            <div className="start-workout-modal">
                <div className="start-workout-modal-header">
                    <h2>Start Workout</h2>
                    <button
                        type="button"
                        className="start-workout-modal-close"
                        onClick={onClose}
                        disabled={loading}
                    >
                        ×
                    </button>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="start-workout-form-group">
                        <label htmlFor="workout-name">Workout Name</label>
                        <input
                            id="workout-name"
                            type="text"
                            placeholder="e.g. Push Day"
                            value={name}
                            onChange={(e) => {
                                setName(e.target.value);
                                setError("");
                            }}
                            autoFocus
                            disabled={loading}
                        />
                    </div>

                    {error && (
                        <p className="start-workout-error">{error}</p>
                    )}

                    <div className="start-workout-modal-actions">
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={loading}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                        >
                            {loading ? "Starting..." : "Start Workout"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default StartWorkoutModal;