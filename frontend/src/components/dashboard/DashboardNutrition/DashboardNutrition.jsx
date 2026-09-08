import { useNavigate } from "react-router-dom";
import { ChevronRight } from "lucide-react";
import { formatNumber } from "../../../utils";

import "./DashboardNutrition.css";

function DashboardNutrition({ analytics }) {
    const navigate = useNavigate();

    const hasNutritionData = analytics.daysLogged > 0;

    return (
        <button
            type="button"
            className="dashboard-nutrition-card"
            onClick={() => navigate("/app/nutrition")}
        >
            <div className="dashboard-nutrition-header">
                <span className="dashboard-nutrition-label">
                    AVERAGE DAILY NUTRITION
                </span>

                <ChevronRight
                    size={20}
                    className="dashboard-nutrition-arrow"
                />
            </div>

            {hasNutritionData ? (
                <>
                    <NutritionRow
                        label="Calories"
                        current={analytics.averageDailyCalories}
                        target={analytics.recommendedCalories}
                        percentage={analytics.calorieTargetPercentage}
                        unit="kcal"
                    />

                    <NutritionRow
                        label="Protein"
                        current={analytics.averageDailyProtein}
                        target={analytics.recommendedProtein}
                        percentage={analytics.proteinTargetPercentage}
                        unit="g"
                    />
                </>
            ) : (
                <div className="dashboard-nutrition-empty">
                    <span>
                        Nothing logged yet this week, 
                    </span>

                    <span>
                        Log your first meal.
                    </span>
                </div>
            )}
        </button>
    );
}


/* =====================================================
   Nutrition Row
===================================================== */

function NutritionRow({
    label,
    current,
    target,
    percentage,
    unit
}) {
    const safePercentage = Math.max(
        0,
        Math.min(percentage ?? 0, 100)
    );

    return (
        <div className="dashboard-nutrition-row">

            <div className="dashboard-nutrition-row-header">

                <span className="dashboard-nutrition-row-label">
                    {label}
                </span>

                <span className="dashboard-nutrition-values">

                    <strong>
                        {formatNumber(current ?? 0)}
                    </strong>

                    <span>
                        {" / "}
                        {formatNumber(target ?? 0)}
                        {" "}
                        {unit}
                    </span>

                </span>

            </div>

            <div className="dashboard-nutrition-progress">

                <div
                    className="dashboard-nutrition-progress-fill"
                    style={{
                        width: `${safePercentage}%`
                    }}
                />

            </div>

            <span className="dashboard-nutrition-percentage">
                {formatNumber(percentage ?? 0)}% of target
            </span>

        </div>
    );
}

export default DashboardNutrition;