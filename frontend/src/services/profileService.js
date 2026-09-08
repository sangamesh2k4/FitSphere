
import api from "./api";

const BASE_URL = "/profile";

const profileService = {
    getProfile: async () => {
        const response = await api.get(BASE_URL);
        return response.data;
    },

    createProfile: async (profileData) => {
        const response = await api.post(BASE_URL, profileData);
        return response.data;
    },

    updateProfile: async (profileData) => {
        const response = await api.put(BASE_URL, profileData);
        return response.data;
    },
};

export default profileService;