import { Navigate } from 'react-router-dom';
import { useAuth } from "../hooks/useAuth";

function ProtectedRoute({ children, allowedRoles }) {
  const { isAuthenticated, userRole } = useAuth();

  if (!isAuthenticated) {
    // replace ensures the login page replaces the current route in history
    return <Navigate to="/" replace />;
  }

  // Optional: Add role-based protection since we are storing userRole
  if (allowedRoles && !allowedRoles.includes(userRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
}

export default ProtectedRoute;