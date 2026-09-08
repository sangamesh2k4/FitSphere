import "./HealthOverview.css";

const HealthOverview = ({ profile }) => {
    const healthScore = profile.healthScore || 100;
    const healthStatus = profile.healthStatus || "Normal Weight";
    const bmi = profile.bmi || 0;
    const bmiCategory = profile.bmiCategory || "Normal Weight";
    const bodyFat = profile.bodyFatPercentage || 0;
    const bodyFatCategory = profile.bodyFatCategory || "Athlete";
    const bmr = Math.round(profile.bmr || 0);
    const tdee = Math.round(profile.tdee || 0);

    return (
        <section className="health-overview">
            <div className="health-overview-header">
            
            </div>

            {/* Top 3 Cards Grid: Dark Hero Card + BMI + Body Fat */}
            <div className="health-overview-grid">
                {/* Hero Health Card (Dark Theme with Progress Ring) */}
                <div className="hero-health-card">
                    <div className="progress-ring-container">
                        <svg className="progress-ring" viewBox="0 0 36 36" width="165" height="165">
                            <path
                                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                                fill="none"
                                stroke="rgba(255, 255, 255, 0.1)"
                                strokeWidth="3.8"
                            />
                            <path
                                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                                fill="none"
                                stroke="#f97316"
                                strokeWidth="3.8"
                                strokeDasharray={`${healthScore}, 100`}
                                strokeLinecap="round"
                            />
                        </svg>
                    </div>
                    <div className="hero-content">
                        <span className="hero-label">HEALTH SCORE</span>
                        <h3 className="hero-value">{healthScore}</h3>
                        <span className="hero-status">{healthStatus}</span>
                    </div>
                </div>

                {/* BMI Card */}
                <div className="metric-card">
                    <span className="metric-title">BMI</span>
                    <div className="metric-value-wrapper">
                        <span className="metric-value">{bmi}</span>
                    </div>
                    <div className="metric-bar-container">
                        <div className="metric-bar green" style={{ width: `${Math.min((bmi / 40) * 100, 100)}%` }}></div>
                    </div>
                </div>

                {/* Body Fat Card */}
                <div className="metric-card">
                    <span className="metric-title">BODY FAT</span>
                    <div className="metric-value-wrapper">
                        <span className="metric-value">{bodyFat}</span>
                        <span className="metric-unit">%</span>
                    </div>
                    <div className="metric-bar-container">
                        <div className="metric-bar green" style={{ width: `${Math.min(bodyFat, 100)}%` }}></div>
                    </div>
                </div>
            </div>

          {/* BMR & TDEE Reference Row */}
<div className="reference-row">
    
    {/* BMR Card */}
    <div className="reference-card">
        <div className="reference-header">
            <span className="reference-label">BMR</span>
            <div className="reference-value">
                {bmr} <span>kcal</span>
            </div>
        </div>
        <p className="reference-description">
            *Calories your body burns at complete rest.
        </p>
    </div>

    {/* TDEE Card */}
    <div className="reference-card">
        <div className="reference-header">
            <span className="reference-label">TDEE</span>
            <div className="reference-value">
                {tdee} <span>kcal</span>
            </div>
        </div>
        <p className="reference-description">
            *Calories you burn in a typical day including activity.
        </p>
    </div>

</div>
        </section>
    );
};

export default HealthOverview;