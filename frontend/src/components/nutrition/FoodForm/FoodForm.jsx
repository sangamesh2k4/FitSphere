import { useEffect } from "react";
import "./FoodForm.css";
import CustomSelect from "../../common/CustomSelect";

export default function FoodForm({
    selectedFood,
    quantity,
    setQuantity,
    unit,
    setUnit,
    mealType,
    setMealType,
    onCancel,
    onSave,
    saving,
    editingFood
}) {
    // Lock background scroll when the form is active
    useEffect(() => {
        if (selectedFood) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "unset";
        }

        return () => {
            document.body.style.overflow = "unset";
        };
    }, [selectedFood]);

    if (!selectedFood) {
        return null;
    }

    return (
        <>
            {/* Background blur overlay */}
            <div className="inline-backdrop" onClick={onCancel}></div>

            {/* Modal Form centered on screen */}
            <section className="food-form">

                <h3>{selectedFood.description}</h3>

                <div className="form-grid">

                    {/* Quantity */}
                    <div className="form-group">
                        <label htmlFor="food-quantity">Quantity</label>
                        <input
                            id="food-quantity"
                            type="number"
                            min="1"
                            step="0.1"
                            value={quantity}
                            onChange={(e) => setQuantity(e.target.value)}
                            disabled={saving}
                        />
                    </div>

                    {/* Unit */}
                    <div className="form-group">
                        <label htmlFor="food-unit">Unit</label>
                        <div className="select-wrapper">
                            <CustomSelect
                                name="food-unit"
                                value={unit}
                                onChange={(e) => setUnit(e.target.value)}
                                disabled={saving}
                                options={[
                                    { value: "g", label: "g" },
                                    { value: "ml", label: "ml" },
                                    { value: "pcs", label: "pcs" },
                                ]}
                            />
                        </div>
                    </div>

                    {/* Meal */}
                    <div className="form-group">
                        <label htmlFor="food-meal">Meal</label>
                        <div className="select-wrapper">
                            <CustomSelect
                                name="food-meal"
                                value={mealType}
                                onChange={(e) => setMealType(e.target.value)}
                                disabled={saving}
                                options={[
                                    { value: "BREAKFAST", label: "Breakfast" },
                                    { value: "LUNCH", label: "Lunch" },
                                    { value: "DINNER", label: "Dinner" },
                                    { value: "SNACK", label: "Snack" },
                                ]}
                            />
                        </div>
                    </div>

                </div>

                {/* Actions */}
                <div className="modal-actions">
                    <button
                        type="button"
                        className="secondary-btn"
                        onClick={onCancel}
                        disabled={saving}
                    >
                        Cancel
                    </button>

                    <button
                        type="button"
                        className="primary-btn"
                        onClick={onSave}
                        disabled={saving}
                    >
                        {saving
                            ? "Saving..."
                            : editingFood
                                ? "Update Food"
                                : "Save Food"
                        }
                    </button>
                </div>

            </section>
        </>
    );
}