import { CheckCircle2 } from "lucide-react";
import "./RecommendationCard.css";

export default function RecommendationCard({ result }) {
    const recommendations = result.recommendations || [];
    if (recommendations.length === 0) return null;

    return (
        <div className="health-card recommendation-card">
            <CheckCircle2 size={18} className="recommendation-icon" /> {/*[cite: 1] */}
            <div className="recommendation-text">
                <strong>{recommendations[0]}</strong> {/* Just prioritizing the top recommendation to match design */}
            </div>
        </div>
    );
}