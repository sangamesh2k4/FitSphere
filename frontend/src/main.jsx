import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import "react-toastify/dist/ReactToastify.css";

import "./index.css";
import App from "./App";

import { AuthProvider } from "./context/AuthContext";
import { FavoritesProvider } from "./context/FavoritesContext";
import { AuthModalProvider } from "./context/AuthModalContext";
import { WorkoutProvider } from "./context/WorkoutContext";
import ScrollToHash from "./components/common/ScrollToHash";
import { ThemeProvider } from "./context/ThemeContext";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
     <WorkoutProvider>
    <ThemeProvider>
     <ScrollToHash />
      <AuthProvider>
        <AuthModalProvider>
        <FavoritesProvider>
          <App />
        </FavoritesProvider>
        </AuthModalProvider>
      </AuthProvider>
      </ThemeProvider>
      </WorkoutProvider>
    </BrowserRouter>
  </StrictMode>
);