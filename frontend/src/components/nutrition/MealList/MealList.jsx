import MealSection from "../MealSection/MealSection";
import "./MealList.css";

const mealTypes = [
    "BREAKFAST",
    "LUNCH",
    "DINNER",
    "SNACK",
];

export default function MealList({
    foodLogs = [], // Failsafe to prevent .filter() from crashing
    onAddFood,
    onEditFood,
    onDeleteFood
}) {
    return (
        <section className="meal-list">
            
            <div className="meal-list-header">
                <h2>Today's Meals</h2>
            </div>

            {mealTypes.map((mealType) => (
                <MealSection
                    key={mealType}
                    mealType={mealType}
                    foods={foodLogs.filter(
                        (food) => food.mealType === mealType
                    )}
                    onAddFood={onAddFood}
                    onEditFood={onEditFood}
                    onDeleteFood={onDeleteFood}
                />
            ))}

        </section>
    );
}