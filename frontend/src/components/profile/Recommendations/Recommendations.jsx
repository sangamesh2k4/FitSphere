import "./Recommendations.css";

const Recommendations = ({ profile }) => {
    const recommendations = profile.recommendations;

    return (
        <section className="recommendations">
            <div className="section-header">
                <h2>Recommendations</h2>
                <p>Personalized suggestions based on your health profile.</p>
            </div>

            {!recommendations || recommendations.length === 0 ? (
                <div className="recommendations-empty">
                    <h3>No recommendations available</h3>
                    <p>Complete or update your profile to receive personalized health recommendations.</p>
                </div>
            ) : (
                <div className="recommendations-list">
                    {recommendations.map((recommendation, index) => (
                        <div key={index} className="recommendation-item">
                            {recommendation}
                        </div>
                    ))}
                </div>
            )}
        </section>
    );
};

export default Recommendations;