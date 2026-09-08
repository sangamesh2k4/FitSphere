import "./MineralsCard.css";

function MineralsCard({ food }) {

    const minerals = [
        {
            label: "Sodium",
            value: food.sodium,
            unit: "mg"
        },
        {
            label: "Potassium",
            value: food.potassium,
            unit: "mg"
        },
        {
            label: "Calcium",
            value: food.calcium,
            unit: "mg"
        },
        {
            label: "Iron",
            value: food.iron,
            unit: "mg"
        }
    ];

    return (

        <div className="content-card">

            <h2 className="card-heading">
                Minerals
            </h2>

            <div className="minerals-list">

                {minerals.map((item) => (

                    <div
                        key={item.label}
                        className="mineral-row"
                    >

                        <span className="mineral-label">
                            {item.label}
                        </span>

                        <span className="mineral-value">
                            {item.value ?? "-"} {item.unit}
                        </span>

                    </div>

                ))}

            </div>

        </div>

    );

}

export default MineralsCard;