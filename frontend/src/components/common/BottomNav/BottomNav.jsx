import { useState, useRef, useEffect } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
    
    Dumbbell,
    Users,
    Mail,
    ChartNoAxesCombined,
    LogOut,
    MoreHorizontal
} from "lucide-react";

import { useAuth } from "../../../hooks/useAuth";
import "./BottomNav.css";

function BottomNav() {
    const { logout } = useAuth();
    const navigate = useNavigate();
    const [showMore, setShowMore] = useState(false);
    const [showLogoutModal, setShowLogoutModal] = useState(false);
    const moreMenuRef = useRef(null);

    const handleLogout = async () => {
        try {
            await logout();
        } finally {
            setShowLogoutModal(false);
            navigate("/");
        }
    };

    // Close "More" dropdown when clicking outside
    useEffect(() => {
        function handleClickOutside(event) {
            if (moreMenuRef.current && !moreMenuRef.current.contains(event.target)) {
                setShowMore(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <>
            <nav className="admin-bottom-nav">
                <div className="admin-nav-container">

                    {/* 1. Usage */}
                    <NavLink to="/admin/usage" className="admin-nav-link">
                        <ChartNoAxesCombined size={21} />
                        <span>Usage</span>
                    </NavLink>

                    {/* 2. Users */}
                    <NavLink to="/admin/users" className="admin-nav-link">
                        <Users size={21} />
                        <span>Users</span>
                    </NavLink>

                    {/* 3. Exercises */}
                    <NavLink to="/admin/exercises" className="admin-nav-link">
                        <Dumbbell size={21} />
                        <span>Exercises</span>
                    </NavLink>

                    {/* 4. Inbox */}
                    <NavLink to="/admin/inbox" className="admin-nav-link">
                        <Mail size={21} />
                        <span>Inbox</span>
                    </NavLink>

                    {/* Desktop-only Logout button */}
                    <button
                        type="button"
                        className="admin-nav-link admin-logout-button desktop-only"
                        onClick={() => setShowLogoutModal(true)}
                    >
                        <LogOut size={21} />
                        <span>Logout</span>
                    </button>

                    {/* ── MOBILE ONLY "MORE" DROPDOWN ── */}
                    <div className="admin-more-wrapper mobile-only" ref={moreMenuRef}>
                        <button
                            type="button"
                            className={`admin-nav-link ${showMore ? "active" : ""}`}
                            onClick={() => setShowMore((prev) => !prev)}
                        >
                            <MoreHorizontal size={21} />
                            <span>More</span>
                        </button>

                        {showMore && (
                            <div className="admin-more-menu">
                                <button
                                    type="button"
                                    className="admin-more-item logout-item"
                                    onClick={() => {
                                        setShowMore(false);
                                        setShowLogoutModal(true);
                                    }}
                                >
                                    <LogOut size={18} />
                                    <span>Logout</span>
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </nav>

            {/* Logout Confirmation Modal */}
            {showLogoutModal && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal-container">
                        <h3>Confirm Logout</h3>
                        <p>Are you sure you want to log out of your admin session?</p>
                        <div className="admin-modal-actions">
                            <button
                                type="button"
                                className="admin-modal-btn-cancel"
                                onClick={() => setShowLogoutModal(false)}
                            >
                                Cancel
                            </button>
                            <button
                                type="button"
                                className="admin-modal-btn-confirm"
                                onClick={handleLogout}
                            >
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default BottomNav;