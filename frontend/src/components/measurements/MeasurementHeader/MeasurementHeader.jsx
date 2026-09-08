import "./MeasurementHeader.css";

function MeasurementHeader({ onLogMeasurement }) {
    return (
        <header className="measurements-header">
         
            <button
                className="log-measurement-button"
                onClick={onLogMeasurement}
            >
                + Log Measurement
            </button>
        </header>
    );
}

export default MeasurementHeader;