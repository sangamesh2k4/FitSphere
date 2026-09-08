import { Pencil, Trash2 } from "lucide-react";
import "./FoodLogCard.css";

export default function FoodLogCard({
    food,
    onEdit,
    onDelete
}) {
    if (!food) return null;

    // Format calories to 2 decimal places if it's a floating point number
    const formattedCalories = typeof food.calories === "number"
        ? Number.isInteger(food.calories) ? food.calories : food.calories.toFixed(2)
        : food.calories;

    return (
        <div className="food-card">

            <div className="food-left">
                <h3>{food.foodName}</h3>
                <p>
                    {food.quantity} {food.unit}
                </p>
            </div>

            <div className="food-right">

                <span className="food-calories">
                    {formattedCalories} kcal
                </span>

                <div className="food-actions">
                    <button
                        type="button"
                        className="icon-btn"
                        onClick={() => onEdit(food)}
                        aria-label="Edit food"
                    >
                        <Pencil size={16} />
                    </button>

                    <button
                        type="button"
                        className="icon-btn delete-btn"
                        onClick={() => onDelete(food.id)}
                        aria-label="Delete food"
                    >
                        <Trash2 size={16} />
                    </button>
                </div>

            </div>

        </div>
    );
}