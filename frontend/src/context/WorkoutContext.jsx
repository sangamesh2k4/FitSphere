import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { workoutService } from "../services/workoutService";

const WorkoutContext = createContext(null);

export function WorkoutProvider({ children }) {

    const [activeWorkout, setActiveWorkout] = useState(null);
    const [loading, setLoading] = useState(true);

    const refreshActiveWorkout = useCallback(async () => {
        try {
            const workout = await workoutService.getActiveWorkout();
            setActiveWorkout(workout);
        } catch {
            // No active workout
            setActiveWorkout(null);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        refreshActiveWorkout();
    }, [refreshActiveWorkout]);

    const startWorkout = async (name) => {
        const workout = await workoutService.startWorkout({
            name,
            startedAt: new Date().toISOString()
        });

        setActiveWorkout(workout);
        return workout;
    };

    const completeWorkout = async () => {
        if (!activeWorkout) {
            return;
        }

        const completedWorkout = await workoutService.completeWorkout(
            activeWorkout.id
        );

        setActiveWorkout(null);
        return completedWorkout;
    };

    const value = {
        activeWorkout,
        loading,
        hasActiveWorkout: activeWorkout !== null,

        startWorkout,
        completeWorkout,
        refreshActiveWorkout
    };

    return (
        <WorkoutContext.Provider value={value}>
            {children}
        </WorkoutContext.Provider>
    );
}

export function useWorkout() {
    const context = useContext(WorkoutContext);

    if (!context) {
        throw new Error(
            "useWorkout must be used inside WorkoutProvider"
        );
    }

    return context;
}