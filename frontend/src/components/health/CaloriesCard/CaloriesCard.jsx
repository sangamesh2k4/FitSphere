import { formatCalories } from "../../../utils/formatters";
import "./CaloriesCard.css";

export default function CaloriesCard({ result }) {

    return (
        <div className="health-card calories-card">
            <div className="calories-header">
                <span className="card-mini-title">CALORIES</span>
                <span className="calories-main-value">
                    {formatCalories(result.recommendedCalories)}
                    <span className="calories-unit"> kcal/day recommended</span> {/*[cite: 1] */}
                </span>
            </div>

            <div className="calories-sub-metrics">
                <span>BMR <strong>{formatCalories(result.bmr)}</strong></span>
                <span>TDEE <strong>{formatCalories(result.tdee)}</strong></span>
            </div>
        </div>
    );
}