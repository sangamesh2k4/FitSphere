import "./NutritionFactsCard.css";

function NutritionFactsCard({ food }) {

    const nutritionFacts = [
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
            label: "Carbohydrates",
            value: food.carbohydrates,
            unit: "g"
        },
        {
            label: "Fat",
            value: food.fat,
            unit: "g"
        },
        {
            label: "Fiber",
            value: food.fiber,
            unit: "g"
        },
        {
            label: "Sugar",
            value: food.sugar,
            unit: "g"
        }
    ];

    return (

        <div className="content-card">

            <h2 className="card-heading">
                Nutrition Facts
            </h2>

            <div className="nutrition-facts-list">

                {nutritionFacts.map((item) => (

                    <div
                        key={item.label}
                        className="nutrition-row"
                    >

                        <span className="nutrition-label">
                            {item.label}
                        </span>

                        <span className="nutrition-value">
                            {item.value ?? "-"} {item.unit}
                        </span>

                    </div>

                ))}

            </div>

        </div>

    );

}

export default NutritionFactsCard;