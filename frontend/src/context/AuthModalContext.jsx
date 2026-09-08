import { createContext, useContext, useMemo, useState } from "react";

export const AuthModalContext = createContext(null);

export const AuthModalProvider = ({ children }) => {

    const [activeModal, setActiveModal] = useState(null);

    const [modalData, setModalData] = useState({
        email: "",
        resetToken: ""
    });

    const openLogin = () => setActiveModal("login");

    const openRegister = () => setActiveModal("register");

    const openForgotPassword = () => setActiveModal("forgot-password");

    const closeModal = () => {
        setActiveModal(null);

        setModalData({
            email: "",
            resetToken: ""
        });
    };

    const value = useMemo(() => ({
        activeModal,
        modalData,
        setModalData,
        setActiveModal,

        openLogin,
        openRegister,
        openForgotPassword,
        closeModal

    }), [activeModal, modalData]);

    return (
        <AuthModalContext.Provider value={value}>
            {children}
        </AuthModalContext.Provider>
    );
};

export const useAuthModal = () => useContext(AuthModalContext);