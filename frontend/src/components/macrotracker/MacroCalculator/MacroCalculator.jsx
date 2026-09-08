import { useState } from "react";
import { Calculator, Scale } from "lucide-react";
import { nutritionService } from "../../../services/nutritionService";
import "./MacroCalculator.css";

export default function MacroCalculator({ food, onCalculated }) {

    const [quantity, setQuantity] = useState(100);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleCalculate = async (e) => {
        e.preventDefault();

        if (!quantity || Number(quantity) <= 0) {
            setError("Enter a valid quantity.");
            return;
        }

        setError("");

        try {
            setLoading(true);

            const response =
                await nutritionService.calculateMacros({
                    fdcId: food.fdcId,
                    quantity: Number(quantity),
                    unit: "g"
                });

            onCalculated(response);

        } catch (error) {
            console.error("Macro calculation failed:", error);
            setError("Unable to calculate macros. Please try again.");

        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="macro-calculator">

            <div className="calculator-food">

                <div className="calculator-food-icon">
                    <Scale size={21} />
                </div>

                <div>
                    <span className="calculator-label">
                        SELECTED FOOD
                    </span>

                    <h2>{food.description}</h2>

                    {food.brandName && (
                        <p>{food.brandName}</p>
                    )}
                </div>

            </div>

            <form
                className="calculator-form"
                onSubmit={handleCalculate}
            >

                <div className="quantity-group">

                    <label htmlFor="quantity">
                        Quantity
                    </label>

                    <div className="quantity-input">

                        <input
                            id="quantity"
                            type="number"
                            min="1"
                            step="1"
                            value={quantity}
                            onChange={(e) =>
                                setQuantity(e.target.value)
                            }
                            disabled={loading}
                            required
                        />

                        <span>grams</span>

                    </div>

                </div>

                <button
                    type="submit"
                    disabled={loading}
                >
                    <Calculator size={17} />

                    {loading
                        ? "Calculating..."
                        : "Calculate Macros"}
                </button>

            </form>

            {error && (
                <p className="calculator-error">
                    {error}
                </p>
            )}

        </section>
    );
}