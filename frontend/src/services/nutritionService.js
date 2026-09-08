import api from "./api";

export const nutritionService = {

    // =====================================================
    // USDA FOOD SEARCH
    // =====================================================

    searchFoods: async (query, page = 1) => {
        const response = await api.get("/nutrition/search", {
            params: {
                query,
                page
            }
        });

        return response.data;
    },

    getFoodDetails: async (fdcId) => {
        const response = await api.get(
            `/nutrition/${fdcId}`
        );

        return response.data;
    },

    calculateMacros: async (request) => {
        const response = await api.post(
            "/nutrition/calculate",
            request
        );

        return response.data;
    },

    // =====================================================
    // FOOD LOGS
    // =====================================================

    addFoodLog: async (request) => {
        const response = await api.post(
            "/foodlogs",
            request
        );

        return response.data;
    },

    getTodayFoodLogs: async () => {
        const response = await api.get(
            "/foodlogs/today"
        );

        return response.data;
    },

    getTodaySummary: async () => {
        const response = await api.get(
            "/foodlogs/today/summary"
        );

        return response.data;
    },

    updateFoodLog: async (id, request) => {
        const response = await api.put(
            `/foodlogs/${id}`,
            request
        );

        return response.data;
    },

    deleteFoodLog: async (id) => {
        await api.delete(`/foodlogs/${id}`);
    },

    getHistory: async (startDate, endDate, page=0) => {
        const response = await api.get(
            "/foodlogs/history",
            {
                params: {
                    startDate,
                    endDate,
                    page
                }
            }
        );

        return response.data;
    },

    getFoodLogsByDate: async (date) => {
        const response = await api.get(
            "/foodlogs/date",
            {
                params: {
                    date
                }
            }
        );

        return response.data;
    }
};

export default nutritionService;