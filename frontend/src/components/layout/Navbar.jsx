import { useState } from "react";
import { Link } from "react-router-dom";
import {
  ChevronDown,
  HeartPulse,
  Apple,
  Dumbbell,
  Menu,
  X
} from "lucide-react";

import "../../styles/Navbar.css";
import "../../styles/Button.css";

import { useAuthModal } from "../../context/AuthModalContext";

function Navbar() {

  const { openLogin, openRegister } = useAuthModal();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [mobileToolsOpen, setMobileToolsOpen] = useState(false);

  const closeMobileMenu = () => {
    setMobileOpen(false);
    setMobileToolsOpen(false);
  };

  const handleLogin = () => {
    closeMobileMenu();
    openLogin();
  };

  const handleRegister = () => {
    closeMobileMenu();
    openRegister();
  };

  return (
    <nav className="navbar">

      {/* LOGO */}
      <Link
        to="/"
        className="navbar-logo"
        onClick={closeMobileMenu}
      >
        FitSphere
      </Link>


      {/* ================= DESKTOP NAV ================= */}

      <div className="navbar-links">

        <Link to="/">Home</Link>

        <Link to="/#features">
          Features
        </Link>


        {/* TOOLS DROPDOWN */}

        <div className="nav-dropdown">

          <button
            type="button"
            className="nav-dropdown-trigger"
          >
            Tools
            <ChevronDown size={14} />
          </button>


          <div className="nav-dropdown-menu">

            <Link
              to="/health-assessment"
              className="nav-dropdown-item"
            >
              <HeartPulse size={17} />

              <div>
                <strong>Health Assessment</strong>
                <span>Analyze your health metrics</span>
              </div>
            </Link>


            <Link
              to="/macro-tracker"
              className="nav-dropdown-item"
            >
              <Apple size={17} />

              <div>
                <strong>Macro Tracker</strong>
                <span>Calculate food nutrition</span>
              </div>
            </Link>


            <Link
              to="/exercises"
              className="nav-dropdown-item"
            >
              <Dumbbell size={17} />

              <div>
                <strong>Exercise Library</strong>
                <span>Explore exercises and workouts</span>
              </div>
            </Link>


            <div className="nav-dropdown-divider" />


            <Link
              to="/#tools"
              className="nav-view-all"
            >
              View all tools
            </Link>

          </div>

        </div>

        <Link to="/#about">
          About
        </Link>
        <Link to="/contact">
          Contact
        </Link>
        

      </div>


      {/* DESKTOP ACTIONS */}

      <div className="navbar-actions">

        <button
          className="login-button"
          onClick={openLogin}
        >
          Login
        </button>

        <button
          className="primary-button"
          onClick={openRegister}
        >
          Get Started
        </button>

      </div>


      {/* ================= MOBILE BUTTON ================= */}

      <button
        type="button"
        className="mobile-menu-button"
        onClick={() => setMobileOpen(previous => !previous)}
        aria-label="Toggle navigation menu"
        aria-expanded={mobileOpen}
      >
        {mobileOpen ? (
          <X size={23} />
        ) : (
          <Menu size={23} />
        )}
      </button>


      {/* ================= MOBILE MENU ================= */}

      <div
        className={`mobile-menu ${
          mobileOpen ? "mobile-menu-open" : ""
        }`}
      >

        <div className="mobile-menu-links">

          <Link
            to="/"
            onClick={closeMobileMenu}
          >
            Home
          </Link>


          <Link
            to="/#features"
            onClick={closeMobileMenu}
          >
            Features
          </Link>


          {/* MOBILE TOOLS */}

          <button
            type="button"
            className="mobile-tools-trigger"
            onClick={() =>
              setMobileToolsOpen(previous => !previous)
            }
          >
            <span>Tools</span>

            <ChevronDown
              size={16}
              className={
                mobileToolsOpen
                  ? "mobile-tools-arrow-open"
                  : ""
              }
            />
          </button>


          {mobileToolsOpen && (

            <div className="mobile-tools-menu">

              <Link
                to="/health-assessment"
                onClick={closeMobileMenu}
              >
                <HeartPulse size={17} />

                <div>
                  <strong>Health Assessment</strong>
                  <span>Analyze your health metrics</span>
                </div>
              </Link>


              <Link
                to="/macro-tracker"
                onClick={closeMobileMenu}
              >
                <Apple size={17} />

                <div>
                  <strong>Macro Tracker</strong>
                  <span>Calculate food nutrition</span>
                </div>
              </Link>


              <Link
                to="/exercises"
                onClick={closeMobileMenu}
              >
                <Dumbbell size={17} />

                <div>
                  <strong>Exercise Library</strong>
                  <span>Explore exercises and workouts</span>
                </div>
              </Link>

            </div>

          )}


          <Link
            to="/#about"
            onClick={closeMobileMenu}
          >
            About
          </Link>

        </div>


        {/* MOBILE AUTH BUTTONS */}

        <div className="mobile-menu-actions">

          <button
            className="login-button"
            onClick={handleLogin}
          >
            Login
          </button>

          <button
            className="primary-button"
            onClick={handleRegister}
          >
            Get Started
          </button>

        </div>

      </div>

    </nav>
  );
}

export default Navbar;