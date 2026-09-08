import api from "./api";

const favoriteService = {

    // ---------- Exercises ----------

    getUserFavorites: async () => {
        const { data } = await api.get("/favorites");
        return data;
    },

    addFavorite: async (exerciseId) => {
        const { data } = await api.post(
            `/favorites/${exerciseId}`
        );
        return data;
    },

    removeFavorite: async (exerciseId) => {
        const { data } = await api.delete(
            `/favorites/${exerciseId}`
        );
        return data;
    },

    isFavorite: async (exerciseId) => {
        const { data } = await api.get(
            `/favorites/${exerciseId}/status`
        );
        return data;
    },

    // ---------- Foods ----------

    getUserFoodFavorites: async () => {
        const { data } = await api.get(
            "/favorites/foods"
        );
        return data;
    },

    addFoodFavorite: async (fdcId) => {
        const { data } = await api.post(
            `/favorites/foods/${fdcId}`
        );
        return data;
    },

    removeFoodFavorite: async (fdcId) => {
        const { data } = await api.delete(
            `/favorites/foods/${fdcId}`
        );
        return data;
    },

    isFoodFavorite: async (fdcId) => {
        const { data } = await api.get(
            `/favorites/foods/${fdcId}/status`
        );
        return data;
    }

};

export default favoriteService;