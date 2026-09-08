import { NavLink,useNavigate } from "react-router-dom";
import { X } from "lucide-react";
import "../../styles/layout/sidebar.css";
import { useAuth } from "../../hooks/useAuth";
import { useState } from "react";
import ConfirmDialog from "../common/ConfirmDailog/ConfirmDailog";



function Sidebar({isOpen , closeSidebar}) {
    const navigate = useNavigate();
    const {logout}=useAuth();
    const [showLogoutDialog, setShowLogoutDialog] = useState(false);
    const handleNavigation = () => {closeSidebar?.();};

const handleLogout = async () => {
    await logout();
    closeSidebar?.();

        navigate("/", { replace: true });

};
    return (
        <>
    <aside className={`sidebar ${isOpen ? "open" : ""}`}>
<div className="sidebar-header">

    <div className="sidebar-logo">
        <h2>FITSPHERE</h2>
    </div>

   <button
    className="sidebar-close"
    onClick={closeSidebar}
    aria-label="Close navigation menu"
>
    <X size={26} strokeWidth={2.5} />
</button>

</div>
            <nav className="sidebar-nav">

                <NavLink
                    to="/app/dashboard"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span>Dashboard</span>
                </NavLink>

                <hr />

                <NavLink
                    to="/app/nutrition"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span>Nutrition</span>
                </NavLink>

                <NavLink
                    to="/app/workouts"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span> Workouts</span>
                </NavLink>

                <NavLink
                    to="/app/measurements"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span>Body Measurements</span>
                </NavLink>

                <NavLink
                    to="/app/progress"
                    className="sidebar-link"
                    onClick={handleNavigation}
                >
                    <span>Progress</span>
                </NavLink>

                <hr />

                <NavLink
                    to="/app/health-assessment"
                    className="sidebar-link"
                    onClick={handleNavigation}
                >
                   <span>Health Assessment</span>
                </NavLink>

                <NavLink
                    to="/app/macro-tracker"
                    className="sidebar-link"
                    onClick={handleNavigation}
                >
                   <span>Macro Tracker</span> 
                </NavLink>

                <NavLink
                    to="/app/exercises"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span>Explore Exercises</span>
                </NavLink>

                <hr />

                <NavLink
                    to="/app/favorites"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span>Favorites</span>
                </NavLink>

                <NavLink
                    to="/app/profile"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span>Profile</span>
                </NavLink>

                <NavLink
                    to="/app/settings"
                    className="sidebar-link"
                    onClick={handleNavigation}

                >
                    <span>Settings</span>
                </NavLink>

                <NavLink 
                to="/app/contact"
                className="sidebar-link"
                onClick={handleNavigation}
                >
                    <span>Contact Us</span>
                </NavLink>

                <hr />

                <button className="logout-button" onClick={()=>{setShowLogoutDialog(true); handleNavigation();}}>
                    <span>Logout</span>
                </button>

            </nav>

        </aside>  
          <ConfirmDialog
    open={showLogoutDialog}
    title="Logout"
    message="Are you sure you want to logout?"
    confirmText="Logout"
    cancelText="Cancel"
    onConfirm={async () => {
        setShowLogoutDialog(false);
        await handleLogout();
    }}
    onCancel={() => setShowLogoutDialog(false)}
/></>
        
    );

}

export default Sidebar;