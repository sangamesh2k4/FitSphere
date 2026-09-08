import  { useState, useEffect, useRef } from "react";
import ExpandableSettingsCard from "./ExpandableSettingsCard";
import { CheckCircle, XCircle, Loader2, Mail } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import accountService from "../../services/accountService";

function ChangeEmailCard({ isOpen, onToggle }) {
    const [step, setStep] = useState(1);
    const [newEmail, setNewEmail] = useState("");
    const [otp, setOtp] = useState(new Array(6).fill(""));
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [timer, setTimer] = useState(30);

    const { user } = useAuth();
    const currentEmail = user?.email ?? "";
    const inputRefs = useRef([]);

    useEffect(() => {
        let interval;
        if (step === 2 && timer > 0) {
            interval = setInterval(() => {
                setTimer((prev) => prev - 1);
            }, 1000);
        }
        return () => clearInterval(interval);
    }, [step, timer]);

    const isValidEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    const isSameEmail = newEmail.toLowerCase() === currentEmail.toLowerCase();
    const isOtpComplete = otp.every((digit) => digit !== "");

    const handleSendOtp = async () => {
        if (!isValidEmail(newEmail)) {
            setError("Please enter a valid email address.");
            return;
        }
        if (isSameEmail) {
            setError("New email cannot be the same as your current email.");
            return;
        }

        try {
            setLoading(true);
            setError("");
            setSuccess("");

            await accountService.sendEmailChangeOtp(newEmail);

            setStep(2);
            setTimer(30);
            setSuccess(`OTP sent to ${newEmail}`);
            setTimeout(() => setSuccess(""), 4000);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to send OTP.");
        } finally {
            setLoading(false);
        }
    };

    const handleResendOtp = async () => {
        try {
            setError("");
            setSuccess("");
            setTimer(30);

            await accountService.sendEmailChangeOtp(newEmail);
            setSuccess("A new OTP has been sent.");
            setTimeout(() => setSuccess(""), 4000);
        } catch {
            setError("Failed to resend OTP.");
        }
    };

    const handleVerifyOtp = async () => {
        if (!isOtpComplete) return;

        try {
            setLoading(true);
            setError("");
            setSuccess("");
            
            await accountService.verifyEmail(newEmail, otp.join(""));

            setSuccess("Email updated successfully.");
            setNewEmail("");
            setOtp(new Array(6).fill(""));
            setStep(1); 
        } catch (err) {
            setError(err.response?.data?.message || "Invalid OTP. Please try again.");
            setOtp(new Array(6).fill("")); 
            if (inputRefs.current[0]) inputRefs.current[0].focus();
        } finally {
            setLoading(false);
        }
    };

    const handleOtpChange = (e, index) => {
        const value = e.target.value;
        if (isNaN(value)) return; 

        const newOtp = [...otp];
        newOtp[index] = value.substring(value.length - 1);
        setOtp(newOtp);

        if (value && index < 5) {
            inputRefs.current[index + 1].focus();
        }
    };

    const handleOtpKeyDown = (e, index) => {
        if (e.key === "Backspace" && !otp[index] && index > 0) {
            const newOtp = [...otp];
            newOtp[index - 1] = "";
            setOtp(newOtp);
            inputRefs.current[index - 1].focus();
        }
    };

    const handleOtpPaste = (e) => {
        e.preventDefault();
        const pastedData = e.clipboardData.getData("text").replace(/\D/g, "").slice(0, 6);
        
        if (!pastedData) return;

        const newOtp = [...otp];
        for (let i = 0; i < pastedData.length; i++) {
            newOtp[i] = pastedData[i];
        }
        setOtp(newOtp);

        const focusIndex = pastedData.length < 6 ? pastedData.length : 5;
        inputRefs.current[focusIndex].focus();
    };

    // Shared Button Style Helper
    const getButtonStyle = (isDisabled) => ({
        marginTop: "10px",
        padding: "14px 24px",
        background: "var(--fs-gold)",
        color: "var(--fs-bg)",
        border: "none",
        borderRadius: "8px",
        fontSize: "1rem",
        fontWeight: "600",
        cursor: isDisabled ? "not-allowed" : "pointer",
        opacity: isDisabled ? 0.6 : 1,
        transition: "all 0.3s ease",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        width: "100%"
    });

    const isStep1Disabled = loading || !newEmail || isSameEmail;
    const isStep2Disabled = loading || !isOtpComplete;

    return (
        <ExpandableSettingsCard title="Change Email" isOpen={isOpen} onToggle={onToggle}>
            <div className="email-content" style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
                
                {/* Current Email */}
                <div className="input-group">
                    <label style={{ color: "var(--fs-muted)", fontSize: "0.9rem", marginBottom: "6px", display: "block", fontWeight: "500" }}>
                        Current Email
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
                        <Mail size={18} />
                        {currentEmail}
                    </div>
                </div>

                {/* Step 1: Enter New Email */}
                {step === 1 && (
                    <>
                        <div className="input-group">
                            <label style={{ color: "var(--fs-muted)", fontSize: "0.9rem", marginBottom: "6px", display: "block", fontWeight: "500" }}>
                                New Email
                            </label>
                            <input
                                type="email"
                                value={newEmail}
                                onChange={(e) => {
                                    setNewEmail(e.target.value.trim());
                                    setError("");
                                }}
                                placeholder="Enter new email address"
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

                        {error && <p className="error-message" style={{ color: "var(--fs-red)", margin: 0, padding: "10px", background: "var(--fs-red-bg)", borderRadius: "8px", fontSize: "0.9rem", display: "flex", alignItems: "center", gap: "6px" }}><XCircle size={16} />{error}</p>}
                        {success && <p className="success-message" style={{ color: "var(--fs-green)", margin: 0, padding: "10px", background: "rgba(34, 197, 94, 0.1)", borderRadius: "8px", fontSize: "0.9rem", display: "flex", alignItems: "center", gap: "6px" }}><CheckCircle size={16} />{success}</p>}

                        <button
                            onClick={handleSendOtp}
                            disabled={isStep1Disabled}
                            style={getButtonStyle(isStep1Disabled)}
                        >
                            {loading ? (
                                <span style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: "8px" }}>
                                    <Loader2 size={18} className="icon-spin" /> Sending...
                                </span>
                            ) : "Send OTP"}
                        </button>
                    </>
                )}

                {/* Step 2: OTP Verification */}
                {step === 2 && (
                    <>
                        <div className="input-group">
                            <label style={{ color: "var(--fs-muted)", fontSize: "0.9rem", marginBottom: "6px", display: "block", fontWeight: "500" }}>
                                New Email
                            </label>
                            <div style={{ padding: "12px 16px", background: "var(--fs-surface-2)", border: "1px solid var(--fs-border)", borderRadius: "8px", color: "var(--fs-text)", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                                <span>{newEmail}</span>
                                <button 
                                    onClick={() => setStep(1)}
                                    style={{ background: "none", border: "none", color: "var(--fs-gold)", cursor: "pointer", fontSize: "0.9rem", fontWeight: 600 }}
                                >
                                    Edit
                                </button>
                            </div>
                        </div>

                        <div className="input-group">
                            <label style={{ display: "flex", justifyContent: "space-between", color: "var(--fs-muted)", fontSize: "0.9rem", marginBottom: "6px", fontWeight: "500" }}>
                                Enter 6-digit OTP
                                <span style={{ color: "var(--fs-subtle)", fontSize: "0.85rem", fontWeight: 400 }}>Sent to your new email</span>
                            </label>
                            
                            <div style={{ display: "flex", gap: "10px", justifyContent: "space-between", marginTop: "8px" }} onPaste={handleOtpPaste}>
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        ref={(el) => (inputRefs.current[index] = el)}
                                        type="text"
                                        inputMode="numeric"
                                        maxLength={1}
                                        value={digit}
                                        onChange={(e) => handleOtpChange(e, index)}
                                        onKeyDown={(e) => handleOtpKeyDown(e, index)}
                                        style={{
                                            width: "48px",
                                            height: "56px",
                                            textAlign: "center",
                                            fontSize: "1.5rem",
                                            fontWeight: 600,
                                            borderRadius: "8px",
                                            border: `1px solid ${digit ? "var(--fs-gold)" : "var(--fs-border)"}`,
                                            background: "var(--fs-surface-2)",
                                            color: "var(--fs-text)",
                                            outline: "none",
                                            transition: "border-color 0.2s ease"
                                        }}
                                    />
                                ))}
                            </div>
                        </div>

                        {/* Resend Timer */}
                        <div style={{ display: "flex", justifyContent: "flex-end", fontSize: "0.9rem" }}>
                            {timer > 0 ? (
                                <span style={{ color: "var(--fs-muted)" }}>Resend OTP in {timer}s</span>
                            ) : (
                                <button 
                                    onClick={handleResendOtp}
                                    style={{ background: "none", border: "none", color: "var(--fs-gold)", cursor: "pointer", fontWeight: 600, padding: 0 }}
                                >
                                    Resend OTP
                                </button>
                            )}
                        </div>

                        {error && <p className="error-message" style={{ color: "var(--fs-red)", margin: 0, padding: "10px", background: "var(--fs-red-bg)", borderRadius: "8px", fontSize: "0.9rem", display: "flex", alignItems: "center", gap: "6px" }}><XCircle size={16} />{error}</p>}
                        {success && <p className="success-message" style={{ color: "var(--fs-green)", margin: 0, padding: "10px", background: "rgba(34, 197, 94, 0.1)", borderRadius: "8px", fontSize: "0.9rem", display: "flex", alignItems: "center", gap: "6px" }}><CheckCircle size={16} />{success}</p>}

                        <button
                            onClick={handleVerifyOtp}
                            disabled={isStep2Disabled}
                            style={getButtonStyle(isStep2Disabled)}
                        >
                            {loading ? (
                                <span style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: "8px" }}>
                                    <Loader2 size={18} className="icon-spin" /> Verifying...
                                </span>
                            ) : "Verify & Update Email"}
                        </button>
                    </>
                )}
            </div>
        </ExpandableSettingsCard>
    );
}

export default ChangeEmailCard;