import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function PublicRoute() {
    const {
        loading,
        isAuthenticated,
        userRole
    } = useAuth();

    if (loading) {
        return null;
    }

    if (isAuthenticated && userRole === "ROLE_ADMIN") {
        return <Navigate to="/admin/usage" replace />;
    }
    else if (isAuthenticated) {
        return <Navigate to="/app/dashboard" replace />;
    }

    return <Outlet />;
}

export default PublicRoute;