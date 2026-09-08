import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { workoutService } from "../../../services/workoutService";
import WorkoutHistoryCard from "../WorkoutHistoryCard/WorkoutHistoryCard";
import LoadingSkeleton from "../../skeleton/LoadingSkeleton/LoadingSkeleton";

function WorkoutHistory() {
    const location = useLocation();

    const [workouts, setWorkouts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const loadHistory = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await workoutService.getWorkoutHistory();
            setWorkouts(data);

        } catch (err) {
            console.error(
                "Failed to load workout history:",
                err
            );

            setError("Failed to load workout history.");

        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadHistory();
    }, [location.key]);

    if (loading) {
        return (
           <LoadingSkeleton /> 
        );
    }

    if (error) {
        return (
            <div className="workout-history-error">
                {error}
            </div>
        );
    }

    return (
        <section className="workout-history">

            <h2>RECENT SESSIONS</h2>

            {workouts.length === 0 ? (
                <div className="workout-history-empty">
                    <p>No workouts yet</p>

                    <span>
                        Start your first workout and your history
                        will appear here.
                    </span>
                </div>
            ) : (
                <div className="workout-history-list">

                    {workouts.map(workout => (
                        <WorkoutHistoryCard
                            key={workout.id}
                            workout={workout}
                        />
                    ))}

                </div>
            )}

        </section>
    );
}

export default WorkoutHistory;