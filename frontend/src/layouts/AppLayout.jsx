
import { useEffect, useState } from "react";
import { Menu, X } from "lucide-react";
import { Outlet, useLocation } from "react-router-dom";

import Sidebar from "../components/layout/Sidebar";
import ActiveWorkoutBanner from "../components/workouts/ActiveWorkoutBanner/ActiveWorkoutBanner";

import "../styles/layout/appLayout.css";

function AppLayout() {
    const location = useLocation();
    const pageTitles = {
    "/dashboard": "Dashboard",
    "/nutrition": "Nutrition",
    "/workouts": "Workouts",
    "/measurements": "Body Measurements",
    "/progress": "Progress",
    "/health": "Health Assessment",
    "/macro-tracker": "Macro Tracker",
    "/exercises": "Exercise Library",
    "/favorites": "Favorites",
    "/profile": "Profile",
    "/settings": "Settings",
};

const currentPage =
    pageTitles[location.pathname] || "FitSphere";

    const [sidebarOpen, setSidebarOpen] = useState(false);

    useEffect(() => {
    if (sidebarOpen) {
        document.body.style.overflow = "hidden";
    } else {
        document.body.style.overflow = "";
    }

    return () => {
        document.body.style.overflow = "";
    };
}, [sidebarOpen]);
useEffect(() => {

    const handleResize = () => {

        if (window.innerWidth > 768) {
            setSidebarOpen(false);
        }

    };

    window.addEventListener("resize", handleResize);

    return () => window.removeEventListener("resize", handleResize);

}, []);

    return (
        <>
<header className="mobile-header">

    <button
        className="menu-button"
        onClick={() => setSidebarOpen(!sidebarOpen)}
        aria-label={sidebarOpen ? "Close navigation menu" : "Open navigation menu"}
        aria-expanded={sidebarOpen}
    >
        {sidebarOpen ? (
            <X size={28} strokeWidth={2.5} />
        ) : (
            <Menu size={28} strokeWidth={2.5} />
        )}
    </button>

    <h2>{currentPage}</h2>

</header>

            {sidebarOpen && (
                <div
                    className="sidebar-overlay"
                    onClick={() => setSidebarOpen(false)}
                />
            )}

            <div className="app-layout">

                <Sidebar
                    isOpen={sidebarOpen}
                    closeSidebar={() => setSidebarOpen(false)}
                />

                <main className="app-content">
                    <ActiveWorkoutBanner />

                    <Outlet />
                    
                </main>

            </div>
        </>
    );
}

export default AppLayout;