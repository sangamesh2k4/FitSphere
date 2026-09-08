import { useEffect, useMemo, useState } from 'react';
import "./ActiveWorkoutScreen.css";

const formatElapsedTime = (totalSeconds) => {
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
};

function ActiveWorkoutScreen({ workout }) {
    const [elapsedTime, setElapsedTime] = useState("00:00");

    const { totalVolume, totalSets } = useMemo(() => {
        let volume = 0;
        let sets = 0;

        workout?.exercises?.forEach(exercise => {
            exercise.sets?.forEach(set => {
                sets++;
                volume += set.volume ?? ((set.weight ?? 0) * (set.reps ?? 0));
            });
        });

        return {
            totalVolume: volume,
            totalSets: sets
        };
    }, [workout]);

    useEffect(() => {
        if (!workout?.startedAt) return;

        const startTime = new Date(workout.startedAt).getTime();

        if (Number.isNaN(startTime)) {
            console.error("Invalid workout startedAt:", workout.startedAt);
            return;
        }

        const updateTimer = () => {
            const now = Date.now();
            const elapsedSeconds = Math.max(0, Math.floor((now - startTime) / 1000));
            setElapsedTime(formatElapsedTime(elapsedSeconds));
        };

        updateTimer();
        const timer = setInterval(updateTimer, 1000);

        return () => clearInterval(timer);
    }, [workout?.startedAt]);

    return (
        <div className="active-workout-header">
            
            {/* Top Row: Badge & Title */}
            <div className="active-workout-top">
                <div className="status-badge">
                    <span className="status-dot"></span> IN PROGRESS
                </div>
                
                <h1>{workout?.name || "ACTIVE WORKOUT"}</h1>
                
                {/* Spacer to keep flex/grid alignment centered */}
                <div className="spacer"></div>
            </div>

            {/* Stats Container (Using your provided classes) */}
            <div className="active-workout-stats">
                <div className="active-workout-stat">
                    <span>TIME</span>
                    <strong>{elapsedTime}</strong>
                </div>

                <div className="active-workout-stat">
                    <span>VOLUME</span>
                    <strong>
                        {totalVolume.toLocaleString(undefined, { maximumFractionDigits: 1 })}
                        <small>kg</small>
                    </strong>
                </div>

                <div className="active-workout-stat">
                    <span>SETS</span>
                    <strong>{totalSets}</strong>
                </div>
            </div>
            
        </div>
    );
}

export default ActiveWorkoutScreen;