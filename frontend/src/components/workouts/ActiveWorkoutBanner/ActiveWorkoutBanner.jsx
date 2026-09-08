import { useEffect, useState } from "react";
import {
    useNavigate,
    useLocation
} from "react-router-dom";
import { useWorkout } from "../../../context/WorkoutContext";
import "./ActiveWorkoutBanner.css";

function formatElapsedTime(startedAt) {
    if (!startedAt) {
        return "00:00";
    }

    const elapsedSeconds = Math.max(
        0,
        Math.floor(
            (Date.now() - new Date(startedAt).getTime()) / 1000
        )
    );

    const hours = Math.floor(elapsedSeconds / 3600);
    const minutes = Math.floor(
        (elapsedSeconds % 3600) / 60
    );
    const seconds = elapsedSeconds % 60;

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

function ActiveWorkoutBanner() {
    const navigate = useNavigate();
    const location = useLocation();

    const { activeWorkout } = useWorkout();

    // Hooks MUST always run
    const [elapsedTime, setElapsedTime] = useState("00:00");

    useEffect(() => {
        // No active workout -> nothing to update
        if (!activeWorkout?.startedAt) {
            return;
        }

        const updateTime = () => {
            setElapsedTime(
                formatElapsedTime(
                    activeWorkout.startedAt
                )
            );
        };

        updateTime();

        const timer = setInterval(
            updateTime,
            1000
        );

        return () => {
            clearInterval(timer);
        };

    }, [activeWorkout?.startedAt]);

    const isWorkoutPage =
        location.pathname === "/app/workouts" ||
        location.pathname === "/app/workouts/active" ||
        location.pathname== "/app/workouts/summary" ||
        /^\/app\/workouts\/\d+$/.test(location.pathname);

    // Only return AFTER all hooks
    if (!activeWorkout || isWorkoutPage) {
        return null;
    }

    return (
        <button
            type="button"
            className="active-workout-banner"
            onClick={() =>
                navigate("/app/workouts/active")
            }
        >
            <span className="active-workout-dot" />

            <span className="active-workout-name">
                {activeWorkout.name}
            </span>

            <span className="active-workout-time">
                {elapsedTime}
            </span>

            <span className="active-workout-arrow">
                →
            </span>
        </button>
    );
}

export default ActiveWorkoutBanner;