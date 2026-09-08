import api from "./api";

export const measurementService = {

    // Add a new measurement
    addMeasurement: async (request) => {
        const response = await api.post(
            "/measurements",
            request
        );

        return response.data;
    },

    // Get all measurements
    getMeasurementHistory: async (page=0) => {
        const response = await api.get(
            "/measurements",{params:{page}}
        );

        return response.data;
    },

    // Get latest measurement
    getLatestMeasurement: async () => {
        const response = await api.get(
            "/measurements/latest"
        );

        return response.data;
    },

    // Update an existing measurement
    updateMeasurement: async (id, request) => {
        const response = await api.patch(
            `/measurements/${id}`,
            request
        );

        return response.data;
    },

    // Delete measurement
    deleteMeasurement: async (id) => {
        const response = await api.delete(
            `/measurements/${id}`
        );

        return response.data;
    },

    // Get trend for a particular metric
    getMeasurementTrend: async (metric) => {
        const response = await api.get(
            "/measurements/trend",
            {
                params: { metric }
            }
        );

        return response.data;
    }
};