import { Scale, ShieldCheck, Activity } from "lucide-react";
import CardHeader from "../CardHeader/CardHeader";
import "./HealthyRangeCard.css";

export default function HealthyRangeCard({ result }) {

    return (
        <div className="health-card healthy-range-card">

            <CardHeader
                icon={ShieldCheck}
                title="Healthy Range"
                variant="emerald"
            />

            <div className="healthy-range-content">

                <div className="range-item">

                    <div className="range-icon weight-range-icon">
                        <Scale size={20} />
                    </div>

                    <div className="range-details">
                        <span className="range-label">
                            Healthy Weight
                        </span>

                        <strong className="range-value">
                            {result.healthyWeightRange}
                        </strong>

                        <span className="range-description">
                            Recommended weight range for your height
                        </span>
                    </div>

                </div>

                <div className="range-divider" />

                <div className="range-item">

                    <div className="range-icon fat-range-icon">
                        <Activity size={20} />
                    </div>

                    <div className="range-details">
                        <span className="range-label">
                            Ideal Body Fat
                        </span>

                        <strong className="range-value">
                            {result.idealBodyFatRange}
                        </strong>

                        <span className="range-description">
                            Recommended body fat range for your profile
                        </span>
                    </div>

                </div>

            </div>

        </div>
    );
}