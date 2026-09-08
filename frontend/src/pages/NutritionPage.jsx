import NutritionSummary from "../components/nutrition/NutritionSummary/NutritionSummary";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { nutritionService } from "../services/nutritionService";
import AddFoodModal from "../components/nutrition/AddFoodModal/AddFoodModal";
import MealList from "../components/nutrition/MealList/MealList";
import NutritionHistory from "../components/nutrition/NutritionHistory/NutritionHistory";
import NutritionSkeleton from "../components/skeleton/NutritionSkeleton";
import "../styles/nutrition/nutrition.css";

export default function NutritionPage() {

    const navigate = useNavigate();

    const [isAddFoodOpen, setIsAddFoodOpen] = useState(false);
    const [selectedMeal, setSelectedMeal] = useState("BREAKFAST");
    const [editingFood, setEditingFood] = useState(null);
    const [foodLogs, setFoodLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [profileIncomplete, setProfileIncomplete] = useState(false);

    const [summary, setSummary] = useState({
        consumedCalories: 0,
        targetCalories: 0,
        consumedProtein: 0,
        targetProtein: 0,
        consumedCarbohydrates: 0,
        targetCarbohydrates: 0,
        consumedFat: 0,
        targetFat: 0,
    });

    // Disable scrolling when any modal (profile overlay or add food modal) is open
    useEffect(() => {
        if (profileIncomplete || isAddFoodOpen) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "auto";
        }

        // Cleanup scroll state on unmount
        return () => {
            document.body.style.overflow = "auto";
        };
    }, [profileIncomplete, isAddFoodOpen]);

    const handleAddFood = (mealType) => {
        setEditingFood(null);
        setSelectedMeal(mealType);
        setIsAddFoodOpen(true);
    };

    const handleEditFood = (food) => {
        setEditingFood(food);
        setSelectedMeal(food.mealType);
        setIsAddFoodOpen(true);
    };

    const handleDeleteFood = async (id) => {

        if (!window.confirm("Are you sure you want to delete this food?")) {
            return;
        }

        try {
            await nutritionService.deleteFoodLog(id);
            await loadNutritionData();
        } catch (error) {
            console.error("Failed to delete food", error);
            setError("Unable to delete food.");
        }
    };

    const loadNutritionData = async () => {

        try {

            setLoading(true);
            setError("");
            setProfileIncomplete(false);

            const [logs, summaryData] = await Promise.all([
                nutritionService.getTodayFoodLogs(),
                nutritionService.getTodaySummary()
            ]);

            setFoodLogs(logs);
            setSummary(summaryData);

        } catch (err) {

            console.error(err);

            if (err.response?.status === 404) {
                setProfileIncomplete(true);
                setFoodLogs([]);
            } else {
                setError("Unable to load nutrition data.");
            }

        } finally {
            setLoading(false);
        }
    };

    const handleFoodSaved = async () => {
        setIsAddFoodOpen(false);
        setEditingFood(null);
        await loadNutritionData();
    };

    useEffect(() => {
        loadNutritionData();
    }, []);

    const handleCompleteProfile = () => {
        navigate("/app/profile");
    };

    const handleMaybeLater = () => {
        setProfileIncomplete(false);
    };

    return (
        <div className="nutrition-page-wrapper">

            {loading ? (
                <NutritionSkeleton />
            ) : (
                <>
                    {error && (
                        <p className="nutrition-error">
                            {error}
                        </p>
                    )}

                    <NutritionSummary summary={summary} />

                    <MealList
                        foodLogs={foodLogs}
                        onAddFood={handleAddFood}
                        onEditFood={handleEditFood}
                        onDeleteFood={handleDeleteFood}
                    />

                    <NutritionHistory />
                </>
            )}

            {isAddFoodOpen && (
                <AddFoodModal
                    selectedMeal={selectedMeal}
                    editingFood={editingFood}
                    onClose={() => {
                        setIsAddFoodOpen(false);
                        setEditingFood(null);
                    }}
                    onFoodSaved={handleFoodSaved}
                />
            )}

            {profileIncomplete && (
                <div className="nutrition-profile-overlay">

                    <div className="nutrition-profile-modal">

                        <div className="nutrition-profile-icon">
                            👤
                        </div>

                        <h2>
                            Complete your profile
                        </h2>

                        <p>
                            Add your basic details so we can
                            calculate your personalized calorie
                            and macro targets.
                        </p>

                        <button
                            type="button"
                            className="nutrition-profile-primary"
                            onClick={handleCompleteProfile}
                        >
                            Complete Profile
                        </button>

                        <button
                            type="button"
                            className="nutrition-profile-secondary"
                            onClick={handleMaybeLater}
                        >
                            Maybe Later
                        </button>

                    </div>

                </div>
            )}
        </div>
    );
}