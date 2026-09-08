import "./FoodSummaryCard.css";

function FoodSummaryCard({ food }) {
    const summaryItems = [
        {
            label: "Calories",
            value: food.calories,
            unit: "kcal"
        },
        {
            label: "Protein",
            value: food.protein,
            unit: "g"
        },
        {
            label: "Carbs",
            value: food.carbohydrates,
            unit: "g"
        },
        {
            label: "Fat",
            value: food.fat,
            unit: "g"
        }
    ];

    return (
        <div className="food-summary-card">

            <h2 className="food-summary-title">
                Nutrition Summary
            </h2>

            <div className="food-summary-list">

                {summaryItems.map((item) => (
                    <div
                        key={item.label}
                        className="food-summary-item"
                    >
                        <span className="summary-label">
                            {item.label}
                        </span>

                        <div className="summary-value">
                            <span>{item.value ?? "-"}</span>
                            <span className="summary-unit">
                                {item.unit}
                            </span>
                        </div>
                    </div>
                ))}

            </div>

        </div>
    );
}

export default FoodSummaryCard;