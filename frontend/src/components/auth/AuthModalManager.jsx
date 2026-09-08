import { useContext } from "react";

import Modal from "../common/Modal";

import Login from "../../pages/auth/Login";
import Register from "../../pages/auth/Register";
import VerifyEmail from "../../pages/auth/VerifyEmail";
import ForgotPassword from "../../pages/auth/ForgotPassword";
import VerifyResetOtp from "../../pages/auth/VerifyResetOtp";
import ResetPassword from "../../pages/auth/ResetPassword";

import { AuthModalContext } from "../../context/AuthModalContext";

function AuthModalManager() {

    const {
        activeModal,
        setActiveModal,
        modalData,
        setModalData,
        closeModal
    } = useContext(AuthModalContext);

    return (
        <Modal
            isOpen={activeModal !== null}
            onClose={closeModal}
        >

            {activeModal === "login" && (
                <Login
                    onClose={closeModal}
                    onSwitchToRegister={() => setActiveModal("register")}
                    onSwitchToForgotPassword={() => setActiveModal("forgot-password")}
                />
            )}

            {activeModal === "register" && (
                <Register
                    onClose={closeModal}
                    onSwitchToLogin={() => setActiveModal("login")}
                    onSuccess={(email) => {
                        setModalData(prev => ({
                            ...prev,
                            email
                        }));
                        setActiveModal("verify-email");
                    }}
                />
            )}

            {activeModal === "verify-email" && (
                <VerifyEmail
                    email={modalData.email}
                    onClose={closeModal}
                    onSuccessSwitchToLogin={() => setActiveModal("login")}
                />
            )}

            {activeModal === "forgot-password" && (
                <ForgotPassword
                    onClose={closeModal}
                    onSwitchToLogin={() => setActiveModal("login")}
                    onCodeSent={(email) => {
                        setModalData(prev => ({
                            ...prev,
                            email
                        }));
                        setActiveModal("verify-otp");
                    }}
                />
            )}

            {activeModal === "verify-otp" && (
                <VerifyResetOtp
                    email={modalData.email}
                    onClose={closeModal}
                    onSwitchToForgotPassword={() =>
                        setActiveModal("forgot-password")
                    }
                    onVerified={(resetToken) => {
                        setModalData(prev => ({
                            ...prev,
                            resetToken
                        }));
                        setActiveModal("reset-password");
                    }}
                />
            )}

            {activeModal === "reset-password" && (
                <ResetPassword
                    resetToken={modalData.resetToken}
                    onClose={closeModal}
                    onSuccessSwitchToLogin={() => {
                        closeModal();
                        setActiveModal("login");
                    }}
                />
            )}

        </Modal>
    );
}

export default AuthModalManager;