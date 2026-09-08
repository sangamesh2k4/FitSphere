import "./MetricCard.css";

const MetricCard = ({ title, value, unit, subtitle }) => {
    return (
        <div className="metric-card">
            <span className="metric-title">{title}</span>
            
            <div className="metric-value">
                <span>{value}</span>
                {unit && <span className="metric-unit">{unit}</span>}
            </div>

            {subtitle && (
                <p className="metric-subtitle">{subtitle}</p>
            )}
        </div>
    );
};

export default MetricCard;