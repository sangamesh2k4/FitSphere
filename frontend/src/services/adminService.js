import api from "./api";

const adminService = {

    getExerciseById: async (id) => {
    const response = await api.get(`/admin/exercises/${id}`);
    return response.data;
},

    // Get all exercises
    getAllExercises: async ({
        page = 0,
        keyword = "",
        category = "",
        primaryMuscle = "",
        difficulty = "",
        equipment = "",
        exerciseType = "",
        active = ""
    }) => {
        const response = await api.get("/admin/exercises", {
            params: {
                page,
                keyword: keyword || undefined,
                category: category || undefined,
                primaryMuscle: primaryMuscle || undefined,
                difficulty: difficulty || undefined,
                equipment: equipment || undefined,
                exerciseType: exerciseType || undefined,
                active: active === "" ? undefined : active
            }
        });

        return response.data;
    },

    // Get primary muscles by category
    getPrimaryMusclesByCategory: async (category) => {
        const response = await api.get(
            `/exercises/category/${category}/primary-muscles`
        );

        return response.data;
    },

    // Add exercise
    addExercise: async (exerciseData) => {
        const response = await api.post("/admin/exercises", exerciseData);
        return response.data;
    },

    // Update exercise
    updateExercise: async (exerciseId, exerciseData) => {
        const response = await api.patch(
            `/admin/exercises/${exerciseId}`,
            exerciseData
        );
        return response.data;
    },

    // Disable exercise
    disableExercise: async (exerciseId) => {
        const response = await api.patch(
            `/admin/exercises/${exerciseId}/disable`
        );
        return response.data;
    },

    // Enable exercise
    enableExercise: async (exerciseId) => {
        const response = await api.patch(
            `/admin/exercises/${exerciseId}/enable`
        );
        return response.data;
    },

    // Delete user
    deleteUser: async (userId) => {
        const response = await api.delete(
            `/admin/users/${userId}`
        );
        return response.data;
    },

    // Usage statistics
    getUsageStats: async () => {
        const response = await api.get("/admin/usage");
        return response.data;
    },

    // users
    getUsers: async (page = 0) => {
        const response = await api.get("/admin/users", {
            params: { page }
        });
        return response.data;
    },

    disableUser: async (userId) => {
    const response = await api.patch(
        `/admin/users/${userId}/disable`
    );
    return response.data;
},

enableUser: async (userId) => {
    const response = await api.patch(
        `/admin/users/${userId}/enable`
    );
    return response.data;
},


getContactMessages: async ({page = 0,
     status = "",sortBy = "createdAt", direction = "desc"} = {}) => {
    const response = await api.get("/admin/contact-messages", {
        params: {page,status: status || undefined,
            sortBy,direction }
    });
    return response.data;
},
getContactMessageById: async (messageId) => {
    const response = await api.get(
        `/admin/contact-messages/${messageId}`
    );
    return response.data;
},

replyToContactMessage: async (messageId, message) => {
    const response = await api.post(
        `/admin/contact-messages/${messageId}/reply`,
        { message }
    );
    return response.data;
},

updateContactMessageStatus: async (messageId, status) => {
    const response = await api.patch(
        `/admin/contact-messages/${messageId}/status`,
        { status }
    );
    return response.data;
},

deleteContactMessage: async (messageId) => {
    const response = await api.delete(
        `/admin/contact-messages/${messageId}`
    );
    return response.data;
}};



export default adminService;