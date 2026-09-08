import "./MeasurementCard.css"

function MeasurementCard({ label, value, unit }) {
    return (
        <div className="measurement-card">
            <div className="measurement-value">
                {value ?? "-"} <span>{unit}</span>
            </div>

            <div className="measurement-label">
                {label}
            </div>
        </div>
    );
}

export default MeasurementCard;