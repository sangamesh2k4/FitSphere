import { useEffect, useState } from "react";
import { workoutService } from "../services/workoutService";
import TrainingVolumeCard from "../components/progress/TrainingVolumeCard/TrainingVolumeCard";
import ExerciseProgressCard from "../components/progress/ExerciseProgressCard/ExerciseProgressCard";

import "../css/progress.css";

function Progress() {
    const [volumePeriod, setVolumePeriod] = useState("WEEKLY");

    const [volumeData, setVolumeData] = useState(null);
    const [volumeLoading, setVolumeLoading] = useState(true);
    const [volumeError, setVolumeError] = useState("");

    const [exercises, setExercises] = useState([]);
    const [exerciseLoading, setExerciseLoading] = useState(true);

    const [selectedExerciseId, setSelectedExerciseId] = useState("");

    /*
     * Load training volume
     */
    useEffect(() => {
        const loadVolume = async () => {
            try {
                setVolumeLoading(true);
                setVolumeError("");

                const data =
                    await workoutService.getTrainingVolumeAnalytics(
                        volumePeriod
                    );

                setVolumeData(data);

            } catch (err) {
                console.error(
                    "Failed to load training volume:",
                    err
                );

                setVolumeError(
                    "Failed to load training volume."
                );
            } finally {
                setVolumeLoading(false);
            }
        };

        loadVolume();
    }, [volumePeriod]);

    /*
     * Get exercises from workout history.
     *
     * We use the existing workout history rather than
     * creating another API call just for the selector.
     */
    useEffect(() => {
        const loadExercises = async () => {
            try {
                setExerciseLoading(true);

                const workouts =
                    await workoutService.getWorkoutHistory();

                const exerciseMap = new Map();

                workouts?.forEach(workout => {
                    workout.exercises?.forEach(exercise => {

                        if (
                            exercise.exerciseId &&
                            !exerciseMap.has(exercise.exerciseId)
                        ) {
                            exerciseMap.set(
                                exercise.exerciseId,
                                {
                                    id: exercise.exerciseId,
                                    name: exercise.exerciseName
                                }
                            );
                        }

                    });
                });

                const exerciseList =
                    Array.from(exerciseMap.values())
                        .sort((a, b) =>
                            a.name.localeCompare(b.name)
                        );

                setExercises(exerciseList);

                if (exerciseList.length > 0) {
                    setSelectedExerciseId(
                        String(exerciseList[0].id)
                    );
                }

            } catch (err) {
                console.error(
                    "Failed to load exercises:",
                    err
                );
            } finally {
                setExerciseLoading(false);
            }
        };

        loadExercises();
    }, []);

    return (
        <main className="progress-page">


            <TrainingVolumeCard
                period={volumePeriod}
                onPeriodChange={setVolumePeriod}
                data={volumeData}
                loading={volumeLoading}
                error={volumeError}
            />

            <ExerciseProgressCard
                exercises={exercises}
                selectedExerciseId={selectedExerciseId}
                onExerciseChange={setSelectedExerciseId}
                loadingExercises={exerciseLoading}
            />

        </main>
    );
}

export default Progress;