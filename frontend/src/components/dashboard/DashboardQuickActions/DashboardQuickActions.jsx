import { useNavigate } from "react-router-dom";
import {
    Dumbbell,
    Utensils,
    Ruler,
    ChevronRight
} from "lucide-react";

import "./DashboardQuickActions.css";

function DashboardQuickActions() {
    const navigate = useNavigate();

    const actions = [
        {
            title: "Log Workout",
            description: "Start a workout",
            icon: Dumbbell,
            path: "/app/workouts"
        },
        {
            title: "Log Food",
            description: "Track your nutrition",
            icon: Utensils,
            path: "/app/nutrition"
        },
        {
            title: "Add Measurement",
            description: "Track your progress",
            icon: Ruler,
            path: "/app/measurements"
        }
    ];

    return (
        <section className="dashboard-quick-actions">

            <h2 className="dashboard-section-title">
                QUICK ACTIONS
            </h2>

            <div className="quick-actions-grid">

                {actions.map((action) => (
                    <button
                        key={action.title}
                        type="button"
                        className="quick-action-card"
                        onClick={() => navigate(action.path)}
                    >
                        <div className="quick-action-top">

                            <div className="quick-action-icon">
                                <action.icon
                                    size={21}
                                    strokeWidth={2}
                                />
                            </div>

                            <ChevronRight
                                className="quick-action-arrow"
                                size={19}
                            />

                        </div>

                        <div className="quick-action-content">

                            <strong>
                                {action.title}
                            </strong>

                            <span>
                                {action.description}
                            </span>

                        </div>

                    </button>
                ))}

            </div>

        </section>
    );
}

export default DashboardQuickActions;