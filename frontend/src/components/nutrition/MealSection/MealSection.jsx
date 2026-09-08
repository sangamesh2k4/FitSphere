import FoodLogCard from "../FoodLogCard/FoodLogCard";
import "./MealSection.css";

const mealLabels = {
    BREAKFAST: "Breakfast",
    LUNCH: "Lunch",
    DINNER: "Dinner",
    SNACK: "Snack",
};

export default function MealSection({
    mealType,
    foods = [], // Failsafe to prevent crashes
    onAddFood,
    onEditFood,
    onDeleteFood
}) {
    return (
        <section className="meal-section">
            
            <div className="meal-header">
                <h2>{mealLabels[mealType]}</h2>
                <button
                    className="add-food-btn"
                    onClick={() => onAddFood(mealType)}
                >
                    + Add Food
                </button>
            </div>

            {foods.length === 0 ? (
                <div className="meal-empty">
                    No foods added yet.
                </div>
            ) : (
                <div className="meal-foods-container">
                    {foods.map(food => (
                        <FoodLogCard
                            key={food.id}
                            food={food}
                            onEdit={onEditFood}
                            onDelete={onDeleteFood}
                        />
                    ))}
                </div>
            )}

        </section>
    );
}