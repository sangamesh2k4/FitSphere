import { useState } from 'react';
import { authService } from '../../services/authService';
import { validatePassword } from '../../utils/passwordValidator'; 
import PasswordRequirements from '../../components/auth/PasswordRequirements';
import "../../styles/auth/auth.css";

const getAuthErrorMessage = (err, fallback) => {
  if (err.response?.status === 429) {
    return (
      err.response?.data?.message ||
      "Too many registration attempts. Please wait a while and try again."
    );
  }

  return err.response?.data?.message || fallback;
};

function Register({ onClose, onSwitchToLogin, onSuccess }) {
  const [formData, setFormData] = useState({ 
    username: '', 
    email: '', 
    password: '' 
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate password before hitting the API
    const passwordError = validatePassword(formData.password);
    if (passwordError) {
      setError(passwordError);
      return; 
    }
    
    setLoading(true);
    setError(null);

    try {
      await authService.register(formData);
      onSuccess(formData.email);
    } catch (err) {
      console.error('Registration failed:', err);
      setError(
        getAuthErrorMessage(
          err,
          "Registration failed. Please try again."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-card">
      {/* Modal Close Button */}
      <button className="close-modal" onClick={onClose} aria-label="Close modal">
        &times;
      </button>

      <h2>Create an Account</h2>
      <p>Join Fitsphere and track your workouts.</p>

      {error && <div className="error-banner">{error}</div>}

      <form onSubmit={handleSubmit} className="auth-form">
        <div className="form-group">
          <label htmlFor="username">Username</label>
          <input
            type="text"
            id="username"
            name="username"
            value={formData.username}
            onChange={handleChange}
            disabled={loading}
            required
            autoFocus
          />
        </div>

        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            disabled={loading}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            disabled={loading}
            required
            minLength="8" 
          />
          {/* Real-time password indicator */}
          <PasswordRequirements password={formData.password} />
        </div>

        <button type="submit" className="primary-button full-width" disabled={loading}>
          {loading ? 'Creating Account...' : 'Sign Up'}
        </button>
      </form>

      <div className="auth-footer">
        <p>
          Already have an account?{' '}
          <span 
            onClick={onSwitchToLogin}
            style={{ color: 'var(--fs-gold)', cursor: 'pointer', fontWeight: '500' }}
          >
            Log in
          </span>
        </p>
      </div>
    </div>
  );
}

export default Register;