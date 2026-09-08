import { useState } from "react";
import { Outlet } from "react-router-dom";

import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import Modal from "../components/common/Modal";

import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";

function PublicLayout() {
  const [activeModal, setActiveModal] = useState(null);

  const closeModal = () => setActiveModal(null);

  return (
    <div className="public-layout">
      <Navbar
        onOpenLogin={() => setActiveModal("login")}
        onOpenRegister={() => setActiveModal("register")}
      />

      <main className="public-content">
        <Outlet
          context={{
            openLogin: () => setActiveModal("login"),
            openRegister: () => setActiveModal("register"),
          }}
        />
      </main>

      <Footer />

      {/* Login Modal */}
      <Modal
        isOpen={activeModal === "login"}
        onClose={closeModal}
      >
        <Login
          onClose={closeModal}
          onSwitchToRegister={() => setActiveModal("register")}
        />
      </Modal>

      {/* Register Modal */}
      <Modal
        isOpen={activeModal === "register"}
        onClose={closeModal}
      >
        <Register
          onClose={closeModal}
          onSwitchToLogin={() => setActiveModal("login")}
        />
      </Modal>
    </div>
  );
}

export default PublicLayout;