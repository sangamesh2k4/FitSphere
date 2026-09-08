import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import "../../styles/auth/Login.css"; // Ensure this matches your CSS structure

const getAuthErrorMessage = (err, fallback) => {
  if (err.response?.status === 429) {
    return (
      err.response?.data?.message ||
      "Too many login attempts. Please wait a while and try again."
    );
  }

  return err.response?.data?.message || fallback;
};

function Login({ onClose, onSwitchToRegister, onSwitchToForgotPassword }) {
  const navigate = useNavigate();
  const { login } = useAuth();
  
  const [formData, setFormData] = useState({ usernameOrEmail: "", password: "" });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError(null); // Clear error when user types
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.usernameOrEmail || !formData.password) return;

    setLoading(true);
    setError(null);

    try {
  const data = await login(formData);
  if (onClose) onClose();
  if (data.role === "ROLE_ADMIN") {
    navigate("/admin/usage", { replace: true });
  } else {
    navigate("/app/dashboard", { replace: true });
  }
}catch (err) {
      console.error('Login failed:', err);
      setError(
        getAuthErrorMessage(
          err,
          "Invalid email/username or password."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-card">
      <h2>Welcome Back</h2>
      <p>Log in to Fitsphere to continue your journey.</p>

      {error && <div className="error-banner">{error}</div>}

      <form onSubmit={handleSubmit} className="auth-form">
        <div className="form-group">
          <label htmlFor="usernameOrEmail">Email or Username</label>
          <input
            type="text"
            id="usernameOrEmail"
            name="usernameOrEmail"
            placeholder="Enter your email or username"
            value={formData.usernameOrEmail}
            onChange={handleChange}
            disabled={loading}
            required
            autoFocus
            autoComplete="username"
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
          />
        </div>

        <button type="submit" className="primary-button full-width" disabled={loading}>
          {loading ? 'Logging in...' : 'Log In'}
        </button>
      </form>

      <div className="auth-footer">
        <button
          type="button"
          className="auth-link-button"
          onClick={onSwitchToForgotPassword}
        >
          Forgot Password?
        </button>
        <p>
          Don't have an account?{' '}
          <span 
            onClick={onSwitchToRegister}
            style={{ color: 'var(--fs-gold)', cursor: 'pointer', fontWeight: '500' }}
          >
            Sign up
          </span>
        </p>
      </div>
    </div>
  );
}

export default Login;