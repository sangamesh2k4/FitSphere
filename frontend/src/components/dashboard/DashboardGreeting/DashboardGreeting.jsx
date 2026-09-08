import "./DashboardGreeting.css";

function DashboardGreeting() {
    const hour = new Date().getHours();
    
    let greetingTitle;
    let greetingSubtitle;

    if (hour < 12) {
        greetingTitle = "Good morning!";
        greetingSubtitle = "Time to set the tone for the day. Let's check your stats and hit today's targets.";
    } else if (hour < 17) {
        greetingTitle = "Good afternoon!";
        greetingSubtitle = "Halfway through the day. Keep your momentum going and stay on track.";
    } else {
        greetingTitle = "Good evening!";
        greetingSubtitle = "The day is winding down. Review your training progress and prepare for tomorrow.";
    }

    return (
        <section className="dashboard-greeting">
            <h1>{greetingTitle}</h1>
            <div className="marquee-container">
                <p>{greetingSubtitle}</p>
            </div>
        </section>
    );
}

export default DashboardGreeting;