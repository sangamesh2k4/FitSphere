import { Link } from "react-router-dom";

function PublicTools() {
    return (
        <section className="public-tools" id="tools">

            <div className="tools-heading">
                <p className="section-label">
                    NO ACCOUNT NEEDED
                </p>

                <h2>
                    TOOLS YOU CAN
                    <br />
                    USE RIGHT NOW.
                </h2>
            </div>

            <div className="tools-grid">

                {/* HEALTH ASSESSMENT */}
                <article className="tool-card">

                    <p className="tool-number">01</p>

                    <div>
                        <h3>HEALTH ASSESSMENT</h3>

                        <p>
                            Analyze your body composition, calorie needs,
                            daily macros, hydration, and receive personalized
                            health recommendations.
                        </p>
                    </div>

                    <Link
                        to="/health-assessment"
                        className="tool-action"
                    >
                        Analyze Health
                    </Link>

                </article>


                {/* MACRO TRACKER */}
                <article className="tool-card">

                    <p className="tool-number">02</p>

                    <div>
                        <h3>MACRO TRACKER</h3>

                        <p>
                            Search foods using USDA nutrition data, choose
                            your serving size, and calculate calories,
                            protein, carbs, fats, and key nutrients.
                        </p>
                    </div>

                    <Link
                        to="/macro-tracker"
                        className="tool-action"
                    >
                        Track Macros
                    </Link>

                </article>


                {/* EXERCISE LIBRARY */}
                <article className="tool-card">

                    <p className="tool-number">03</p>

                    <div>
                        <h3>EXERCISE LIBRARY</h3>

                        <p>
                            Explore exercises by muscle group, equipment,
                            and difficulty to discover movements for
                            smarter workouts.
                        </p>
                    </div>

                    <Link
                        to="/exercises"
                        className="tool-action"
                    >
                        Explore Exercises
                    </Link>

                </article>

            </div>

        </section>
    );
}

export default PublicTools;