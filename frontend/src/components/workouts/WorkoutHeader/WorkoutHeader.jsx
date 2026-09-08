import { useState, useEffect, useMemo } from 'react';
import '../WorkoutHeader/WorkoutHeader.css';

// Helper to format the timer
const formatElapsedTime = (totalSeconds) => {
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    if (hours > 0) {
        return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
    }
    return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
};

function WorkoutHeader({ workout }) {
    const [elapsedTime, setElapsedTime] = useState("00:00");

    // 1. Calculate Elapsed Time
    useEffect(() => {
        if (!workout?.startedAt) return;

        const startTime = new Date(workout.startedAt).getTime();
        if (Number.isNaN(startTime)) return;

        const updateTimer = () => {
            const now = Date.now();
            const elapsedSeconds = Math.max(0, Math.floor((now - startTime) / 1000));
            setElapsedTime(formatElapsedTime(elapsedSeconds));
        };

        updateTimer(); // Initial call
        const timer = setInterval(updateTimer, 1000);

        return () => clearInterval(timer);
    }, [workout?.startedAt]);

    // 2. Calculate Volume and Sets
    const { totalVolume, totalSets } = useMemo(() => {
        let volume = 0;
        let sets = 0;

        if (workout?.exercises) {
            workout.exercises.forEach(exercise => {
                exercise.sets?.forEach(set => {
                    sets++;
                    volume += set.volume ?? ((set.weight ?? 0) * (set.reps ?? 0));
                });
            });
        }

        return {
            // Fallback to workout.totalVolume if exercises aren't populated yet
            totalVolume: volume > 0 ? volume : (workout?.totalVolume ?? 0),
            totalSets: sets > 0 ? sets : 0
        };
    }, [workout]);

    return (
        <div className="active-workout-header">
            
            {/* Top Row: Status and Title */}
            <div className="workout-header-top">
                <div className="status-badge">
                    <span className="status-dot"></span> IN PROGRESS
                </div>
                
                <h1 className="workout-name">
                    {workout?.name || "Workout"}
                </h1>
                
                {/* Empty spacer to keep the title perfectly centered using CSS Grid */}
                <div className="spacer"></div>
            </div>

            {/* Bottom Row: Metrics */}
            <div className="workout-header-metrics">
                <div className="metric-box">
                    <span className="metric-label">TIME</span>
                    <span className="metric-value">{elapsedTime}</span>
                </div>

                <div className="metric-box">
                    <span className="metric-label">VOLUME</span>
                    <span className="metric-value">
                        {totalVolume.toLocaleString(undefined, { maximumFractionDigits: 1 })} 
                        <span className="metric-unit">kg</span>
                    </span>
                </div>

                <div className="metric-box">
                    <span className="metric-label">SETS</span>
                    <span className="metric-value">{totalSets}</span>
                </div>
            </div>
            
        </div>
    );
}

export default WorkoutHeader;