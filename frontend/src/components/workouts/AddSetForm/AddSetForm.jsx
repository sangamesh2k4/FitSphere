import { useState } from 'react';
import { toast } from 'react-hot-toast';
import "../AddSetForm/AddSetForm.css";

function AddSetForm({ disabled, onAdd }) {

    const [weight, setWeight] = useState(0);
    const [reps, setReps] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();

        const weightValue = Number(weight);
        const repsValue = Number(reps);

        // Weight validation
        if (
            weight === '' ||
            !Number.isFinite(weightValue) ||
            weightValue < 0
        ) {
            toast.error("Invalid weight. Please enter 0 or a positive weight.");
            return;
        }

        // Reps validation
        if (
            reps === '' ||
            !Number.isFinite(repsValue) ||
            repsValue <= 0
        ) {
            toast.error("Invalid reps. Please enter at least 1 rep.");
            return;
        }

        onAdd(weightValue, repsValue);

        // Reset for next set
        setWeight(0);
        setReps('');
    };

    return (
        <form
            className="add-set-form"
            onSubmit={handleSubmit}
        >

            <label className="add-set-label">
                LOG NEW SET
            </label>

            <div className="add-set-inputs">

                <input
                    type="number"
                    min="0"
                    step="0.5"
                    placeholder="Weight"
                    value={weight}
                    onChange={(e) => setWeight(e.target.value)}
                    disabled={disabled}
                />

                <input
                    type="number"
                    min="1"
                    step="1"
                    placeholder="Reps"
                    value={reps}
                    onChange={(e) => setReps(e.target.value)}
                    disabled={disabled}
                />

            </div>

            <button
                type="submit"
                className="btn-log-set"
                disabled={disabled}
            >
                ✓ Log Set
            </button>

        </form>
    );
}

export default AddSetForm;