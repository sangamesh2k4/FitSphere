import { useState } from "react";
import { healthService } from "../../../services/healthService";
import CustomSelect  from "../../common/CustomSelect";

import "./HealthAssessmentForm.css";

    const genderOptions = [
    { value: "MALE", label: "Male" },
    { value: "FEMALE", label: "Female" }
];

const activityOptions = [
    { value: "SEDENTARY", label: "Sedentary" },
    { value: "LIGHTLY_ACTIVE", label: "Lightly Active" },
    { value: "MODERATELY_ACTIVE", label: "Moderately Active" },
    { value: "VERY_ACTIVE", label: "Very Active" },
    { value: "EXTRA_ACTIVE", label: "Extra Active" },
    { value: "ATHLETE", label: "Athlete" }
];

const goalOptions = [
    { value: "FAT_LOSS", label: "Fat Loss" },
    { value: "MAINTAIN", label: "Maintain" },
    { value: "MUSCLE_GAIN", label: "Muscle Gain" }
];

function HealthAssessmentForm({ onSuccess }) {

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");


    const [form, setForm] = useState({
        age: "",
        gender: "",
        height: "",
        weight: "",
        activityLevel: "",
        goal: ""
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(previous => ({
            ...previous,
            [name]: value
        }));
    };

const handleSubmit = async (e) => {
    e.preventDefault();

    // Clear previous result and error
    onSuccess(null);
    setError("");

    // Validate custom dropdowns
   if (!form.gender || !form.activityLevel || !form.goal) {
    setError("Please select Gender, Activity Level, and Goal.");
    return;
}

    try {
        setLoading(true);

        const response = await healthService.assessHealth(form);

        onSuccess(response);

    } catch (error) {
        console.error("Health assessment failed:", error);

        setError(
            "Unable to analyze health. Please try again."
        );

    } finally {
        setLoading(false);
    }
};

    return (
        <form 
        className="health-form"
        onSubmit={handleSubmit}>

            <div className="form-group">
                <label htmlFor="age">Age</label>
                <input
                    id="age"
                    name="age"
                    type="number"
                    min="10"
                    max="120"
                    value={form.age}
                    onChange={handleChange}
                    disabled={loading}
                    required
                />
            </div>

<div className="form-group">
    <label>Gender</label>

    <CustomSelect
        name="gender"
        value={form.gender}
        onChange={handleChange}
        options={genderOptions}
        placeholder="Select Gender"
    />
</div>

            <div className="form-group">
                <label htmlFor="height">Height (cm)</label>
                <input
                    id="height"
                    name="height"
                    type="number"
                    min="50"
                    max="250"
                    value={form.height}
                    onChange={handleChange}
                    disabled={loading}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="weight">Weight (kg)</label>
                <input
                    id="weight"
                    name="weight"
                    type="number"
                    min="20"
                    max="400"
                    step="0.1"
                    value={form.weight}
                    onChange={handleChange}
                    disabled={loading}
                    required
                />
            </div>

<div className="form-group">
    <label>Activity Level</label>

    <CustomSelect
        name="activityLevel"
        value={form.activityLevel}
        onChange={handleChange}
        options={activityOptions}
        placeholder="Select Activity Level"
    />
</div>
<div className="form-group">
    <label>Goal</label>

    <CustomSelect
        name="goal"
        value={form.goal}
        onChange={handleChange}
        options={goalOptions}
        placeholder="Select Goal"
    />
</div>

            {error && (
                <p className="error-message">
                    {error}
                </p>
            )}

            <button type="submit" disabled={loading}>
                {loading ? "Analyzing..." : "Analyze Health"}
            </button>

        </form>
    );
}

export default HealthAssessmentForm;