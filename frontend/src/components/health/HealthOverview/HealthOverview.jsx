import HealthScoreCard from "../HealthScoreCard/HealthScoreCard";
import BodyCompositionCard from "../BodyCompositionCard/BodyCompositionCard";
import CaloriesCard from "../CaloriesCard/CaloriesCard";
import MacroCard from "../MacroCard/MacroCard";
import HydrationCard from "../HydrationCard/HydrationCard";
import HealthyRangeCard from "../HealthyRangeCard/HealthyRangeCard";
import RecommendationCard from "../RecommendationCard/RecommendationCard";

import "./HealthOverview.css";

function HealthOverview({ result }) {
    if (!result) return null;

    return (
        <section className="health-overview">

            {/* Top Row: Health Score & Body Composition */}
            <div className="health-overview-row top-row">
                <HealthScoreCard result={result} />
                <BodyCompositionCard result={result} />
            </div>

            {/* Middle Block: Calories & Daily Macros */}
            <div className="calories-macro-block">
                <CaloriesCard result={result} />
                <MacroCard result={result} />
            </div>

            {/* Optional Healthy Range section if present */}
            {result.healthyRange && (
                <div className="health-overview-row">
                    <HealthyRangeCard result={result} />
                </div>
            )}

            {/* Bottom Row: Hydration & Recommendations */}
            <div className="health-overview-row bottom-row">
                <HydrationCard result={result} />
                <RecommendationCard result={result} />
            </div>

        </section>
    );
}

export default HealthOverview;