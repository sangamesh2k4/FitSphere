import { useEffect, useState } from "react";
import "./MeasurementForm.css";

const emptyForm = {
    weight: "",
    bodyFatPercentage: "",
    waist: "",
    chest: "",
    leftArm: "",
    rightArm: "",
    leftThigh: "",
    rightThigh: "",
    recordedAt: ""
};

const getLocalDateTime = (date) => {
    if (!date) return "";

    const local = new Date(date);

    if (Number.isNaN(local.getTime())) {
        return "";
    }

    return local
        .toLocaleString("sv")
        .replace(" ", "T")
        .slice(0, 16);
};

function MeasurementForm({
    measurement = null,
    onSubmit,
    onCancel,
    loading = false
}) {

    const [form, setForm] = useState(emptyForm);
    const [error, setError] = useState("");

    useEffect(() => {
        // Lock body scroll when the modal mounts
        document.body.style.overflow = "hidden";

        // Unlock body scroll when the modal unmounts
        return () => {
            document.body.style.overflow = "unset";
        };
    }, []);

    useEffect(() => {

        if (measurement) {

            setForm({
                weight: measurement.weight ?? "",
                bodyFatPercentage:
                    measurement.bodyFatPercentage ?? "",
                waist: measurement.waist ?? "",
                chest: measurement.chest ?? "",
                leftArm: measurement.leftArm ?? "",
                rightArm: measurement.rightArm ?? "",
                leftThigh: measurement.leftThigh ?? "",
                rightThigh: measurement.rightThigh ?? "",

                recordedAt: getLocalDateTime(
                    measurement.recordedAt
                )
            });

        } else {

            setForm({
                ...emptyForm,
                recordedAt: getLocalDateTime(new Date())
            });

        }

        setError("");

    }, [measurement]);

    const handleChange = (e) => {

        const { name, value } = e.target;

        setForm(prev => ({
            ...prev,
            [name]: value
        }));

        setError("");
    };

    const handleSubmit = (e) => {

        e.preventDefault();

        const measurementFields = [
            form.weight,
            form.bodyFatPercentage,
            form.waist,
            form.chest,
            form.leftArm,
            form.rightArm,
            form.leftThigh,
            form.rightThigh
        ];

        const isFormEmpty =
            measurementFields.every(value => value === "");

        if (isFormEmpty) {
            setError(
                "Please enter at least one measurement."
            );
            return;
        }

        const toNumberOrNull = (value) =>
            value === "" ? null : Number(value);

        const request = {

            weight: toNumberOrNull(form.weight),

            bodyFatPercentage:
                toNumberOrNull(form.bodyFatPercentage),

            waist:
                toNumberOrNull(form.waist),

            chest:
                toNumberOrNull(form.chest),

            leftArm:
                toNumberOrNull(form.leftArm),

            rightArm:
                toNumberOrNull(form.rightArm),

            leftThigh:
                toNumberOrNull(form.leftThigh),

            rightThigh:
                toNumberOrNull(form.rightThigh),

            recordedAt: form.recordedAt
    ? `${form.recordedAt}:00`
    : null
        };
        onSubmit(request);
    };

    return (
        <form
            className="measurement-form"
            onSubmit={handleSubmit}
        >

            <div className="measurement-form-header">

                <div>
                    <h2>
                        {measurement
                            ? "Edit Measurement"
                            : "Log Measurement"}
                    </h2>

                    <p>
                        Record your current body measurements.
                    </p>
                </div>

                <button
                    type="button"
                    className="measurement-form-close"
                    onClick={onCancel}
                    disabled={loading}
                >
                    ×
                </button>

            </div>

            {error && (
                <div className="measurement-form-error">
                    {error}
                </div>
            )}

            <div className="measurement-form-grid">

                <MeasurementInput
                    id="weight"
                    label="Weight"
                    unit="kg"
                    value={form.weight}
                    onChange={handleChange}
                    placeholder="65.0"
                />

                <MeasurementInput
                    id="bodyFatPercentage"
                    label="Body Fat"
                    unit="%"
                    value={form.bodyFatPercentage}
                    onChange={handleChange}
                    placeholder="14.5"
                />

                <MeasurementInput
                    id="chest"
                    label="Chest"
                    unit="cm"
                    value={form.chest}
                    onChange={handleChange}
                    placeholder="98"
                />

                <MeasurementInput
                    id="waist"
                    label="Waist"
                    unit="cm"
                    value={form.waist}
                    onChange={handleChange}
                    placeholder="81"
                />

                <MeasurementInput
                    id="leftArm"
                    label="Left Arm"
                    unit="cm"
                    value={form.leftArm}
                    onChange={handleChange}
                    placeholder="36"
                />

                <MeasurementInput
                    id="rightArm"
                    label="Right Arm"
                    unit="cm"
                    value={form.rightArm}
                    onChange={handleChange}
                    placeholder="35.5"
                />

                <MeasurementInput
                    id="leftThigh"
                    label="Left Thigh"
                    unit="cm"
                    value={form.leftThigh}
                    onChange={handleChange}
                    placeholder="58"
                />

                <MeasurementInput
                    id="rightThigh"
                    label="Right Thigh"
                    unit="cm"
                    value={form.rightThigh}
                    onChange={handleChange}
                    placeholder="57.5"
                />

            </div>

            <div className="form-group recorded-at-group">

                <label htmlFor="recordedAt">
                    Recorded At
                </label>

                <input
                    id="recordedAt"
                    name="recordedAt"
                    type="datetime-local"
                    value={form.recordedAt}
                    onChange={handleChange}
                    disabled={loading}
                />

            </div>

            <div className="measurement-form-actions">

                <button
                    type="button"
                    className="cancel-button"
                    onClick={onCancel}
                    disabled={loading}
                >
                    Cancel
                </button>

                <button
                    type="submit"
                    className="submit-button"
                    disabled={loading}
                >
                    {loading
                        ? "Saving..."
                        : measurement
                            ? "Update Measurement"
                            : "Save Measurement"}
                </button>

            </div>

        </form>
    );
}

function MeasurementInput({
    id,
    label,
    unit,
    value,
    onChange,
    placeholder
}) {

    return (
        <div className="form-group">

            <label htmlFor={id}>
                {label}
            </label>

            <div className="input-with-unit">

                <input
                    id={id}
                    name={id}
                    type="number"
                    step="0.1"
                    min="0"
                    value={value}
                    onChange={onChange}
                    placeholder={placeholder}
                />

                <span>{unit}</span>

            </div>

        </div>
    );
}

export default MeasurementForm;