import { useState, useEffect } from "react";
import ExpandableSettingsCard from "./ExpandableSettingsCard";
import { CheckCircle, XCircle, Loader2, User } from "lucide-react";
import accountService from "../../services/accountService";
import { useAuth } from "../../hooks/useAuth";

function ChangeUsernameCard({ isOpen, onToggle, currentUsername }) {
    const { user, setUser } = useAuth();
    const activeCurrentUsername = user?.username || currentUsername || "";

    const [newUsername, setNewUsername] = useState("");
    const [checking, setChecking] = useState(false);
    const [available, setAvailable] = useState(null);
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        if (newUsername.length < 3) {
            return;
        }

        let active = true;

        const timer = setTimeout(async () => {
            try {
                setChecking(true);
                const response = await accountService.checkUsernameAvailability(newUsername);
                
                if (active) {
                    setAvailable(response.data.available);
                }
            } catch {
                if (active) {
                    setAvailable(false);
                }
            } finally {
                if (active) {
                    setChecking(false);
                }
            }
        }, 500);

        return () => {
            active = false;
            clearTimeout(timer);
        };
    }, [newUsername]);

    const handleUsernameChange = (e) => {
        const value = e.target.value
            .toLowerCase()
            .replace(/[^a-z0-9_]/g, "")
            .replace(/_+/g, "_")
            .replace(/^_+/, "")
            .slice(0, 20);

        setNewUsername(value);
        setSuccess("");
        setError("");
        setAvailable(null);
    };

    const handleSubmit = async () => {
        const finalUsername = newUsername.replace(/_+$/, "");
        
        if (!finalUsername.trim() || finalUsername.length < 3) {
            setError("Username must be at least 3 characters.");
            return;
        }

        try {
            setLoading(true);
            setError("");
            setSuccess("");

            await accountService.changeUsername(finalUsername);

            setUser({
                ...user,
                username: finalUsername
            });

            setSuccess("Username updated successfully.");
            setNewUsername("");
            setAvailable(null);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to update username.");
        } finally {
            setLoading(false);
        }
    };

    const isSubmitDisabled = loading || checking || available !== true || newUsername === activeCurrentUsername;

    return (
        <ExpandableSettingsCard title="Change Username" isOpen={isOpen} onToggle={onToggle}>
            <div className="username-content" style={{ display: "flex", flexDirection: "column", gap: "1.2rem" }}>
                
                {/* Current Username */}
                <div className="input-group">
                    <label style={{ display: "block", marginBottom: "6px", fontSize: "0.9rem", color: "var(--fs-muted)", fontWeight: "500" }}>
                        Current Username
                    </label>
                    <div style={{ 
                        padding: "12px 16px", 
                        background: "var(--fs-surface-2)", 
                        border: "1px solid var(--fs-border)", 
                        borderRadius: "8px",
                        color: "var(--fs-muted)",
                        display: "flex",
                        alignItems: "center",
                        gap: "10px"
                    }}>
                        <User size={18} />
                        <span>{activeCurrentUsername || "No username set"}</span>
                    </div>
                </div>

                {/* New Username Input Header */}
                <div className="input-group">
                    <div className="username-header" style={{ display: "flex", justifyContent: "space-between", marginBottom: "6px" }}>
                        <label style={{ fontSize: "0.9rem", color: "var(--fs-muted)", fontWeight: "500" }}>New Username</label>
                        <span style={{ fontSize: "0.85rem", color: "var(--fs-subtle)" }}>{newUsername.length}/20</span>
                    </div>

                    <input
                        type="text"
                        value={newUsername}
                        onChange={handleUsernameChange}
                        placeholder="Enter new username"
                        style={{
                            width: "100%",
                            padding: "12px",
                            background: "var(--fs-surface-2)",
                            color: "var(--fs-text)",
                            border: "1px solid var(--fs-border)",
                            borderRadius: "8px",
                            outline: "none"
                        }}
                    />
                </div>

                {/* Username Rules */}
                <div className="username-rules" style={{ fontSize: "0.85rem", color: "var(--fs-subtle)", display: "flex", gap: "12px", flexWrap: "wrap" }}>
                    <span>✓ Lowercase</span>
                    <span>✓ Numbers</span>
                    <span>✓ Underscores (_)</span>
                </div>

                {/* Status Messages */}
                <div className="status-messages">
                    {newUsername.length > 0 && newUsername.length < 3 && (
                        <p className="info-message" style={{ display: 'flex', alignItems: 'center', gap: '6px', margin: 0, fontSize: "0.9rem", color: "var(--fs-muted)" }}>
                            Username must be at least 3 characters.
                        </p>
                    )}
                    
                    {checking && (
                        <p className="checking-message" style={{ display: 'flex', alignItems: 'center', gap: '6px', margin: 0, fontSize: "0.9rem", color: "var(--fs-muted)" }}>
                            <Loader2 size={16} className="icon-spin" /> Checking availability...
                        </p>
                    )}
                    
                    {!checking && available === true && (
                        <p className="success-message" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--fs-green)', margin: 0, fontSize: "0.9rem" }}>
                            <CheckCircle size={16} /> Username available
                        </p>
                    )}
                    
                    {!checking && available === false && (
                        <p className="error-message" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--fs-red)', margin: 0, fontSize: "0.9rem" }}>
                            <XCircle size={16} /> Username already taken
                        </p>
                    )}

                    {error && <p className="error-message" style={{ color: "var(--fs-red)", margin: 0, padding: "10px", background: "var(--fs-red-bg)", borderRadius: "8px", fontSize: "0.9rem" }}>{error}</p>}
                    {success && <p className="success-message" style={{ color: "var(--fs-green)", margin: 0, padding: "10px", background: "rgba(34, 197, 94, 0.1)", borderRadius: "8px", fontSize: "0.9rem" }}>{success}</p>}
                </div>

                {/* Themed Submit Button */}
                <button
                    onClick={handleSubmit}
                    disabled={isSubmitDisabled}
                    style={{ 
                        marginTop: "10px",
                        padding: "14px 24px",
                        background: "var(--fs-gold)",
                        color: "var(--fs-bg)",
                        border: "none",
                        borderRadius: "8px",
                        fontSize: "1rem",
                        fontWeight: "600",
                        cursor: isSubmitDisabled ? "not-allowed" : "pointer",
                        opacity: isSubmitDisabled ? 0.6 : 1,
                        transition: "all 0.3s ease",
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                        width: "100%"
                    }}
                >
                    {loading ? "Updating..." : "Update Username"}
                </button>
            </div>
        </ExpandableSettingsCard>
    );
}

export default ChangeUsernameCard;