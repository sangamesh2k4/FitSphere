import { useState } from "react";
import HealthAssessmentForm from "../components/health/HealthAssessmentForm/HealthAssessmentForm";
import HealthOverview from "../components/health/HealthOverview/HealthOverview";
import "../styles/health.css";

function HealthAssessment() {
    const [assessmentResult, setAssessmentResult] = useState(null);
    
    return (
        <main className="health-assessment">
            {/* Redesigned Integrated Header */}
            <div className="health-header">
                
                <h1 className="health-title">HEALTH ASSESSMENT</h1>
                <p className="health-subtitle">
                    Calculate your body composition, calorie needs, daily macros, hydration, and personalized health recommendations.
                </p>
            </div>

            <HealthAssessmentForm
                onSuccess={setAssessmentResult}
            />

            {assessmentResult && (
                <HealthOverview
                    result={assessmentResult}
                />
            )}
        </main>
    );
}

export default HealthAssessment;