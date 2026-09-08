import "./LatestMeasurements.css";
import MeasurementCard from "../MeasurementCard/MeasurementCard";

function LatestMeasurements({ latest }) {

    const hasLatestData = latest && Object.values(latest).some(
        (value) => value !== null && value !== undefined
    );

    return (
        <section className="measurements-section">

            <div className="section-header">
                <span>LATEST</span>
            </div>

            <div className="latest-grid">

                <MeasurementCard
                    label="Weight"
                    value={hasLatestData ? latest?.weight : null}
                    unit="kg"
                />

                <MeasurementCard
                    label="Body Fat"
                    value={hasLatestData ? latest?.bodyFatPercentage : null}
                    unit="%"
                />

                <MeasurementCard
                    label="Chest"
                    value={hasLatestData ? latest?.chest : null}
                    unit="cm"
                />

                <MeasurementCard
                    label="Waist"
                    value={hasLatestData ? latest?.waist : null}
                    unit="cm"
                />

                <MeasurementCard
                    label="Arms"
                    value={
                        hasLatestData
                            ? `${latest?.leftArm ?? "-"} / ${latest?.rightArm ?? "-"}`
                            : null
                    }
                    unit="cm"
                />

                <MeasurementCard
                    label="Thighs"
                    value={
                        hasLatestData
                            ? `${latest?.leftThigh ?? "-"} / ${latest?.rightThigh ?? "-"}`
                            : null
                    }
                    unit="cm"
                />

            </div>

        </section>
    );
}

export default LatestMeasurements;