import { createContext, useState, useEffect, useCallback, useMemo } from "react";
import { authService } from "../services/authService";
import accountService from "../services/accountService";

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userRole, setUserRole] = useState(null);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // INITIALIZE AUTH
    useEffect(() => {
        const initialize = async () => {
            const accessToken = localStorage.getItem("accessToken");
            const refreshToken = localStorage.getItem("refreshToken");
            const role = localStorage.getItem("userRole");

            if (!accessToken && !refreshToken) {
                setIsAuthenticated(false);
                setUserRole(null);
                setUser(null);
                setLoading(false);
                return;
            }

            try {
                const response = await accountService.getCurrentUser();
                setUser(response.data);
                setUserRole(role);
                setIsAuthenticated(true);
            } catch (error) {
                console.error("Authentication initialization failed:", error);
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                localStorage.removeItem("userRole");
                setIsAuthenticated(false);
                setUserRole(null);
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        initialize();
    }, []);

    // LOGIN
    const login = useCallback(async (credentials) => {
        const data = await authService.login(credentials);

        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);

        if (data.role) {
            localStorage.setItem("userRole", data.role);
        }

        setIsAuthenticated(true);
        setUserRole(data.role);

        const response = await accountService.getCurrentUser();
        setUser(response.data);

        return data;
    }, []);

    // LOGOUT
    const logout = useCallback(async () => {
        const refreshToken = localStorage.getItem("refreshToken");

        try {
            if (refreshToken) {
                await authService.logout({ refreshToken });
            }
        } catch (error) {
            console.error("Backend logout failed:", error);
        } finally {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            localStorage.removeItem("userRole");

            setIsAuthenticated(false);
            setUserRole(null);
            setUser(null);
        }
    }, []);

    const value = useMemo(() => ({
        isAuthenticated,
        userRole,
        user,
        loading,
        login,
        logout,
    }), [isAuthenticated, userRole, user, loading, login, logout]);

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};