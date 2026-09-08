import "./NutritionSummary.css";

export default function NutritionSummary({ summary }) {

    const safeSummary = summary || {};

    const hasNutritionTargets =
        safeSummary.targetCalories !== null &&
        safeSummary.targetCalories !== undefined &&
        safeSummary.targetProtein !== null &&
        safeSummary.targetProtein !== undefined;

    if (!hasNutritionTargets) {
        return (
            <section className="nutrition-summary nutrition-summary-empty">
                <div className="nutrition-summary-empty-content">
                    <strong>
                        No nutrition data yet
                    </strong>

                    <span>
                        Complete your profile to calculate
                        your personalized calories and macros.
                    </span>
                </div>
            </section>
        );
    }

    const nutritionMetrics = [
        {
            key: "calories",
            label: "Calories",
            consumed: safeSummary.consumedCalories ?? 0,
            target: safeSummary.targetCalories ?? 0,
            unit: "kcal",
        },
        {
            key: "protein",
            label: "Protein",
            consumed: safeSummary.consumedProtein ?? 0,
            target: safeSummary.targetProtein ?? 0,
            unit: "g",
        },
        {
            key: "carbohydrates",
            label: "Carbohydrates",
            consumed: safeSummary.consumedCarbohydrates ?? 0,
            target: safeSummary.targetCarbohydrates ?? 0,
            unit: "g",
        },
        {
            key: "fat",
            label: "Fat",
            consumed: safeSummary.consumedFat ?? 0,
            target: safeSummary.targetFat ?? 0,
            unit: "g",
        },
    ];

    const getProgress = (consumed, target) => {
        if (!target || target <= 0) {
            return 0;
        }

        return Math.min(
            (consumed / target) * 100,
            100
        );
    };

    const formatValue = (val) => {
        if (val === 0) {
            return "0";
        }

        return Number.isInteger(val)
            ? val
            : Number(val).toFixed(2);
    };

    return (
        <section className="nutrition-summary">

            <div className="nutrition-summary-grid">

                {nutritionMetrics.map((metric) => (

                    <div
                        className="macro-item"
                        key={metric.key}
                    >

                        <div className="macro-header">

                            <span className="macro-label">
                                {metric.label}
                            </span>

                            <span className="macro-value">
                                {formatValue(metric.consumed)}
                                {" / "}
                                {formatValue(metric.target)}
                                {" "}
                                {metric.unit}
                            </span>

                        </div>

                        <div className="progress-bar">

                            <div
                                className={`progress-fill fill-${metric.key}`}
                                style={{
                                    width: `${getProgress(
                                        metric.consumed,
                                        metric.target
                                    )}%`
                                }}
                            />

                        </div>

                    </div>

                ))}

            </div>

        </section>
    );
}