// src/components/profile/EmptyProfile.jsx

import "./EmptyProfile.css";

import { UserRound } from "lucide-react";

const EmptyProfile = ({ onCreate }) => {
    return (
        <div className="empty-profile">
            <div className="empty-profile__icon">
                <UserRound size={60} />
            </div>

            <h2>Create Your Health Profile</h2>

            <p>
               you gotta drop your stats in your health profile real quick so we can actually cook here.
                Don't leave your goals hanging! Once you’re fully locked in and dialed,
                 you'll instantly unlock your custom-tailored calorie targets, exact macro breakdowns,
                  a clean-cut BMI analysis, and all the elite fitness insights you need to completely crush your era and glow up your routine.
                   Let's get it!
            </p>

            <ul className="empty-profile__features">
                <li>✓ BMI & Body Fat Analysis</li>
                <li>✓ Daily Calorie Goal</li>
                <li>✓ Daily Protein Target</li>
                <li>✓ Macro Recommendations</li>
                <li>✓ Water Intake Target</li>
                <li>✓ Personalized Health Score</li>
            </ul>

            <button
                className="btn btn-primary"
                onClick={onCreate}
            >
                Create Profile
            </button>
        </div>
    );
};

export default EmptyProfile;