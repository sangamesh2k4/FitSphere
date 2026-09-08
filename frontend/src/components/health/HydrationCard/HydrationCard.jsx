import { formatLiters } from "../../../utils/formatters";
import "./HydrationCard.css";

export default function HydrationCard({ result }) {
    const glasses = Math.round(result.recommendedWater / 250);

    return (
        <div className="health-card hydration-card">
            <div className="card-mini-title">HYDRATION</div> {/*[cite: 1] */}
            <div className="hydration-value-container">
                <span className="hydration-main-value">{formatLiters(result.recommendedWater)}</span>
                <span className="hydration-unit">/day · ~{glasses} glasses</span> {/*[cite: 1] */}
            </div>
        </div>
    );
}