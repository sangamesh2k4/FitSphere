import { useState } from 'react';
import { authService } from '../../services/authService';
import "../../styles/auth/auth.css";

function ForgotPassword({ onSwitchToLogin, onCodeSent }) {
  const [email, setEmail] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await authService.forgotPassword({ email });
      // Tell the parent component to switch to the OTP modal and pass the email
      onCodeSent(email); 
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to send reset code. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="auth-card">        
        <h2>Reset Password</h2>
        <p>Enter your email and we'll send you a code to reset your password.</p>

        {error && <div className="error-banner">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                if (error) setError(null);
              }}
              disabled={loading}
              required
              autoFocus
            />
          </div>

          <button type="submit" className="primary-button full-width" disabled={loading || !email}>
            {loading ? 'Sending Code...' : 'Send Reset Code'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Remember your password?{' '}
            <button type="button" className='auth-link' onClick={onSwitchToLogin}>
              Log in
            </button>
          </p>
        </div>
      </div>
  );
}

export default ForgotPassword;