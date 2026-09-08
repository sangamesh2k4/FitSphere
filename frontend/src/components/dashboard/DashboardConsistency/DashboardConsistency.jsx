import '../DashboardConsistency/DashboardConsistency.css';
function DashboardConsistency({ daysLogged }) {
    const days = Math.max(
        0,
        Math.min(daysLogged ?? 0, 7)
    );

    return (
        <section className="dashboard-consistency">

            <span className="dashboard-consistency-label">
                CONSISTENCY
            </span>

            <div className="dashboard-consistency-content">

                <div className="dashboard-consistency-count">
                    <strong>
                        {days}
                    </strong>

                    <span>
                        / 7 days
                    </span>
                </div>

                <span className="dashboard-consistency-description">
                    Days logged this week
                </span>

            </div>

            <div className="dashboard-consistency-progress">

                <div
                    className="dashboard-consistency-progress-fill"
                    style={{
                        width: `${(days / 7) * 100}%`
                    }}
                />

            </div>

        </section>
    );
}

export default DashboardConsistency;