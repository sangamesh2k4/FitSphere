import { useEffect, useState } from "react";
import adminService from "../../../services/adminService";
import Select from "../../common/CustomSelect";
import "./ExerciseForm.css";

const initialForm = {
    name: "",
    category: "",
    primaryMuscle: "",
    secondaryMuscles: [],
    movementPattern: "",
    equipment: "",
    exerciseType: "",
    difficulty: "",
    description: "",
    instructions: [],
    tips: [],
    commonMistakes: [],
    imageUrl: ""
};

const toLabel = (value) =>
    value
        .toLowerCase()
        .replace(/_/g, " ")
        .replace(/\b\w/g, (char) => char.toUpperCase());

const categoryOptions = [
    "CHEST", "BACK", "SHOULDERS", "BICEPS", "TRICEPS", 
    "LEGS", "CORE", "CARDIO", "FUNCTIONAL", "FULL_BODY"
].map((value) => ({ value, label: toLabel(value) }));

const movementPatternOptions = [
    "HORIZONTAL_PUSH", "VERTICAL_PUSH", "HORIZONTAL_PULL", "VERTICAL_PULL",
    "SHOULDER_ABDUCTION", "SHOULDER_FLEXION", "SHOULDER_EXTENSION", "HIP_HINGE",
    "SQUAT", "LUNGE", "ELBOW_FLEXION", "ELBOW_EXTENSION", "KNEE_FLEXION",
    "KNEE_EXTENSION", "ANKLE_PLANTARFLEXION", "ANKLE_DORSIFLEXION",
    "CORE_FLEXION", "CORE_ROTATION", "ANTI_ROTATION", "CARRY"
].map((value) => ({ value, label: toLabel(value) }));

const equipmentOptions = [
    "BARBELL", "DUMBBELL", "CABLE", "MACHINE", "BODYWEIGHT",
    "SMITH_MACHINE", "EZ_BAR", "KETTLEBELL", "MEDICINE_BALL",
    "TRX", "RESISTANCE_BAND", "WEIGHT_PLATE", "AB_WHEEL",
    "SLED", "BATTLE_ROPE", "SANDBAG", "LANDMINE"
].map((value) => ({ value, label: toLabel(value) }));

const exerciseTypeOptions = ["COMPOUND", "ISOLATION"].map((value) => ({
    value,
    label: toLabel(value)
}));

const difficultyOptions = ["BEGINNER", "INTERMEDIATE", "ADVANCED"].map((value) => ({
    value,
    label: toLabel(value)
}));

