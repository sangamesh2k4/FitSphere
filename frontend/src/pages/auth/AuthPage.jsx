import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";

import Login from "../auth/Login";
import Register from "../auth/Register";
import VerifyEmail from "../auth/VerifyEmail";
import ForgotPassword from "../auth/ForgotPassword";
import VerifyResetOtp from "../auth/VerifyResetOtp";
import ResetPassword from "../auth/ResetPassword";

import "../../css/AuthPage.css";

function AuthPage() {
    const location = useLocation();
    const navigate = useNavigate();

    const isRegisterRoute = location.pathname === "/register";

    const [authStep, setAuthStep] = useState(
        isRegisterRoute ? "register" : "login"
    );

    const [authData, setAuthData] = useState({
        email: "",
        resetToken: ""
    });

    const showAuthSwitch =
        authStep === "login" ||
        authStep === "register";

    const switchToLogin = () => {
        setAuthStep("login");
        navigate("/login");
    };

    const switchToRegister = () => {
        setAuthStep("register");
        navigate("/register");
    };

    return (
        <main className="auth-page">

            <div className="auth-overlay" />

            <div className="auth-content">

                <div className="auth-panel">

                    {/* LOGIN / REGISTER PILL */}
                    {showAuthSwitch && (
                        <div className="auth-switch">

                            <div
                                className={`auth-switch-indicator ${
                                    authStep === "register"
                                        ? "register"
                                        : "login"
                                }`}
                            />

                            <button
                                type="button"
                                className={
                                    authStep === "login"
                                        ? "active"
                                        : ""
                                }
                                onClick={switchToLogin}
                            >
                                Login
                            </button>

                            <button
                                type="button"
                                className={
                                    authStep === "register"
                                        ? "active"
                                        : ""
                                }
                                onClick={switchToRegister}
                            >
                                Sign Up
                            </button>

                        </div>
                    )}

                    <div className="auth-form-container">

                        {/* LOGIN */}
                        {authStep === "login" && (
                            <Login
                                onSwitchToRegister={switchToRegister}
                                onSwitchToForgotPassword={() =>
                                    setAuthStep("forgot-password")
                                }
                            />
                        )}

                        {/* REGISTER */}
                        {authStep === "register" && (
                            <Register
                                onSwitchToLogin={switchToLogin}
                                onSuccess={(email) => {
                                    setAuthData({
                                        email,
                                        resetToken: ""
                                    });

                                    setAuthStep("verify-email");
                                }}
                            />
                        )}

                        {/* EMAIL VERIFICATION */}
                        {authStep === "verify-email" && (
                            <VerifyEmail
                                email={authData.email}
                                onSuccessSwitchToLogin={switchToLogin}
                            />
                        )}

                        {/* FORGOT PASSWORD */}
                        {authStep === "forgot-password" && (
                            <ForgotPassword
                                onSwitchToLogin={switchToLogin}
                                onCodeSent={(email) => {
                                    setAuthData({
                                        email,
                                        resetToken: ""
                                    });

                                    setAuthStep("verify-reset-otp");
                                }}
                            />
                        )}

                        {/* RESET OTP */}
                        {authStep === "verify-reset-otp" && (
                            <VerifyResetOtp
                                email={authData.email}
                                onSwitchToForgotPassword={() =>
                                    setAuthStep("forgot-password")
                                }
                                onVerified={(resetToken) => {
                                    setAuthData(prev => ({
                                        ...prev,
                                        resetToken
                                    }));

                                    setAuthStep("reset-password");
                                }}
                            />
                        )}

                        {/* NEW PASSWORD */}
                        {authStep === "reset-password" && (
                            <ResetPassword
                                resetToken={authData.resetToken}
                                onSuccessSwitchToLogin={switchToLogin}
                            />
                        )}

                    </div>

                </div>

            </div>

        </main>
    );
}

export default AuthPage;