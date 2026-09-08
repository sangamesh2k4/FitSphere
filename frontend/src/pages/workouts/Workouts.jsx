import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";
import { useWorkout } from "../../context/WorkoutContext";
import { workoutService } from "../../services/workoutService";

import WorkoutHistory from "../../components/workouts/WorkoutHistory/WorkoutHistory";
import StartWorkoutModal from "../../components/workouts/StartWorkoutModal/StartWorkoutModal";

import "../../styles/workout/Workouts.css";

function Workouts() {
    const navigate = useNavigate();
    const { hasActiveWorkout, refreshActiveWorkout } = useWorkout();
    const [showStartModal, setShowStartModal] = useState(false);
    const [discardSnackbar, setDiscardSnackbar] = useState(null);
    const discardTimerRef = useRef(null);

    const isDiscardPending = discardSnackbar !== null;

    /* Fetch active workout immediately on mount */
    useEffect(() => {
        refreshActiveWorkout();
    }, [refreshActiveWorkout]);

    /* Check if a workout is waiting to be discarded */
    useEffect(() => {
        const stored = sessionStorage.getItem("pendingDiscardWorkout");
        if (!stored) {
            return;
        }

        let pending;
        try {
            pending = JSON.parse(stored);
        } catch {
            sessionStorage.removeItem("pendingDiscardWorkout");
            return;
        }

        const remainingTime = pending.expiresAt - Date.now();
        if (remainingTime <= 0) {
            sessionStorage.removeItem("pendingDiscardWorkout");
            refreshActiveWorkout();
            return;
        }

        /* Show snackbar immediately */
        setDiscardSnackbar({
            workoutId: pending.workoutId
        });

        /* Delete only after remaining time expires */
        discardTimerRef.current = setTimeout(async () => {
            try {
                await workoutService.deleteWorkout(pending.workoutId);
                sessionStorage.removeItem("pendingDiscardWorkout");
                setDiscardSnackbar(null);

                // Force context to re-check backend
                await refreshActiveWorkout();
            } catch {
                sessionStorage.removeItem("pendingDiscardWorkout");
                setDiscardSnackbar(null);

                await refreshActiveWorkout();
            } finally {
                discardTimerRef.current = null;
            }
        }, remainingTime);

        return () => {
            if (discardTimerRef.current) {
                clearTimeout(discardTimerRef.current);
                discardTimerRef.current = null;
            }
        };
    }, [refreshActiveWorkout]);

    /* UNDO DISCARD */
    const handleUndoDiscard = async () => {
        /* Stop pending DELETE */
        if (discardTimerRef.current) {
            clearTimeout(discardTimerRef.current);
            discardTimerRef.current = null;
        }

        /* Get ID directly from snackbar state */
        const workoutId = discardSnackbar?.workoutId;

        /* Remove pending discard */
        sessionStorage.removeItem("pendingDiscardWorkout");

        /* Remove snackbar */
        setDiscardSnackbar(null);

        /* Ensure context knows the workout still exists */
        await refreshActiveWorkout();

        toast.success("Workout restored.");

        /* Return to active workout */
        if (workoutId) {
            navigate("/app/workouts/active");
        }
    };

    const handleWorkoutButton = () => {
        if (hasActiveWorkout) {
            navigate("/app/workouts/active");
            return;
        }
        setShowStartModal(true);
    };

    return (
        <main className="workouts-page">
            <div className="workouts-header">
                <button
                    className="start-workout-button"
                    onClick={handleWorkoutButton}
                    disabled={isDiscardPending}
                >
                    {isDiscardPending
                        ? "Discarding..."
                        : hasActiveWorkout
                        ? "Continue Workout"
                        : "+ Start Workout"}
                </button>
            </div>

            <WorkoutHistory />

            {showStartModal && (
                <StartWorkoutModal
                    onClose={() => setShowStartModal(false)}
                />
            )}

            {/* DISCARD SNACKBAR */}
            {discardSnackbar && (
                <div className="discard-snackbar">
                    <span>Workout will be discarded</span>
                    <button
                        type="button"
                        onClick={handleUndoDiscard}
                    >
                        Undo
                    </button>
                </div>
            )}
        </main>
    );
}

export default Workouts;