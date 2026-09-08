import { useState } from 'react';
import { authService } from '../../services/authService';
import { validatePassword } from '../../utils/passwordValidator';
import PasswordRequirements from '../../components/auth/PasswordRequirements';
import "../../styles/auth/auth.css";

function ResetPassword({  resetToken, onSuccessSwitchToLogin }) {
  const [formData, setFormData] = useState({ newPassword: '', confirmPassword: '' });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (formData.newPassword !== formData.confirmPassword) {
      return setError('Passwords do not match.');
    }

    const passwordError = validatePassword(formData.newPassword);
    if (passwordError) {
      setError(passwordError);
      return; 
    }

    setLoading(true);
    setError(null);

    try {
      await authService.resetPassword({ 
        resetToken, 
        newPassword: formData.newPassword 
      });
      setSuccess(true);
      // Wait 2 seconds so the user sees the success message, then switch to login modal
      setTimeout(() => onSuccessSwitchToLogin(), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to reset password. The token may be expired.');
    } finally {
      setLoading(false);
    }
  };

  if (!resetToken) return null; 

  return (
      <div className="auth-card">
        <h2>Choose New Password</h2>
        <p>Your new password must be at least 8 characters long and contain an uppercase letter, a number, and a special character.</p>

        {error && <div className="error-banner">{error}</div>}
        {success && <div className="success-banner">Password updated! Switching to login...</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="newPassword">New Password</label>
            <input
              type="password"
              id="newPassword"
              name="newPassword"
              value={formData.newPassword}
              onChange={handleChange}
              disabled={loading || success}
              required
              minLength="8" 
              autoFocus
            />
            <PasswordRequirements password={formData.newPassword} />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              disabled={loading || success}
              required
              minLength="8"
            />
          </div>

          <button type="submit" className="primary-button full-width" disabled={loading || success}>
            {loading ? 'Resetting...' : 'Reset Password'}
          </button>
        </form>
      </div>
  );
}

export default ResetPassword;