function DynamicListField({ label, items, onAdd, onChange, onRemove }) {
    return (
        <div className="dynamic-list-group">
            <div className="dynamic-list-header">
                <label className="input-label">{label}</label>
                <button type="button" className="btn-add-outline" onClick={onAdd}>
                    + Add
                </button>
            </div>

            {items.length === 0 ? (
                <p className="empty-list-text">No {label.toLowerCase()} added yet.</p>
            ) : (
                <div className="dynamic-list-items">
                    {items.map((item, index) => (
                        <div className="dynamic-list-row" key={index}>
                            <span className="step-number">{index + 1}.</span>
                            <input
                                type="text"
                                className="form-input"
                                value={item}
                                onChange={(e) => onChange(index, e.target.value)}
                                placeholder="Step description..."
                            />
                            <button 
                                type="button" 
                                className="btn-remove-icon" 
                                onClick={() => onRemove(index)}
                                title="Remove item"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <polyline points="3 6 5 6 21 6"></polyline>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                    <line x1="10" y1="11" x2="10" y2="17"></line>
                                    <line x1="14" y1="11" x2="14" y2="17"></line>
                                </svg>
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

function ExerciseForm({ mode = "add", exercise = null, onClose, onSuccess }) {
    const [form, setForm] = useState(initialForm);
    const [primaryMuscleOptions, setPrimaryMuscleOptions] = useState([]);
    const [musclesLoading, setMusclesLoading] = useState(false);

    const isEdit = mode === "edit";

    useEffect(() => {
        if (!isEdit || !exercise) {
            return;
        }

        setForm({
            name: exercise.name ?? "",
            category: exercise.category ?? "",
            primaryMuscle: exercise.primaryMuscle ?? "",
            secondaryMuscles: exercise.secondaryMuscles ?? [],
            movementPattern: exercise.movementPattern ?? "",
            equipment: exercise.equipment ?? "",
            exerciseType: exercise.exerciseType ?? "",
            difficulty: exercise.difficulty ?? "",
            description: exercise.description ?? "",
            instructions: exercise.instructions ?? [],
            tips: exercise.tips ?? [],
            commonMistakes: exercise.commonMistakes ?? [],
            imageUrl: exercise.imageUrl ?? ""
        });
    }, [isEdit, exercise]);

    useEffect(() => {
        const fetchPrimaryMuscles = async () => {
            if (!form.category) {
                setPrimaryMuscleOptions([]);
                return;
            }

            try {
                setMusclesLoading(true);
                const data = await adminService.getPrimaryMusclesByCategory(form.category);
                setPrimaryMuscleOptions(
                    data.muscles.map((muscle) => ({
                        value: muscle,
                        label: toLabel(muscle)
                    }))
                );
            } catch (error) {
                console.error("Failed to load primary muscles:", error);
                console.error("Response:", error.response?.data);
                setPrimaryMuscleOptions([]);
            } finally {
                setMusclesLoading(false);
            }
        };

        fetchPrimaryMuscles();
    }, [form.category]);

    const handleChange = (name, value) => {
        setForm((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    const handleCategoryChange = (value) => {
        setForm((prev) => ({
            ...prev,
            category: value,
            primaryMuscle: "",
            secondaryMuscles: []
        }));
    };

    const handlePrimaryMuscleChange = (value) => {
        setForm((prev) => ({
            ...prev,
            primaryMuscle: value,
            secondaryMuscles: prev.secondaryMuscles.filter((muscle) => muscle !== value)
        }));
    };

    const toggleSecondaryMuscle = (muscle) => {
        setForm((prev) => {
            const exists = prev.secondaryMuscles.includes(muscle);

            return {
                ...prev,
                secondaryMuscles: exists
                    ? prev.secondaryMuscles.filter((item) => item !== muscle)
                    : [...prev.secondaryMuscles, muscle]
            };
        });
    };

    const addListItem = (field) => {
        setForm((prev) => ({
            ...prev,
            [field]: [...prev[field], ""]
        }));
    };

    const updateListItem = (field, index, value) => {
        setForm((prev) => ({
            ...prev,
            [field]: prev[field].map((item, i) => (i === index ? value : item))
        }));
    };

    const removeListItem = (field, index) => {
        setForm((prev) => ({
            ...prev,
            [field]: prev[field].filter((_, i) => i !== index)
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const payload = {
            ...form,
            instructions: form.instructions.filter((item) => item.trim() !== ""),
            tips: form.tips.filter((item) => item.trim() !== ""),
            commonMistakes: form.commonMistakes.filter((item) => item.trim() !== "")
        };

        try {
            let savedExercise;
            if (isEdit) {
                savedExercise = await adminService.updateExercise(exercise.id, payload);
            } else {
                savedExercise = await adminService.addExercise(payload);
            }
            onSuccess?.(savedExercise);
            onClose?.();
        } catch  {
            // Handled silently or custom UI notification logic
        }
    };

    return (
        <div className="exercise-form-wrapper">
            <h1 className="page-title">{isEdit ? "Edit Exercise" : "Add Exercise"}</h1>
            
            <form className="exercise-form" onSubmit={handleSubmit}>
                
                {/* SECTION 1: BASICS */}
                <div className="form-section-card">
                    <h3 className="section-label">BASICS</h3>
                    
                    <div className="form-group">
                        <label className="input-label">Exercise Name</label>
                        <input
                            className="form-input"
                            type="text"
                            value={form.name}
                            onChange={(e) => handleChange("name", e.target.value)}
                            placeholder="Enter exercise name"
                            required
                        />
                    </div>

                    <div className="form-row-2">
                        <div className="form-group-options">
                            <label className="input-label">Category</label>
                            <Select
                                className="form-select"
                                value={form.category}
                                options={[
                                    { value: "", label: "Select Category" },
                                    ...categoryOptions
                                ]}
                                onChange={(event) => handleCategoryChange(event.target.value)}
                                placeholder="Select Category"
                            />
                        </div>
                        <div className="form-group-options">
                            <label className="input-label">Primary Muscle</label>
                            <Select
                                className="form-select"
                                value={form.primaryMuscle}
                                options={[
                                    {
                                        value: "",
                                        label: musclesLoading ? "Loading Muscles..." : "Select Primary Muscle"
                                    },
                                    ...primaryMuscleOptions
                                ]}
                                onChange={(event) => handlePrimaryMuscleChange(event.target.value)}
                                placeholder="Select Primary Muscle"
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="input-label">Secondary Muscles</label>
                        {!form.category ? (
                            <div className="secondary-muscles-empty-box">
                                Select a category first
                            </div>
                        ) : (
                            <div className="secondary-muscles-grid">
                                {primaryMuscleOptions
                                    .filter((muscle) => muscle.value !== form.primaryMuscle)
                                    .map((muscle) => (
                                        <div key={muscle.value} className="muscle-checkbox-label">
    <input
        type="checkbox"
        checked={form.secondaryMuscles.includes(muscle.value)}
        onChange={() => toggleSecondaryMuscle(muscle.value)}
    />
    <span>{muscle.label}</span>
</div>
                                    ))}
                            </div>
                        )}
                    </div>
                </div>

                {/* SECTION 2: CLASSIFICATION */}
                <div className="form-section-card">
                    <h3 className="section-label">CLASSIFICATION</h3>
                    
                    <div className="form-row-2">
                        <div className="form-group-options">
                            <label className="input-label">Movement Pattern</label>
                            <Select
                                className="form-select"
                                value={form.movementPattern}
                                options={[
                                    { value: "", label: "Select Movement Pattern" },
                                    ...movementPatternOptions
                                ]}
                                onChange={(event) => handleChange("movementPattern", event.target.value)}
                                placeholder="Select Movement Pattern"
                            />
                        </div>
                        <div className="form-group-options">
                            <label className="input-label">Equipment</label>
                            <Select
                                className="form-select"
                                value={form.equipment}
                                options={[
                                    { value: "", label: "Select Equipment" },
                                    ...equipmentOptions
                                ]}
                                onChange={(event) => handleChange("equipment", event.target.value)}
                                placeholder="Select Equipment"
                            />
                        </div>
                    </div>

                    <div className="form-row-2">
                        <div className="form-group-options">
                            <label className="input-label">Exercise Type</label>
                            <Select
                                className="form-select"
                                value={form.exerciseType}
                                options={[
                                    { value: "", label: "Select Exercise Type" },
                                    ...exerciseTypeOptions
                                ]}
                                onChange={(event) => handleChange("exerciseType", event.target.value)}
                                placeholder="Select Exercise Type"
                            />
                        </div>
                        <div className="form-group-options">
                            <label className="input-label">Difficulty</label>
                            <Select
                                className="form-select"
                                value={form.difficulty}
                                options={[
                                    { value: "", label: "Select Difficulty" },
                                    ...difficultyOptions
                                ]}
                                onChange={(event) => handleChange("difficulty", event.target.value)}
                                placeholder="Select Difficulty"
                            />
                        </div>
                    </div>
                </div>

                {/* SECTION 3: CONTENT */}
                <div className="form-section-card">
                    <h3 className="section-label">CONTENT</h3>
                    
                    <div className="form-group">
                        <label className="input-label">Description</label>
                        <textarea
                            className="form-textarea"
                            value={form.description}
                            onChange={(e) => handleChange("description", e.target.value)}
                            placeholder="Describe the exercise"
                            rows={3}
                        />
                    </div>

                    <DynamicListField
                        label="Instructions"
                        items={form.instructions}
                        onAdd={() => addListItem("instructions")}
                        onChange={(index, value) => updateListItem("instructions", index, value)}
                        onRemove={(index) => removeListItem("instructions", index)}
                    />

                    <DynamicListField
                        label="Tips"
                        items={form.tips}
                        onAdd={() => addListItem("tips")}
                        onChange={(index, value) => updateListItem("tips", index, value)}
                        onRemove={(index) => removeListItem("tips", index)}
                    />

                    <DynamicListField
                        label="Common Mistakes"
                        items={form.commonMistakes}
                        onAdd={() => addListItem("commonMistakes")}
                        onChange={(index, value) => updateListItem("commonMistakes", index, value)}
                        onRemove={(index) => removeListItem("commonMistakes", index)}
                    />
                </div>

                {/* SECTION 4: IMAGE */}
                <div className="form-section-card">
                    <h3 className="section-label">IMAGE</h3>
                    <div className="form-group-no-margin">
                        <input
                            className="form-input"
                            type="url"
                            value={form.imageUrl}
                            onChange={(e) => handleChange("imageUrl", e.target.value)}
                            placeholder="https://..."
                        />
                    </div>
                </div>

                {/* ACTIONS */}
                <div className="form-actions-footer">
                    <button type="button" className="btn-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button type="submit" className="btn-submit-primary">
                        Save Exercise
                    </button>
                    
                    {isEdit && (
                        <button
                            type="button"
                            className={`btn-toggle-status ${exercise.active ? "disable" : "enable"}`}
                            onClick={async () => {
                                try {
                                    if (exercise.active) {
                                        await adminService.disableExercise(exercise.id);
                                    } else {
                                        await adminService.enableExercise(exercise.id);
                                    }
                                    onSuccess?.();
                                } catch{
                                    // Handle error
                                }
                            }}
                        >
                            {exercise.active ? "Disable Exercise" : "Enable Exercise"}
                        </button>
                    )}
                </div>
            </form>
        </div>
    );
}

export default ExerciseForm;