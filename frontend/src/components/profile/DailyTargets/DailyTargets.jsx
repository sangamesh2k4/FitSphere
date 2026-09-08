import MetricCard from "../MetricCard/MetricCard";
import "./DailyTargets.css";

const DailyTargets = ({ profile }) => {
    const targets = [
        {
            title: "CALORIES",
            value: Math.round(profile.recommendedCalories),
            unit: "kcal",
        },
        {
            title: "PROTEIN",
            value: Math.round(profile.recommendedProtein),
            unit: "g",
        },
        {
            title: "CARBOHYDRATES",
            value: Math.round(profile.recommendedCarbohydrates),
            unit: "g",
        },
        {
            title: "FAT",
            value: Math.round(profile.recommendedFat),
            unit: "g",
        },
        {
            title: "WATER",
            value: Math.round(profile.recommendedWater),
            unit: "ml",
        },
    ];

    return (
        <section className="daily-targets">
            <div className="section-header">
                <h2>Daily Targets</h2>
                <p>Recommended nutrition and hydration goals based on your profile.</p>
            </div>

            <div className="daily-targets-grid">
                {targets.map((target) => (
                    <MetricCard key={target.title} {...target} />
                ))}
            </div>
        </section>
    );
};

export default DailyTargets;