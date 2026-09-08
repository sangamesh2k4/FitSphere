import api from '../services/api';

export const authService = {
  /**
   * Register a new user
   * @param {Object} payload - Matches RegisterRequest DTO
   */
  register: async (payload) => {
    const { data } = await api.post('/auth/register', payload);
    return data;
  },

  /**
   * Login and receive JWT tokens
   * @param {Object} payload - Matches LoginRequest DTO
   */
  login: async (payload) => {
    const { data } = await api.post('/auth/login', payload);
    return data;
  },

  /**
   * Logout user and revoke refresh token on the backend
   * @param {Object} payload - { refreshToken }
   */
  logout: async (payload) => {
    if (payload?.refreshToken) {
      await api.post('/auth/logout', payload);
    }
  },

  /**
   * Verify email using OTP
   * @param {Object} payload - Matches VerifyEmailRequestDto { email, otp }
   */
  verifyEmail: async (payload) => {
    const { data } = await api.post('/auth/verify-email', payload);
    return data;
  },

  /**
   * Initiate forgot password flow
   * @param {Object} payload - Matches ForgotPasswordRequestDto { email }
   */
  forgotPassword: async (payload) => {
    const { data } = await api.post('/auth/forgot-password', payload);
    return data;
  },

  /**
   * Verify the OTP sent for password reset
   * @param {Object} payload - Matches VerifyResetOtpRequestDto { email, otp }
   */
  verifyResetOtp: async (payload) => {
    const { data } = await api.post('/auth/verify-reset-otp', payload);
    return data;
  },

  /**
   * Set a new password using the reset token
   * @param {Object} payload - Matches ResetPasswordRequestDto { resetToken, newPassword }
   */
  resetPassword: async (payload) => {
    const { data } = await api.post('/auth/reset-password', payload);
    return data;
  },

  /**
   * Resend verification OTP/email
   * @param {string} email
   */
  resendVerification: async (email) => {
    const { data } = await api.post('/auth/resend-verification', { email });
    return data;
  }
};

export default authService;