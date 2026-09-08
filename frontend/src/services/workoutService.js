import api from './api';

export const workoutService = {

    // -------------------------
    // Workouts
    // -------------------------

    startWorkout: async (requestBody) => {
        const response = await api.post(
            '/workouts',
            requestBody
        );

        return response.data;
    },


    getWorkoutHistory: async () => {
        const response = await api.get(
            '/workouts'
        );

        return response.data;
    },


    getActiveWorkout: async () => {
        const response = await api.get(
            '/workouts/active'
        );

        return response.data;
    },


    getWorkoutById: async (workoutId) => {
        const response = await api.get(
            `/workouts/${workoutId}`
        );

        return response.data;
    },


    completeWorkout: async (workoutId) => {
        const response = await api.post(
            `/workouts/${workoutId}/complete`
        );

        return response.data;
    },
    
    deleteWorkout: async (workoutId) => {
    await api.delete(`/workouts/${workoutId}`);
},


    // -------------------------
    // Exercises
    // -------------------------

    addExercise: async (workoutId, requestBody) => {
        const response = await api.post(
            `/workouts/${workoutId}/exercises`,
            requestBody
        );

        return response.data;
    },


    removeExercise: async (
        workoutId,
        workoutExerciseId
    ) => {

        await api.delete(
            `/workouts/${workoutId}/exercises/${workoutExerciseId}`
        );
    },


    reorderExercises : async (
    workoutId,
    exerciseIds
) => {

await api.patch(
        `/workouts/${workoutId}/exercises/reorder`,
        {
            exerciseIds
        }
    );
},

    // -------------------------
    // Sets
    // -------------------------

    addSet: async (
        workoutId,
        workoutExerciseId,
        requestBody
    ) => {

        const response = await api.post(
            `/workouts/${workoutId}/exercises/${workoutExerciseId}/sets`,
            requestBody
        );

        return response.data;
    },


    updateSet: async (
        workoutId,
        workoutExerciseId,
        setId,
        requestBody
    ) => {

        const response = await api.patch(
            `/workouts/${workoutId}/exercises/${workoutExerciseId}/sets/${setId}`,
            requestBody
        );

        return response.data;
    },


    deleteSet: async (
        workoutId,
        workoutExerciseId,
        setId
    ) => {

        await api.delete(
            `/workouts/${workoutId}/exercises/${workoutExerciseId}/sets/${setId}`
        );
    },
 //rename

 renameWorkout: async (workoutId, data) => {
    const response = await api.patch(
        `/workouts/${workoutId}`,
        data
    );

    return response.data;
},

    // -------------------------
    // Analytics
    // -------------------------

    getExerciseProgress: async (exerciseId) => {

        const response = await api.get(
            `/workouts/progress/exercises/${exerciseId}`
        );

        return response.data;
    },


    getTrainingVolumeAnalytics: async (period) => {

        const response = await api.get(
            '/workouts/analytics/volume',
            {
                params: { period }
            }
        );

        return response.data;
    }
};