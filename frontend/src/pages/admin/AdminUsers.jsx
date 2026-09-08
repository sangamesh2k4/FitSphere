import { useEffect, useState, useMemo } from "react";
import { Power, Trash2 } from "lucide-react";
import adminService from "../../services/adminService";
import CustomSelect from "../../components/common/CustomSelect";
import LoadingSkeleton from "../../components/skeleton/LoadingSkeleton/LoadingSkeleton";
import "../../css/admin/AdminUsers.css";

function AdminUsers() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // UI states for interactions
    const [pendingDelete, setPendingDelete] = useState(null);
    const [userToDelete, setUserToDelete] = useState(null);

    // Filter states
    const [searchQuery, setSearchQuery] = useState("");
    const [roleFilter, setRoleFilter] = useState("ALL");
    const [statusFilter, setStatusFilter] = useState("ALL");

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                setLoading(true);
                const data = await adminService.getUsers(0);
                setUsers(data.content || []);
            } catch (error) {
                console.error("Failed to load users:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, []);

    // 1. Triggers the confirmation modal
    const handleDeleteClick = (user) => {
        setUserToDelete(user);
    };

    // 2. Executes the deletion logic after modal confirmation
    const confirmDelete = () => {
        if (!userToDelete) return;
        const user = userToDelete;
        
        // Close modal
        setUserToDelete(null);

        // Remove immediately from the UI
        setUsers((prev) => prev.filter((item) => item.id !== user.id));

        // Start 5-second grace period
        const timer = setTimeout(async () => {
            try {
                await adminService.deleteUser(user.id);
            } catch (error) {
                console.error("Failed to delete user:", error);

                // API failed → restore user
                setUsers((prev) =>
                    [...prev, user].sort((a, b) =>
                        a.username.localeCompare(b.username)
                    )
                );
            } finally {
                setPendingDelete(null);
            }
        }, 5000);

        setPendingDelete({ user, timer });
    };

    const handleUndoDelete = () => {
        if (!pendingDelete) return;

        clearTimeout(pendingDelete.timer);

        setUsers((prev) =>
            [...prev, pendingDelete.user].sort((a, b) =>
                a.username.localeCompare(b.username)
            )
        );

        setPendingDelete(null);
    };

    const handleToggleUser = async (user) => {
        try {
            if (user.enabled) {
                await adminService.disableUser(user.id);
            } else {
                await adminService.enableUser(user.id);
            }

            setUsers((prev) =>
                prev.map((item) =>
                    item.id === user.id
                        ? { ...item, enabled: !item.enabled }
                        : item
                )
            );
        } catch (error) {
            console.error("Failed to toggle user:", error);
        }
    };

    const filteredUsers = useMemo(() => {
        return users.filter((user) => {
            const matchesSearch =
                user.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
                user.email.toLowerCase().includes(searchQuery.toLowerCase());

            const matchesRole =
                roleFilter === "ALL" || user.role === roleFilter;

            const matchesStatus =
                statusFilter === "ALL" ||
                (statusFilter === "ACTIVE" && user.enabled) ||
                (statusFilter === "DISABLED" && !user.enabled);

            return matchesSearch && matchesRole && matchesStatus;
        });
    }, [users, searchQuery, roleFilter, statusFilter]);

    // Role options for CustomSelect
    const roleOptions = [
        { value: "ALL", label: "All Roles" },
        { value: "ROLE_ADMIN", label: "ROLE_ADMIN" },
        { value: "ROLE_USER", label: "ROLE_USER" }
    ];

    // Status options for CustomSelect
    const statusOptions = [
        { value: "ALL", label: "All Status" },
        { value: "ACTIVE", label: "Active" },
        { value: "DISABLED", label: "Disabled" }
    ];

    if (loading) {
        return <LoadingSkeleton />;
    }

    return (
        <section className="admin-users-page">
            <div className="admin-users-header">
                <h1>USERS</h1>
                <p>Manage user accounts and access.</p>
            </div>

            <div className="admin-users-controls">
                <div className="admin-search-wrapper">
                    <input
                        type="text"
                        placeholder="Search users..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="admin-search-input"
                    />
                </div>

                <div className="admin-filters-row">
                    <CustomSelect
                        name="roleFilter"
                        value={roleFilter}
                        onChange={(e) => setRoleFilter(e.target.value)}
                        options={roleOptions}
                        placeholder="Select Role"
                    />

                    <CustomSelect
                        name="statusFilter"
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                        options={statusOptions}
                        placeholder="Select Status"
                    />
                </div>
            </div>

            <div className="admin-users-count">
                {filteredUsers.length} USERS
            </div>

            <div className="admin-users-grid">
                {filteredUsers.map((user) => (
                    <article key={user.id} className="admin-user-card">
                        <div className="admin-user-primary">
                            <div className="admin-user-text">
                                <h3 className="admin-user-name">{user.username}</h3>
                                <p className="admin-user-email">{user.email}</p>
                            </div>
                            <span className={`admin-user-status mobile-only ${user.enabled ? "active" : "disabled"}`}>
                                {user.enabled ? "Active" : "Disabled"}
                            </span>
                        </div>

                        <div className="admin-user-meta-actions">
                            <div className="admin-user-badges">
                                <span className={`admin-user-role ${user.role === "ROLE_ADMIN" ? "role-admin" : "role-user"}`}>
                                    {user.role}
                                </span>
                                <span className={`admin-user-status desktop-only ${user.enabled ? "active" : "disabled"}`}>
                                    {user.enabled ? "Active" : "Disabled"}
                                </span>
                            </div>

                            <div className="admin-user-actions">
                                <button
                                    type="button"
                                    className="admin-action-btn power-btn"
                                    onClick={() => handleToggleUser(user)}
                                    title={user.enabled ? "Disable User" : "Enable User"}
                                >
                                    <Power size={16} />
                                </button>
                                <button
                                    type="button"
                                    className="admin-action-btn delete-btn"
                                    onClick={() => handleDeleteClick(user)}
                                    title="Delete User"
                                >
                                    <Trash2 size={16} />
                                </button>
                            </div>
                        </div>
                    </article>
                ))}
            </div>

            {/* Custom Confirmation Modal */}
            {userToDelete && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal">
                        <h3>Confirm Deletion</h3>
                        <p>Are you sure you want to delete <strong>{userToDelete.username}</strong>?</p>
                        <div className="admin-modal-actions">
                            <button 
                                type="button" 
                                className="admin-btn-cancel" 
                                onClick={() => setUserToDelete(null)}
                            >
                                Cancel
                            </button>
                            <button 
                                type="button" 
                                className="admin-btn-delete" 
                                onClick={confirmDelete}
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Snackbar for Undo */}
            {pendingDelete && (
                <div className="admin-snackbar">
                    <span className="admin-snackbar-text">
                        <strong>{pendingDelete.user.username}</strong> will be permanently deleted.
                    </span>
                    <button type="button" className="admin-snackbar-undo" onClick={handleUndoDelete}>
                        UNDO
                    </button>
                </div>
            )}
        </section>
    );
}

export default AdminUsers;