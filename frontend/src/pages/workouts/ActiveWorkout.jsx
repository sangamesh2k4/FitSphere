import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";
import { workoutService } from "../../services/workoutService";
import WorkoutHeader from "../../components/workouts/ActiveWorkoutScreen/ActiveWorkoutScreen";
import AddExerciseModal from "../../components/workouts/AddExerciseModal/AddExerciseModal";
import WorkoutExerciseCard from "../../components/workouts/WorkoutExerciseCard/WorkoutExerciseCard";

import "../../css/ActiveWorkout.css";
import LoadingSkeleton from "../../components/skeleton/LoadingSkeleton/LoadingSkeleton";

function ActiveWorkout() {
    const navigate = useNavigate();

    const [workout, setWorkout] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showExerciseModal, setShowExerciseModal] = useState(false);
    
    // Action States
    const [isDiscarding, setIsDiscarding] = useState(false);
    const [isCompleting, setIsCompleting] = useState(false);

    // Reorder States
    const [draggedExerciseId, setDraggedExerciseId] = useState(null);
    const [isReordering, setIsReordering] = useState(false);
    
    // Reusable Confirmation Prompt
    const [confirmPrompt, setConfirmPrompt] = useState({
        visible: false,
        title: "",
        message: "",
        confirmText: "",
        confirmClass: "",
        onConfirm: null
    });

    // 1. Centralized Fetch
    const loadActiveWorkout = useCallback(async (isInitialLoad = false) => {
        if (isInitialLoad) setLoading(true);
        try {
            const data = await workoutService.getActiveWorkout();
            setWorkout(data);
            setError("");
        } catch {
            setError("Failed to load active workout.");
        } finally {
            if (isInitialLoad) setLoading(false);
        }
    }, []);

    // 2. Initial Data Fetch
    useEffect(() => {
        loadActiveWorkout(true);
    }, [loadActiveWorkout]);

    // 3. Reorder Drag Handlers
    const handleDragStart = (exerciseId) => {
        setDraggedExerciseId(exerciseId);
    };

    const handleDragOver = (e) => {
        e.preventDefault();
    };

    const handleDrop = async (targetExerciseId) => {
        if (
            draggedExerciseId === null ||
            draggedExerciseId === targetExerciseId ||
            isReordering
        ) {
            return;
        }

        const currentExercises = [...(workout.exercises ?? [])];

        const draggedIndex = currentExercises.findIndex(
            exercise => exercise.id === draggedExerciseId
        );

        const targetIndex = currentExercises.findIndex(
            exercise => exercise.id === targetExerciseId
        );

        if (draggedIndex === -1 || targetIndex === -1) {
            return;
        }

        // Create the new order
        const reorderedExercises = [...currentExercises];
        const [draggedExercise] = reorderedExercises.splice(draggedIndex, 1);
        reorderedExercises.splice(targetIndex, 0, draggedExercise);

        // Update exerciseOrder for the UI
        const updatedExercises = reorderedExercises.map((exercise, index) => ({
            ...exercise,
            exerciseOrder: index + 1
        }));

        // Optimistic UI update
        setWorkout(prev => ({
            ...prev,
            exercises: updatedExercises
        }));

        setDraggedExerciseId(null);
        setIsReordering(true);

        try {
            await workoutService.reorderExercises(
                workout.id,
                updatedExercises.map(exercise => exercise.id)
            );
            toast.success("Exercise order updated");
        } catch  {
            toast.error("Failed to update exercise order.");
            // Restore server state
            await loadActiveWorkout(false);
        } finally {
            setIsReordering(false);
        }
    };

    // 4. Add Exercise (Optimistic UI Update)
    const handleAddExercise = async (exerciseId, exerciseName) => {
        const alreadyAdded = workout?.exercises?.some(
            ex => ex.exerciseId === exerciseId || ex.exercise?.id === exerciseId
        );

        if (alreadyAdded) {
            toast.error(`${exerciseName || "This exercise"} is already in your workout.`);
            return;
        }

        try {
            const newExercise = await workoutService.addExercise(workout.id, { exerciseId });
            
            setWorkout(prev => ({
                ...prev,
                exercises: [...(prev.exercises ?? []), newExercise]
            }));

            toast.success(`${exerciseName || "Exercise"} added to workout`);
            return newExercise;
        } catch  {
            toast.error("Failed to add exercise.");
        }
    };

    // 5. Complete Workout Logic
    const handleCompleteWorkout = async () => {
        if (!workout || isCompleting) {
            return;
        }

        const exerciseCount = workout.exercises?.length ?? 0;

        const totalSets = (workout.exercises ?? []).reduce(
            (total, exercise) => total + (exercise.sets?.length ?? 0),
            0
        );

        if (exerciseCount === 0) {
            toast.error("Add at least one exercise before completing.");
            return;
        }

        if (totalSets === 0) {
            toast.error("Log at least one set before completing.");
            return;
        }

        setConfirmPrompt({
            visible: true,
            title: "Complete Workout?",
            message: "Are you sure you want to complete this workout?",
            confirmText: "Complete",
            confirmClass: "complete",
            onConfirm: executeCompleteWorkout
        });
    };

    const executeCompleteWorkout = async () => {
        if (isCompleting) {
            return;
        }

        setIsCompleting(true);
        cancelConfirm();

        try {
            const completedWorkout = await workoutService.completeWorkout(workout.id);

            sessionStorage.setItem(
                "completedWorkoutSummary",
                JSON.stringify(completedWorkout)
            );

            navigate("/app/workouts/summary");
        } catch {
            toast.error("Failed to complete workout.");
        } finally {
            setIsCompleting(false);
        }
    };

    // 6. Discard Workout Logic
    const cancelConfirm = () => {
        setConfirmPrompt({
            visible: false,
            title: "",
            message: "",
            confirmText: "",
            confirmClass: "",
            onConfirm: null
        });
    };

    const executeDiscardDelay = () => {
        if (!workout || isDiscarding) {
            return;
        }

        cancelConfirm();
        const workoutId = workout.id;
        setIsDiscarding(true);

        sessionStorage.setItem(
            "pendingDiscardWorkout",
            JSON.stringify({
                workoutId,
                expiresAt: Date.now() + 5000
            })
        );

        navigate("/app/workouts");
    };

    const handleDiscardClick = () => {
        if (!workout || workout.completed || isDiscarding) {
            return;
        }

        setConfirmPrompt({
            visible: true,
            title: "Discard Workout?",
            message: "Are you sure you want to discard this workout?",
            confirmText: "Discard",
            confirmClass: "danger",
            onConfirm: executeDiscardDelay
        });
    };

    // 7. Render States
    if (loading) {
        return (
            <LoadingSkeleton />
        );
    }

    if (error) {
        return (
            <div className="active-workout-state error">
                <p>{error}</p>
                <button onClick={() => loadActiveWorkout(true)}>Try Again</button>
            </div>
        );
    }

    if (!workout) {
        return (
            <div className="active-workout-state empty">
                <h2>No active workout</h2>
                <p>Start a workout to begin training.</p>
            </div>
        );
    }

    const exerciseCount = workout?.exercises?.length ?? 0;
    
    const totalSets = (workout?.exercises ?? []).reduce(
        (total, exercise) => total + (exercise.sets?.length ?? 0),
        0
    );
    
    const canComplete = exerciseCount > 0 && totalSets > 0;

    return (
        <div className="active-workout">
            {confirmPrompt.visible && (
                <div className="confirm-modal-overlay">
                    <div className="confirm-modal">
                        <h3>{confirmPrompt.title}</h3>
                        <p>{confirmPrompt.message}</p>
                        <div className="confirm-modal-actions">
                            <button
                                type="button"
                                className="btn-modal-cancel"
                                onClick={cancelConfirm}
                            >
                                Cancel
                            </button>
                            <button
                                type="button"
                                className={`btn-modal-confirm ${confirmPrompt.confirmClass}`}
                                onClick={confirmPrompt.onConfirm}
                            >
                                {confirmPrompt.confirmText}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <WorkoutHeader workout={workout} />

            <div className="workout-exercises">
                {workout.exercises?.map(exercise => (
                    <WorkoutExerciseCard
                        key={exercise.id}
                        exercise={exercise}
                        workoutId={workout.id}
                        onUpdate={() => loadActiveWorkout(false)}
                        onDragStart={handleDragStart}
                        onDragOver={handleDragOver}
                        onDrop={handleDrop}
                        isReordering={isReordering}
                    />
                ))}
                
                <button
                    type="button"
                    className="add-exercise-button"
                    onClick={() => setShowExerciseModal(true)}
                >
                    + Add Exercise
                </button>
            </div>

            <div className="workout-actions" style={{ marginTop: '2rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                <button
                    type="button"
                    className="complete-workout-button"
                    onClick={handleCompleteWorkout}
                    disabled={
                        !canComplete ||
                        workout.completed ||
                        isDiscarding ||
                        isCompleting
                    }
                >
                    {workout.completed
                        ? "Workout Completed"
                        : isCompleting
                        ? "Completing..."
                        : "Complete Workout"}
                </button>

                <button
                    type="button"
                    className="discard-workout-button"
                    onClick={handleDiscardClick}
                    disabled={
                        workout.completed ||
                        isDiscarding ||
                        isCompleting
                    }
                >
                    {isDiscarding
                        ? "Discarding..."
                        : "Discard Workout"}
                </button>
            </div>

            {showExerciseModal && (
                <AddExerciseModal
                    workoutId={workout.id}
                    workoutExercises={workout.exercises}
                    onExerciseAdded={handleAddExercise}
                    onClose={() => setShowExerciseModal(false)}
                />
            )}
        </div>
    );
}

export default ActiveWorkout;