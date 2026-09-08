import { useState,useEffect } from 'react';
import { authService } from '../../services/authService';
import "../../styles/auth/Auth.css";

function VerifyResetOtp({ email, onSwitchToForgotPassword, onVerified }) {
  const [formData, setFormData] = useState({ 
    email: email || '', 
    otp: '' 
  });

  const [countdown, setCountdown] = useState(30);
const [isResending, setIsResending] = useState(false);

useEffect(() => {
    if (countdown <= 0) return;

    const timer = setTimeout(() => {
        setCountdown(prev => prev - 1);
    }, 1000);

    return () => clearTimeout(timer);
}, [countdown]);
  
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError(null);
  };

  const handleResend = async () => {
    try {
        setIsResending(true);
        setError(null);

        await authService.forgotPassword({ email });

        setCountdown(30);
    } catch (err) {
        setError(
            err.response?.data?.message ||
            "Failed to resend code."
        );
    } finally {
        setIsResending(false);
    }
};

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const resetToken = await authService.verifyResetOtp(formData);
      // Tell parent OTP is verified and pass the token for the next step
      onVerified(resetToken); 
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid code. Please check and try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="auth-card">
        <h2>Enter Reset Code</h2>
        <p>We sent a 6-digit code to <strong>{formData.email}</strong>.</p>

        {error && <div className="error-banner">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="otp">Reset Code</label>
            <input
              type="text"
              id="otp"
              name="otp"
              value={formData.otp}
              onChange={handleChange}
              placeholder="123456"
              maxLength="6"
              disabled={loading}
              required
              autoFocus
            />
          </div>

          <button type="submit" className="primary-button full-width" disabled={loading || formData.otp.length < 6}>
            {loading ? 'Verifying...' : 'Verify Code'}
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
           <span className="auth-link"onClick={onSwitchToForgotPassword}>
  Use a different email
</span>
          </p>
        </div>
      </div>
  );
}

export default VerifyResetOtp;