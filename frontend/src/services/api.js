import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

// Separate axios instance.
// IMPORTANT: this does NOT have the main interceptor.
const refreshApi = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

const clearTokens = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userRole");
};

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(({ resolve, reject }) => {
        if (error) {
            reject(error);
        } else {
            resolve(token);
        }
    });

    failedQueue = [];
};


// =====================================================
// REQUEST INTERCEPTOR
// =====================================================

api.interceptors.request.use(
    (config) => {
        const accessToken = localStorage.getItem("accessToken");

        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);


// =====================================================
// RESPONSE INTERCEPTOR
// =====================================================

api.interceptors.response.use(
    (response) => response,

    async (error) => {

        const originalRequest = error.config;

        if (!originalRequest) {
            return Promise.reject(error);
        }

        // Only handle 401
        if (
            error.response?.status !== 401 ||
            originalRequest._retry
        ) {
            return Promise.reject(error);
        }


        // =================================================
        // REFRESH ALREADY RUNNING
        // =================================================

        if (isRefreshing) {

            return new Promise((resolve, reject) => {

                failedQueue.push({
                    resolve,
                    reject,
                });

            }).then((newAccessToken) => {

                originalRequest.headers.Authorization =
                    `Bearer ${newAccessToken}`;

                return api(originalRequest);

            }).catch((err) => {

                return Promise.reject(err);

            });
        }


        // =================================================
        // START REFRESH
        // =================================================

        originalRequest._retry = true;
        isRefreshing = true;

        const refreshToken =
            localStorage.getItem("refreshToken");


        // No refresh token
        if (!refreshToken) {

            isRefreshing = false;
            clearTokens();

            return Promise.reject(error);
        }


        try {

            const response = await refreshApi.post(
                "/auth/refresh",
                {
                    refreshToken,
                }
            );

            const newAccessToken =
                response.data.accessToken;


            if (!newAccessToken) {
                throw new Error("No access token returned");
            }


            localStorage.setItem(
                "accessToken",
                newAccessToken
            );


            // Resolve queued requests
            processQueue(
                null,
                newAccessToken
            );


            // Retry original request
            originalRequest.headers.Authorization =
                `Bearer ${newAccessToken}`;

            return api(originalRequest);


        } catch (refreshError) {

            processQueue(
                refreshError,
                null
            );

            clearTokens();

            return Promise.reject(refreshError);

        } finally {

            isRefreshing = false;

        }
    }
);


export default api;