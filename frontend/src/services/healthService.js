import api from "./api";

export const healthService = {

    assessHealth: async (request) => {
        const { data } = await api.post(
            "/health/assessment",
            request
        );

        return data;
    }

};