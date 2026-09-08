import  { useContext } from "react";
import { useNavigate } from "react-router-dom";
import "./MacroResult.css";

// Contexts & Hooks
import { useAuth } from "../../../hooks/useAuth"; // Adjust path if needed
import { AuthModalContext } from "../../../context/AuthModalContext"; // Adjust path if needed

import {
    Flame,
    Beef,
    Wheat,
    Droplets,
    Leaf,
    Candy,
    CircleDot
} from "lucide-react";

const format = (value) => {
    if (value === null || value === undefined) {
        return "--";
    }
    return Number(value).toFixed(1);
};

export default function MacroResult({ result }) {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const { openLogin } = useContext(AuthModalContext);

    if (!result) return null;

    const handleViewNutrients = () => {
        if (isAuthenticated) {
            // Assumes result object contains fdcId (e.g., result.fdcId)
            navigate(`/app/macro-tracker/food/${result.fdcId}`);
        } else {
            openLogin();
        }
    };

    return (
        <section className="macro-result">
            <div className="macro-result-header">
                <div>
                    <span>NUTRITION BREAKDOWN</span>
                    <h2>{result.foodName}</h2>
                    <p>
                        {format(result.quantity)} {result.unit}
                    </p>
                </div>
            </div>

            <div className="primary-nutrients">
                <div className="nutrient-card calories-nutrient">
                    <Flame size={20} />
                    <strong>{Math.round(result.calories)}</strong>
                    <span>Calories</span>
                    <small>kcal</small>
                </div>

                <div className="nutrient-card protein-nutrient">
                    <Beef size={20} />
                    <strong>{format(result.protein)}</strong>
                    <span>Protein</span>
                    <small>grams</small>
                </div>

                <div className="nutrient-card carbs-nutrient">
                    <Wheat size={20} />
                    <strong>{format(result.carbohydrates)}</strong>
                    <span>Carbohydrates</span>
                    <small>grams</small>
                </div>

                <div className="nutrient-card fat-nutrient">
                    <Droplets size={20} />
                    <strong>{format(result.fat)}</strong>
                    <span>Fat</span>
                    <small>grams</small>
                </div>
            </div>

            <div className="secondary-nutrients">
                <div className="secondary-nutrient">
                    <Leaf size={16} />
                    <span>Fiber</span>
                    <strong>{format(result.fiber)} g</strong>
                </div>

                <div className="secondary-nutrient">
                    <Candy size={16} />
                    <span>Sugar</span>
                    <strong>{format(result.sugar)} g</strong>
                </div>

                <div className="secondary-nutrient">
                    <CircleDot size={16} />
                    <span>Sodium</span>
                    <strong>{format(result.sodium)} mg</strong>
                </div>

                <div className="secondary-nutrient">
                    <CircleDot size={16} />
                    <span>Potassium</span>
                    <strong>{format(result.potassium)} mg</strong>
                </div>

                <div className="secondary-nutrient">
                    <CircleDot size={16} />
                    <span>Calcium</span>
                    <strong>{format(result.calcium)} mg</strong>
                </div>

                <div className="secondary-nutrient">
                    <CircleDot size={16} />
                    <span>Iron</span>
                    <strong>{format(result.iron)} mg</strong>
                </div>
            </div>

            {/* --- ADDED ACTION BUTTON --- */}
            <div className="macro-result-actions">
                <button 
                    type="button" 
                    className="view-full-nutrients-btn"
                    onClick={handleViewNutrients}
                >
                    <span>View all nutrients</span>
                    <i className="ti ti-arrow-right"></i>
                </button>
            </div>
        </section>
    );
}