import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { toast } from "react-hot-toast";
import { Pencil, Check } from "lucide-react";
import { workoutService } from "../../../services/workoutService";
import WorkoutExerciseCard from "../../workouts/WorkoutExerciseCard/WorkoutExerciseCard";
import AddExerciseModal from "../../workouts/AddExerciseModal/AddExerciseModal";
import LoadingSkeleton from "../../skeleton/LoadingSkeleton/LoadingSkeleton";
import "./EditWorkout.css";

function EditWorkout() {
    const { workoutId } = useParams();
    const navigate = useNavigate();

    const [workout, setWorkout] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showExerciseModal, setShowExerciseModal] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [workoutName, setWorkoutName] = useState("");
    const [isEditingName, setIsEditingName] = useState(false);

    const loadWorkout = useCallback(async () => {
        try {
            setLoading(true);
            setError("");
            const data = await workoutService.getWorkoutById(workoutId);
            setWorkout(data);
            setWorkoutName(data.name || "");
        } catch {
            setError("Failed to load workout.");
        } finally {
            setLoading(false);
        }
    }, [workoutId]);

    useEffect(() => {
        loadWorkout();
    }, [loadWorkout]);

    const handleRenameWorkout = async () => {
        const originalName = (workout.name || "").trim();
        const newName = workoutName.trim();

        if (!newName) {
            toast.error("Workout name cannot be empty.");
            return;
        }

        if (newName === originalName) {
            setIsEditingName(false);
            return;
        }

        try {
            setIsSubmitting(true);

            const updatedWorkout = await workoutService.renameWorkout(
                workoutId,
                { name: newName }
            );

            setWorkout(prev => ({
                ...prev,
                name: updatedWorkout.name
            }));

            setWorkoutName(updatedWorkout.name);
            setIsEditingName(false);

            toast.success("Workout renamed.");
        } catch (err) {
            console.error("Failed to rename workout:", err);
            toast.error("Failed to rename workout.");
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleAddExercise = async (exerciseId, exerciseName) => {
        const alreadyAdded = workout?.exercises?.some(
            exercise => exercise.exerciseId === exerciseId
        );

        if (alreadyAdded) {
            toast.error(
                `${exerciseName || "This exercise"} is already in this workout.`
            );
            return;
        }

        try {
            setIsSubmitting(true);
            const targetWorkoutId = workout?.id || workoutId;
            const newExercise = await workoutService.addExercise(
                targetWorkoutId,
                { exerciseId }
            );

            setWorkout(prev => ({
                ...prev,
                exercises: [
                    ...(prev?.exercises ?? []),
                    newExercise
                ]
            }));

            toast.success(`${exerciseName || "Exercise"} added`);
            setShowExerciseModal(false);
        } catch  {
            toast.error("Failed to add exercise.");
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDone = async () => {
        try {
            navigate("/app/workouts/summary", {
                state: {
                    workoutId: workout.id
                }
            });
        } catch (err) {
            console.error("Failed to save workout:", err);
            toast.error("Failed to save changes.");
        }
    };

   if (loading) {
    return (
        <main className="edit-workout-page">
            <LoadingSkeleton />
        </main>
    );
}

    if (error || !workout) {
        return (
            <main className="edit-workout-page">
                <div className="edit-workout-error">
                    <p>{error || "Workout not found."}</p>
                    <button
                        type="button"
                        className="back-workouts-button"
                        onClick={() => navigate("/app/workouts")}
                    >
                        Back to Workouts
                    </button>
                </div>
            </main>
        );
    }

    return (
        <main className="edit-workout-page">
            <header className="edit-workout-header">
                <div className="header-nav">
                    {/* Leftmost */}
                    <button
                        type="button"
                        className="back-button"
                        onClick={() =>
                            navigate("/app/workouts/summary", {
                                state: {
                                    workoutId: workout.id
                                }
                            })
                        }
                    >
                        ← Back
                    </button>
                    
                    {/* Middle */}
                    <h2 className="header-main-title">EDIT WORKOUT</h2>

                    {/* Rightmost */}
                    <div className="header-workout-name-container">
                        {isEditingName ? (
                            <>
                                <input
                                    type="text"
                                    className="header-workout-name-input"
                                    value={workoutName}
                                    onChange={(e) =>
                                        setWorkoutName(e.target.value)
                                    }
                                    onKeyDown={(e) => {
                                        if (e.key === "Enter") {
                                            handleRenameWorkout();
                                        }

                                        if (e.key === "Escape") {
                                            setWorkoutName(workout.name || "");
                                            setIsEditingName(false);
                                        }
                                    }}
                                    maxLength={100}
                                    autoFocus
                                    disabled={isSubmitting}
                                />

                                <button
                                    type="button"
                                    className="save-workout-name-button"
                                    onClick={handleRenameWorkout}
                                    disabled={isSubmitting}
                                    aria-label="Save workout name"
                                >
                                    <Check size={16} />
                                </button>
                            </>
                        ) : (
                            <>
                                <span
                                    className="header-workout-name"
                                    title={workout.name}
                                >
                                    {workout.name || "Workout"}
                                </span>

                                <button
                                    type="button"
                                    className="edit-workout-name-button"
                                    onClick={() => setIsEditingName(true)}
                                    aria-label="Edit workout name"
                                    disabled={isSubmitting}
                                >
                                    <Pencil size={16} />
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </header>

            <section className="edit-workout-exercises">
                {workout.exercises?.map(exercise => (
                    <WorkoutExerciseCard
                        key={exercise.id}
                        exercise={exercise}
                        workoutId={workout.id}
                        onUpdate={loadWorkout}
                    />
                ))}

                <button
                    type="button"
                    className="add-exercise-button"
                    disabled={isSubmitting}
                    onClick={() => setShowExerciseModal(true)}
                >
                    + Add Exercise
                </button>
            </section>

            <div className="edit-workout-footer">
                <button
                    type="button"
                    className="done-editing-button"
                    onClick={handleDone}
                >
                    Done
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
        </main>
    );
}

export default EditWorkout;