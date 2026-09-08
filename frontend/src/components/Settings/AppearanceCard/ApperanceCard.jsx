import { Sun, Moon } from "lucide-react";
import { useTheme } from "../../../context/ThemeContext";
import "../AppearanceCard/AppearanceCard.css";

function AppearanceCard() {
    const { theme, setTheme } = useTheme();

    return (
        <div className="appearance-card">
            
            <div className="appearance-header">
                <h2>Appearance</h2>
            </div>

            <div className="appearance-content">
                <div className="appearance-options">
                    
                    <button
                        className={`theme-option ${theme === "light" ? "active" : ""}`}
                        onClick={() => setTheme("light")}
                        type="button"
                    >
                        <Sun size={20} />
                        <span>Light</span>
                    </button>

                    <button
                        className={`theme-option ${theme === "dark" ? "active" : ""}`}
                        onClick={() => setTheme("dark")}
                        type="button"
                    >
                        <Moon size={20} />
                        <span>Dark</span>
                    </button>

                </div>
            </div>
            
        </div>
    );
}

export default AppearanceCard;