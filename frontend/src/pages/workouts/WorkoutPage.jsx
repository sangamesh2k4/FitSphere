import { useEffect, useState, useRef } from "react";
import { toast } from "react-hot-toast";
import { workoutService } from "../../services/workoutService";
import "./WorkoutPage.css";
import LoadingSkeleton from "../../components/skeleton/LoadingSkeleton/LoadingSkeleton";

export default function WorkoutPage() {
    const [activeWorkout, setActiveWorkout] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const discardTimerRef = useRef(null);

    // 1. Handle Pending Discard
    useEffect(() => {
        const stored = sessionStorage.getItem("pendingDiscardWorkout");

        if (!stored) {
            return;
        }

        const pending = JSON.parse(stored);
        const remainingTime = pending.expiresAt - Date.now();

        if (remainingTime <= 0) {
            sessionStorage.removeItem("pendingDiscardWorkout");
            return;
        }

        const deleteWorkout = async () => {
            try {
                await workoutService.deleteWorkout(pending.workoutId);
                
                sessionStorage.removeItem("pendingDiscardWorkout");
                toast.dismiss("discard-toast");
                toast.success("Workout discarded.");

                // If the discarded workout was loaded into state, clear it
                setActiveWorkout((prev) => 
                    prev?.id === pending.workoutId ? null : prev
                );
            } catch (err) {
                console.error("Failed to discard workout:", err);
                toast.error("Failed to discard workout.");
            }
        };

        discardTimerRef.current = setTimeout(deleteWorkout, remainingTime);

        toast(
            (t) => (
                <div className="discard-snackbar">
                    <span>Workout discarded</span>
                    <button
                        type="button"
                        onClick={() => {
                            clearTimeout(discardTimerRef.current);
                            discardTimerRef.current = null;
                            sessionStorage.removeItem("pendingDiscardWorkout");
                            toast.dismiss(t.id);
                            toast.success("Discard undone.");
                        }}
                    >
                        Undo
                    </button>
                </div>
            ),
            {
                duration: remainingTime,
                id: "discard-toast"
            }
        );

        return () => {
            if (discardTimerRef.current) {
                clearTimeout(discardTimerRef.current);
            }
        };
    }, []);

    // 2. Load Active Workout
    useEffect(() => {
        const loadActiveWorkout = async () => {
            try {
                setLoading(true);
                const workout = await workoutService.getActiveWorkout();
                setActiveWorkout(workout);
            } catch (error) {
                // 404 simply means no active workout
                if (error.response?.status === 404) {
                    setActiveWorkout(null);
                } else {
                    console.error(error);
                    setError("Unable to load workout.");
                }
            } finally {
                setLoading(false);
            }
        };

        loadActiveWorkout();
    }, []);

    if (loading) {
        return (
            <LoadingSkeleton />
        );
    }

    return (
        <div className="workout-page">
            {error && (
                <p className="workout-error">
                    {error}
                </p>
            )}

            <section className="workout-hero">
                <button className="workout-primary-btn">
                    {activeWorkout
                        ? "Go to Current Workout"
                        : "Start Workout"}
                </button>
            </section>

            <section className="workout-history">
                <div className="workout-history-header">
                    <h2>Workout History</h2>
                </div>
                {/* History will come next */}
            </section>
        </div>
    );
}