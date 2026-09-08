import {
    Activity,
    Droplets,
    Flame,
    HeartPulse,
    Scale,
    Utensils
} from "lucide-react";

function HeroSection() {
    return (
        <section className="health-hero">

            <div className="health-hero-content">

                <div className="health-hero-label">
                    <HeartPulse size={15} />
                    <span>HEALTH TOOLS</span>
                </div>

                <h1>HEALTH ASSESSMENT</h1>

                <p>
                    Calculate your body composition, calorie needs,
                    daily macros, hydration, and personalized health
                    recommendations.
                </p>

                <div className="health-hero-features">

                    <span>
                        <Scale size={15} />
                        BMI
                    </span>

                    <span>
                        <Flame size={15} />
                        Calories
                    </span>

                    <span>
                        <Utensils size={15} />
                        Macros
                    </span>

                    <span>
                        <Droplets size={15} />
                        Hydration
                    </span>

                    <span>
                        <Activity size={15} />
                        Health Score
                    </span>

                </div>

            </div>

            <div className="health-hero-visual">

                <div className="health-icon-circle">
                    <HeartPulse size={72} strokeWidth={1.5} />
                </div>

                <div className="health-visual-glow"></div>

            </div>

        </section>
    );
}

export default HeroSection;