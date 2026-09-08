import { useEffect, useMemo, useRef, useState } from "react";
import { workoutService } from "../../../services/workoutService";
import { formatNumber } from "../../../utils";
import "./ExerciseProgressCard.css";

function ExerciseProgressCard({
    exercises,
    selectedExerciseId,
    onExerciseChange,
    loadingExercises
}) {
    const [progress, setProgress] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [isSelectOpen, setIsSelectOpen] = useState(false);
    const selectRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (selectRef.current && !selectRef.current.contains(event.target)) {
                setIsSelectOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    useEffect(() => {
        if (!selectedExerciseId) {
            setProgress(null);
            return;
        }

        const loadProgress = async () => {
            try {
                setLoading(true);
                setError("");
                const response = await workoutService.getExerciseProgress(selectedExerciseId);
                // Handle Axios response unwrapping or direct data objects
                setProgress(response?.data ?? response);
            } catch (err) {
                console.error("Failed to load exercise progress:", err);
                setError("Failed to load exercise progress.");
            } finally {
                setLoading(false);
            }
        };

        loadProgress();
    }, [selectedExerciseId]);

    // Flexible history array fallback across different backend DTO keys
    const history = useMemo(() => {
        if (!progress) return [];
        return progress.history || progress.sessions || progress.data || (Array.isArray(progress) ? progress : []);
    }, [progress]);

    const selectedExercise = exercises.find(
        (exercise) => String(exercise.id) === String(selectedExerciseId)
    );

    const chartConfig = useMemo(() => {
        if (!history.length) return null;

        const width = 800;
        const height = 240;
        const padding = { top: 25, right: 25, bottom: 40, left: 50 };

        const graphWidth = width - padding.left - padding.right;
        const graphHeight = height - padding.top - padding.bottom;

        const values = history.map((point) => point.bestEstimatedOneRepMax ?? point.estimatedOneRepMax ?? point.maxOneRepMax ?? point.weight ?? 0);
        let maxVal = Math.max(...values, 1);
        let minVal = Math.min(...values, 0);

        if (maxVal === minVal) {
            maxVal += 10;
            minVal = Math.max(0, minVal - 10);
        } else {
            const diff = maxVal - minVal;
            maxVal = Math.ceil(maxVal + diff * 0.12);
            minVal = Math.max(0, Math.floor(minVal - diff * 0.12));
        }

        const range = maxVal - minVal || 1;

        const points = history.map((point, index) => {
            const x = history.length === 1
                ? padding.left + graphWidth / 2
                : padding.left + (index / (history.length - 1)) * graphWidth;

            const val = point.bestEstimatedOneRepMax ?? point.estimatedOneRepMax ?? point.maxOneRepMax ?? point.weight ?? 0;
            const y = padding.top + graphHeight - ((val - minVal) / range) * graphHeight;

            // Expanded search for date keys
            const rawDate = point.workoutDate || point.completedAt || point.date || point.createdAt || point.startedAt || point.timestamp;
            let formattedDate = `S${index + 1}`; // Absolute fallback

            if (rawDate) {
                let parsedDate;

                // 1. Handle Java/Spring Boot date arrays (e.g., [2026, 8, 27])
                if (Array.isArray(rawDate) && rawDate.length >= 3) {
                    // JS Date months are 0-indexed, so subtract 1 from the month
                    parsedDate = new Date(rawDate[0], rawDate[1] - 1, rawDate[2]);
                } 
                // 2. Handle string dates
                else if (typeof rawDate === "string") {
                    const dateStr = rawDate.includes("-") && !rawDate.includes("T")
                        ? rawDate.replace(/-/g, "/")
                        : rawDate;
                    parsedDate = new Date(dateStr);
                } 
                // 3. Handle epoch timestamps or standard Date objects
                else {
                    parsedDate = new Date(rawDate);
                }

                // Format if valid
                if (parsedDate && !isNaN(parsedDate.getTime())) {
                    formattedDate = parsedDate.toLocaleDateString("en-US", {
                        month: "short",
                        day: "numeric"
                    });
                }
            }

            return {
                x,
                y,
                val,
                isPR: Boolean(point.personalRecord ?? point.isPR),
                workoutId: point.workoutId ?? point.id ?? index,
                dateLabel: formattedDate
            };
        });

        const polylinePoints = points.map((p) => `${p.x},${p.y}`).join(" ");

        const yTicks = [
            { value: maxVal, y: padding.top },
            { value: (maxVal + minVal) / 2, y: padding.top + graphHeight / 2 },
            { value: minVal, y: padding.top + graphHeight }
        ];

        let xTicks = [];
        if (points.length <= 6) {
            xTicks = points;
        } else {
            const step = Math.ceil(points.length / 5);
            for (let i = 0; i < points.length; i += step) {
                xTicks.push(points[i]);
            }
            if (xTicks[xTicks.length - 1] !== points[points.length - 1]) {
                xTicks.push(points[points.length - 1]);
            }
        }

        return {
            width,
            height,
            padding,
            points,
            polylinePoints,
            yTicks,
            xTicks
        };
    }, [history]);

    const currentBestVal = progress?.currentBestEstimatedOneRepMax ?? progress?.currentBest1RM ?? (history.length > 0 ? Math.max(...history.map(p => p.bestEstimatedOneRepMax ?? p.estimatedOneRepMax ?? 0)) : 0);
    const totalPRsVal = progress?.totalPersonalRecords ?? progress?.prCount ?? history.filter(p => p.personalRecord ?? p.isPR).length;

    const hasData = !loading && !error && history.length > 0;

    return (
        <section className="exercise-progress-card">
            <div className="exercise-progress-header">
                <span className="analytics-card-label">EXERCISE PROGRESS</span>

                <div className="custom-select-container" ref={selectRef}>
                    <button
                        type="button"
                        className={`custom-select-trigger ${isSelectOpen ? "open" : ""}`}
                        onClick={() => setIsSelectOpen(!isSelectOpen)}
                        disabled={loadingExercises || exercises.length === 0}
                    >
                        <span className="select-text">
                            {selectedExercise ? selectedExercise.name : "Select Exercise..."}
                        </span>
                        <span className="select-arrow">▾</span>
                    </button>

                    {isSelectOpen && exercises.length > 0 && (
                        <ul className="custom-select-options">
                            {exercises.map((exercise) => (
                                <li
                                    key={exercise.id}
                                    className={`custom-select-option ${
                                        String(exercise.id) === String(selectedExerciseId) ? "selected" : ""
                                    }`}
                                    onClick={() => {
                                        onExerciseChange(exercise.id);
                                        setIsSelectOpen(false);
                                    }}
                                >
                                    {exercise.name}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>

            {loadingExercises ? (
                <div className="analytics-loading">Loading exercises...</div>
            ) : loading ? (
                <div className="analytics-loading">Loading progress...</div>
            ) : error ? (
                <div className="analytics-error">{error}</div>
            ) : !selectedExercise ? (
                <div className="exercise-no-data">
                    <div className="no-data-icon">↗</div>
                    <h3>No exercise selected</h3>
                    <p>Select an exercise to track your performance history.</p>
                </div>
            ) : !hasData ? (
                <div className="exercise-no-data">
                    <div className="no-data-icon">↗</div>
                    <h3>No data yet for {selectedExercise.name}</h3>
                    <p>Log a set for this exercise in a workout and your progress will show up here.</p>
                    <button
                        type="button"
                        onClick={() => (window.location.href = "/app/workouts")}
                    >
                        + Log {selectedExercise.name} in a Workout
                    </button>
                </div>
            ) : (
                <>
                    <div className="exercise-progress-summary">
                        <div>
                            <strong>
                                {formatNumber(currentBestVal)} kg
                            </strong>
                            <span>Current best · Estimated 1RM</span>
                        </div>

                        <div className="pr-count">
                            🏆 {totalPRsVal} PRs all-time
                        </div>
                    </div>

                    <div className="progress-chart">
                        <svg
                            viewBox={`0 0 ${chartConfig.width} ${chartConfig.height}`}
                            preserveAspectRatio="xMidYMid meet"
                        >
                            {chartConfig.yTicks.map((tick, idx) => (
                                <g key={idx}>
                                    <line
                                        x1={chartConfig.padding.left}
                                        y1={tick.y}
                                        x2={chartConfig.width - chartConfig.padding.right}
                                        y2={tick.y}
                                        className="chart-gridline"
                                    />
                                    <text
                                        x={chartConfig.padding.left - 10}
                                        y={tick.y + 4}
                                        className="chart-axis-text y-axis-text"
                                        textAnchor="end"
                                    >
                                        {formatNumber(Math.round(tick.value))}
                                    </text>
                                </g>
                            ))}

                            {chartConfig.xTicks.map((pt, idx) => (
                                <text
                                    key={idx}
                                    x={pt.x}
                                    y={chartConfig.height - 10}
                                    className="chart-axis-text x-axis-text"
                                    textAnchor="middle"
                                >
                                    {pt.dateLabel}
                                </text>
                            ))}

                            <polyline
                                points={chartConfig.polylinePoints}
                                fill="none"
                                stroke="var(--fs-gold, #c97f0a)"
                                strokeWidth="3"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />

                            {chartConfig.points.map((point) => (
                                <g key={point.workoutId} className="chart-point-group">
                                    {point.isPR && (
                                        <circle
                                            cx={point.x}
                                            cy={point.y}
                                            r="10"
                                            fill="var(--fs-gold, #c97f0a)"
                                            opacity="0.2"
                                        />
                                    )}
                                    <circle
                                        cx={point.x}
                                        cy={point.y}
                                        r={point.isPR ? "5.5" : "4"}
                                        fill={point.isPR ? "#ffffff" : "var(--fs-gold, #c97f0a)"}
                                        stroke="var(--fs-gold, #c97f0a)"
                                        strokeWidth={point.isPR ? "3" : "2"}
                                        className="chart-dot"
                                    />
                                    <title>{`${formatNumber(point.val)} kg (${point.dateLabel})${point.isPR ? " - PR!" : ""}`}</title>
                                </g>
                            ))}
                        </svg>
                    </div>

                    <p className="chart-description">
                        Each point is a completed session · larger dots mark a PR
                    </p>
                </>
            )}
        </section>
    );
}

export default ExerciseProgressCard;