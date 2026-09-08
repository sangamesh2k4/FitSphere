
import Badge from "../Badge/Badge";
import "./HealthScoreCard.css";

const getScoreStatus = (score) => {
    if (score >= 90) return "Excellent";
    if (score >= 80) return "Very Good";
    if (score >= 70) return "Good";
    if (score >= 60) return "Fair";
    return "Needs Improvement";
};

export default function HealthScoreCard({ result }) {
    const score = result.healthScore ?? 0;
    const status = getScoreStatus(score);

    return (
        <div className="health-card health-score-card">
            <div className="card-mini-title">HEALTH SCORE</div> {/* */}
            
            <div className="score-content">
                <div className="score-ring" style={{ "--score": score }}>
                    <div className="score-ring-inner">
                        <span className="score-number">{score}</span> {/* */}
                    </div>
                </div>
                <Badge label={status} />
            </div>
        </div>
    );
}