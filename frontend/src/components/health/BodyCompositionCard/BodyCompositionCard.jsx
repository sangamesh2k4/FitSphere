
import { formatNumber } from "../../../utils/formatters";
import "./BodyCompositionCard.css";

export default function BodyCompositionCard({ result }) {
    const bmi = Number(result.bmi) || 0;
    const bodyFat = Number(result.bodyFatPercentage) || 0;

    const bmiProgress = Math.min((bmi / 40) * 100, 100);
    const bodyFatProgress = Math.min((bodyFat / 40) * 100, 100);

    return (
        <div className="health-card body-composition-card">
            <div className="card-mini-title">BODY COMPOSITION</div> {/* */}

            <div className="composition-content">
                <div className="composition-metrics-row">
                    
                    {/* BMI Section */}
                    <div className="composition-metric">
                        <div className="composition-row">
                            <span className="composition-label">BMI</span>
                            <span className="composition-value-label">
                                {formatNumber(result.bmi)} · {result.bmiCategory}
                            </span>
                        </div>
                        <div className="metric-progress">
                            <div
                                className="metric-progress-fill"
                                style={{ width: `${bmiProgress}%`, background: 'var(--fs-gold)' }}
                            /> {/* */}
                        </div>
                    </div>

                    {/* Body Fat Section */}
                    <div className="composition-metric">
                        <div className="composition-row">
                            <span className="composition-label">Body Fat</span>
                            <span className="composition-value-label">
                                {formatNumber(result.bodyFatPercentage)}% · {result.bodyFatCategory}
                            </span>
                        </div>
                        <div className="metric-progress">
                            <div
                                className="metric-progress-fill"
                                style={{ width: `${bodyFatProgress}%`, background: 'var(--fs-green)' }}
                            /> {/*[cite: 1] */}
                        </div>
                    </div>
                </div>

            </div>
        </div>
    );
}