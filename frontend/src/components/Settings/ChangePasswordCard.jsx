import  { useState } from "react";
import ExpandableSettingsCard from "./ExpandableSettingsCard";
import { Eye, EyeOff, CheckCircle, XCircle } from "lucide-react";

import accountService from "../../services/accountService";

function ChangePasswordCard({ isOpen, onToggle }) {
    // Step 2: State
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showCurrent, setShowCurrent] = useState(false);
    const [showNew, setShowNew] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState("");
    const [error, setError] = useState("");

    // Step 3: Live Validation Checks
    const passwordChecks = {
        length: newPassword.length >= 8,
        uppercase: /[A-Z]/.test(newPassword),
        lowercase: /[a-z]/.test(newPassword),
        number: /\d/.test(newPassword),
        special: /[^A-Za-z0-9]/.test(newPassword),
    };

    // Calculate score for Strength Meter
    const checkScore = Object.values(passwordChecks).filter(Boolean).length;
    const allChecksPass = checkScore === 5;
    
    // Check if passwords match
    const isConfirmDirty = confirmPassword.length > 0;
    const passwordsMatch = newPassword === confirmPassword && newPassword.length > 0;

    // Step 4: Strength Meter Calculation using CSS Variables
    let strengthLabel = "Weak";
    let strengthWidth = "33%";
    let strengthColor = "var(--fs-red)";

    if (checkScore >= 3 && checkScore <= 4) {
        strengthLabel = "Medium";
        strengthWidth = "66%";
        strengthColor = "var(--fs-gold)";
    } else if (checkScore === 5) {
        strengthLabel = "Strong";
        strengthWidth = "100%";
        strengthColor = "var(--fs-green)";
    }

    if (newPassword.length === 0) {
        strengthWidth = "0%";
        strengthLabel = "";
    }

    const isSubmitDisabled = !currentPassword || !allChecksPass || !passwordsMatch || loading;

    // Step 7: Submit
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSubmitDisabled) return;

        try {
            setLoading(true);
            setError("");
            setSuccess("");

            await accountService.changePassword(currentPassword, newPassword);

            setSuccess("Password updated successfully.");
            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");
            
            // Clear success message after 3 seconds automatically
            setTimeout(() => setSuccess(""), 3000);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to update password.");
        } finally {
            setLoading(false);
        }
    };

    // Reusable styles for inputs
    const inputStyle = {
        width: "100%", 
        padding: "12px 40px 12px 12px",
        background: "var(--fs-surface-2)",
        color: "var(--fs-text)",
        border: "1px solid var(--fs-border)",
        borderRadius: "8px",
        outline: "none"
    };

    const labelStyle = {
        color: "var(--fs-muted)",
        fontSize: "0.9rem",
        marginBottom: "6px",
        display: "block",
        fontWeight: "500"
    };

    return (
        <ExpandableSettingsCard
            title="Change Password"
            isOpen={isOpen}
            onToggle={onToggle}
        >
            <form onSubmit={handleSubmit} className="password-content" style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
                
                {/* Step 1: UI Layout - Current Password */}
                <div className="input-group">
                    <label style={labelStyle}>Current Password</label>
                    <div className="password-input-wrapper" style={{ position: "relative" }}>
                        <input
                            type={showCurrent ? "text" : "password"}
                            value={currentPassword}
                            onChange={(e) => {
                                setCurrentPassword(e.target.value);
                                setError("");
                                setSuccess("");
                            }}
                            placeholder="Enter current password"
                            style={inputStyle}
                        />
                        <button 
                            type="button" 
                            onClick={() => setShowCurrent(!showCurrent)}
                            className="toggle-visibility-btn"
                            style={{ position: "absolute", right: "12px", top: "50%", transform: "translateY(-50%)", background: "none", border: "none", color: "var(--fs-muted)", cursor: "pointer", padding: 0, display: "flex" }}
                        >
                            {showCurrent ? <EyeOff size={18} /> : <Eye size={18} />}
                        </button>
                    </div>
                </div>

                {/* New Password */}
                <div className="input-group">
                    <label style={labelStyle}>New Password</label>
                    <div className="password-input-wrapper" style={{ position: "relative" }}>
                        <input
                            type={showNew ? "text" : "password"}
                            value={newPassword}
                            onChange={(e) => {
                                setNewPassword(e.target.value);
                                setError("");
                                setSuccess("");
                            }}
                            placeholder="Enter new password"
                            style={inputStyle}
                        />
                        <button 
                            type="button" 
                            onClick={() => setShowNew(!showNew)}
                            className="toggle-visibility-btn"
                            style={{ position: "absolute", right: "12px", top: "50%", transform: "translateY(-50%)", background: "none", border: "none", color: "var(--fs-muted)", cursor: "pointer", padding: 0, display: "flex" }}
                        >
                            {showNew ? <EyeOff size={18} /> : <Eye size={18} />}
                        </button>
                    </div>
                </div>

                {/* Strength Meter */}
                <div className="strength-meter-container">
                    <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "8px", fontSize: "0.85rem", color: "var(--fs-muted)" }}>
                        <span>Password Strength</span>
                        <span style={{ color: strengthColor, fontWeight: 600 }}>{strengthLabel}</span>
                    </div>
                    <div className="strength-bar-bg" style={{ height: "6px", background: "var(--fs-border)", borderRadius: "999px", overflow: "hidden" }}>
                        <div 
                            className="strength-bar-fill" 
                            style={{ 
                                height: "100%", 
                                width: strengthWidth, 
                                background: strengthColor,
                                transition: "all 0.3s ease" 
                            }} 
                        />
                    </div>
                </div>

                {/* Requirements Checklist */}
                <div className="password-rules" style={{ display: "flex", flexDirection: "column", gap: "8px", fontSize: "0.9rem" }}>
                    <p style={{ color: passwordChecks.length ? "var(--fs-green)" : "var(--fs-subtle)", display: "flex", alignItems: "center", gap: "8px", margin: 0, transition: "color 0.3s ease" }}>
                        {passwordChecks.length ? <CheckCircle size={16} /> : <span style={{ width: "16px", textAlign: "center" }}>○</span>} At least 8 characters
                    </p>
                    <p style={{ color: passwordChecks.uppercase ? "var(--fs-green)" : "var(--fs-subtle)", display: "flex", alignItems: "center", gap: "8px", margin: 0, transition: "color 0.3s ease" }}>
                        {passwordChecks.uppercase ? <CheckCircle size={16} /> : <span style={{ width: "16px", textAlign: "center" }}>○</span>} One uppercase letter
                    </p>
                    <p style={{ color: passwordChecks.lowercase ? "var(--fs-green)" : "var(--fs-subtle)", display: "flex", alignItems: "center", gap: "8px", margin: 0, transition: "color 0.3s ease" }}>
                        {passwordChecks.lowercase ? <CheckCircle size={16} /> : <span style={{ width: "16px", textAlign: "center" }}>○</span>} One lowercase letter
                    </p>
                    <p style={{ color: passwordChecks.number ? "var(--fs-green)" : "var(--fs-subtle)", display: "flex", alignItems: "center", gap: "8px", margin: 0, transition: "color 0.3s ease" }}>
                        {passwordChecks.number ? <CheckCircle size={16} /> : <span style={{ width: "16px", textAlign: "center" }}>○</span>} One number
                    </p>
                    <p style={{ color: passwordChecks.special ? "var(--fs-green)" : "var(--fs-subtle)", display: "flex", alignItems: "center", gap: "8px", margin: 0, transition: "color 0.3s ease" }}>
                        {passwordChecks.special ? <CheckCircle size={16} /> : <span style={{ width: "16px", textAlign: "center" }}>○</span>} One special character
                    </p>
                </div>

                {/* Confirm Password */}
                <div className="input-group">
                    <label style={labelStyle}>Confirm Password</label>
                    <div className="password-input-wrapper" style={{ position: "relative" }}>
                        <input
                            type={showConfirm ? "text" : "password"}
                            value={confirmPassword}
                            onChange={(e) => {
                                setConfirmPassword(e.target.value);
                                setError("");
                                setSuccess("");
                            }}
                            placeholder="Confirm new password"
                            style={inputStyle}
                        />
                        <button 
                            type="button" 
                            onClick={() => setShowConfirm(!showConfirm)}
                            className="toggle-visibility-btn"
                            style={{ position: "absolute", right: "12px", top: "50%", transform: "translateY(-50%)", background: "none", border: "none", color: "var(--fs-muted)", cursor: "pointer", padding: 0, display: "flex" }}
                        >
                            {showConfirm ? <EyeOff size={18} /> : <Eye size={18} />}
                        </button>
                    </div>
                    
                    {/* Step 5: Live Confirm Feedback */}
                    {isConfirmDirty && (
                        <div style={{ marginTop: "8px", fontSize: "0.9rem" }}>
                            {passwordsMatch ? (
                                <span style={{ color: "var(--fs-green)", display: "flex", alignItems: "center", gap: "6px" }}><CheckCircle size={16}/> Passwords match</span>
                            ) : (
                                <span style={{ color: "var(--fs-red)", display: "flex", alignItems: "center", gap: "6px" }}><XCircle size={16}/> Passwords do not match</span>
                            )}
                        </div>
                    )}
                </div>

                {/* Status Messages */}
                {error && <p className="error-message" style={{ color: "var(--fs-red)", margin: 0, padding: "10px", background: "var(--fs-red-bg)", borderRadius: "8px", fontSize: "0.9rem" }}>{error}</p>}
                {success && <p className="success-message" style={{ color: "var(--fs-green)", margin: 0, padding: "10px", background: "rgba(34, 197, 94, 0.1)", borderRadius: "8px", fontSize: "0.9rem" }}>{success}</p>}

                {/* Step 6: Themed Submit Button */}
                <button
                    type="submit"
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
                        alignItems: "center"
                    }}
                >
                    {loading ? "Updating..." : "Update Password"}
                </button>
            </form>
        </ExpandableSettingsCard>
    );
}

export default ChangePasswordCard;