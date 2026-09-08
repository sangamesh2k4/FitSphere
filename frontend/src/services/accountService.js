import api from "./api";

const accountService = {

    changeUsername(newUsername) {
        return api.patch("/account/username", {
            newUsername,
        });
    },

    checkUsernameAvailability(username) {
        return api.get("/account/username/check", {
            params: { username },
        });
    },

    changePassword(currentPassword, newPassword) {
        return api.patch("/account/password", {
            currentPassword,
            newPassword,
        });
    },

    sendEmailOtp(newEmail) {
        return api.post("/account/email/otp", {
            newEmail,
        });
    },

    verifyEmail(newEmail, otp) {
        return api.patch("/account/email", {
            newEmail,
            otp,
        });
    },

    getCurrentUser() {
    return api.get("/account/me");
}
};

export default accountService;