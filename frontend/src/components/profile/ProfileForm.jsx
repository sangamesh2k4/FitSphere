import { useEffect, useState } from "react";
import SelectionCard from "../common/SelectionCard";
import {
    GENDER_OPTIONS,
    ACTIVITY_OPTIONS,
    GOAL_OPTIONS,
} from "../../constants/profileOptions";
import "./ProfileForm.css";

const getInitialForm = (profile) => ({
    age: profile?.age ?? "",
    gender: profile?.gender ?? "",
    height: profile?.height ?? "",
    weight: profile?.weight ?? "",
    activityLevel: profile?.activityLevel ?? "",
    goal: profile?.goal ?? "",
});

const ProfileForm = ({
    mode = "create",
    initialValues = null,
    onSubmit,
    onCancel,
}) => {

    const [formData, setFormData] = useState(
        getInitialForm(initialValues)
    );

    useEffect(() => {
        setFormData(getInitialForm(initialValues));
    }, [initialValues]);

    const handleChange = (e) => {
        const { name, value } = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSelect = (field, value) => {
        setFormData((prev) => ({
            ...prev,
            [field]: value,
        }));
    };

    const handleFormSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <form
            className="profile-form"
            onSubmit={handleFormSubmit}
        >
            <h2>
                {mode === "edit"
                    ? "Edit Health Profile"
                    : "Create Your Health Profile"}
            </h2>

            <p className="form-subtitle">
                {mode === "edit"
                    ? "Update your health information to keep your recommendations accurate."
                    : "Complete your profile to receive personalized health analysis and nutrition targets."}
            </p>

            {/* Personal Information */}

            <section className="form-section">

                <h3>Personal Information</h3>

                <div className="input-grid">

                    <div className="form-group">
                        <label>Age</label>

                        <input
                            type="number"
                            name="age"
                            value={formData.age}
                            onChange={handleChange}
                            min="10"
                            max="100"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Height (cm)</label>

                        <input
                            type="number"
                            name="height"
                            value={formData.height}
                            onChange={handleChange}
                            min="100"
                            max="250"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Weight (kg)</label>

                        <input
                            type="number"
                            name="weight"
                            value={formData.weight}
                            onChange={handleChange}
                            min="20"
                            max="300"
                            required
                        />
                    </div>

                </div>

            </section>

            {/* Gender */}

            <section className="form-section">

                <h3>Gender</h3>

                <div className="selection-grid two-column">

                    {GENDER_OPTIONS.map((option) => (
                        <SelectionCard
                            key={option.value}
                            title={option.title}
                            selected={formData.gender === option.value}
                            onClick={() =>
                                handleSelect("gender", option.value)
                            }
                        />
                    ))}

                </div>

            </section>

            {/* Activity Level */}

            <section className="form-section">

                <h3>Activity Level</h3>

                <div className="selection-grid">

                    {ACTIVITY_OPTIONS.map((option) => (
                        <SelectionCard
                            key={option.value}
                            title={option.title}
                            description={option.description}
                            selected={
                                formData.activityLevel === option.value
                            }
                            onClick={() =>
                                handleSelect(
                                    "activityLevel",
                                    option.value
                                )
                            }
                        />
                    ))}

                </div>

            </section>

            {/* Goal */}

            <section className="form-section">

                <h3>Fitness Goal</h3>

                <div className="selection-grid">

                    {GOAL_OPTIONS.map((option) => (
                        <SelectionCard
                            key={option.value}
                            title={option.title}
                            description={option.description}
                            selected={formData.goal === option.value}
                            onClick={() =>
                                handleSelect("goal", option.value)
                            }
                        />
                    ))}

                </div>

            </section>

            {/* Buttons */}

            <div className="form-actions">

                <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={onCancel}
                >
                    Cancel
                </button>

                <button
                    type="submit"
                    className="btn btn-primary"
                >
                    {mode === "edit"
                        ? "Update Profile"
                        : "Create Profile"}
                </button>

            </div>

        </form>
    );
};

export default ProfileForm;