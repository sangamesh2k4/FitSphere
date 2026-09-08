import { useState, useEffect } from 'react';
import { authService } from '../../services/authService';
import "../../styles/auth/auth.css";

function VerifyEmail({  email, onSuccessSwitchToLogin }) {
  const [formData, setFormData] = useState({ 
    email: email || '', 
    otp: '' 
  });
  
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const [countdown, setCountdown] = useState(30);
const [isResending, setIsResending] = useState(false);


useEffect(() => {
    if (countdown <= 0) return;

    const timer = setTimeout(() => {
        setCountdown(prev => prev - 1);
    }, 1000);

    return () => clearTimeout(timer);
}, [countdown]);


const handleResend = async () => {
    try {
        setIsResending(true);
        setError("");

        await authService.resendVerification(email);

        setCountdown(30);
    } catch (err) {
        setError(
            err.response?.data?.message ||
            "Failed to resend verification code."
        );
    } finally {
        setIsResending(false);
    }
};

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError(null);
  };
  

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await authService.verifyEmail(formData);
      setSuccess(true);
      // Wait 2 seconds so the user sees the success message, then switch to login modal
      setTimeout(() => onSuccessSwitchToLogin(), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid or expired OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="auth-card">

        <h2>Verify Your Email</h2>
        <p>Enter the 6-digit code we sent to your email.</p>

        {error && <div className="error-banner">{error}</div>}
        {success && <div className="success-banner">Email verified! Switching to login...</div>}

        <form onSubmit={handleSubmit} className="auth-form">
         <div className="verification-info">
  <p>
    We've sent a verification code to
    <br />
    <strong>{email}</strong>
  </p>
</div>

          <div className="form-group">
            <label htmlFor="otp">Verification Code</label>
            <input
              type="text"
              id="otp"
              name="otp"
              value={formData.otp}
              onChange={handleChange}
              placeholder="123456"
              maxLength="6"
              disabled={loading || success}
              required
              autoFocus
            />
          </div>

          <button type="submit" className="primary-button full-width" disabled={loading || success || formData.otp.length < 6}>
            {loading ? 'Verifying...' : 'Verify Email'}
          </button>
        </form>
        
        <div className="auth-footer">
          <div className="resend-container">
    {countdown > 0 ? (
      <p className="timer-text">
        Resend code in <strong>{countdown}s</strong>
      </p>
    ) : (
      <button
        type="button"
        className="auth-link-button"
        onClick={handleResend}
        disabled={isResending}
      >
        {isResending ? "Sending..." : "Resend Code"}
      </button>
    )}
  </div>

          <p>
            <span className="auth-link" onClick={onSuccessSwitchToLogin}>
              Back to Login
            </span>
          </p>
        </div>
      </div>

  );
}

export default VerifyEmail;