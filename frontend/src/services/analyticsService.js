import api from "./api";

export const analyticsService = {

    getDashboardAnalytics: async () => {
        const response = await api.get(
            "/analytics/dashboard"
        );

        return response.data;
    }

};