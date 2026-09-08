import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function AdminRoute() {
    const { loading, isAuthenticated, userRole } = useAuth();


    if (loading) {
        return null;
    }

    if (!isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    if (userRole !== "ROLE_ADMIN") {
        return <Navigate to="/401" replace />;
    }

    return <Outlet />;
}

export default AdminRoute;