import { useEffect, useState } from "react";
import { nutritionService } from "../../../services/nutritionService";
import LoadingSkeleton from "../../skeleton/LoadingSkeleton/LoadingSkeleton";
import "../NutritionHistory/NutritionHistory.css";

function NutritionHistory() {
    const [history, setHistory] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [loading, setLoading] = useState(true);
    const [loadingMore, setLoadingMore] = useState(false);

    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedDayLogs, setSelectedDayLogs] = useState([]);
    const [loadingDay, setLoadingDay] = useState(false);

    const [error, setError] = useState("");

    // ==============================
    // LOAD HISTORY
    // ==============================

    const loadHistory = async (pageNumber = 0) => {
        try {
            if (pageNumber === 0) {
                setLoading(true);
            } else {
                setLoadingMore(true);
            }

            setError("");

            const endDate = new Date().toISOString().split("T")[0];
            const start = new Date();
            // Fetch last 30 days
            start.setDate(start.getDate() - 30);
            const startDate = start.toISOString().split("T")[0];

            const response = await nutritionService.getHistory(
                startDate,
                endDate,
                pageNumber
            );

            const newHistory = response.content || [];

            // First page -> replace
            if (pageNumber === 0) {
                setHistory(newHistory);
            } else {
                // Next pages -> append
                setHistory((previous) => [
                    ...previous,
                    ...newHistory
                ]);
            }

            setPage(response.number ?? pageNumber);
            setTotalPages(response.totalPages ?? 0);

        } catch (error) {
            console.error("Failed to load nutrition history", error);
            setError("Unable to load nutrition history.");
        } finally {
            setLoading(false);
            setLoadingMore(false);
        }
    };

    useEffect(() => {
        loadHistory(0);
    }, []);


    // ==============================
    // SHOW MORE
    // ==============================

    const handleShowMore = () => {
        if (loadingMore) return;
        if (page >= totalPages - 1) return;
        loadHistory(page + 1);
    };


    // ==============================
    // SELECT DAY LOGS (ACCORDION TOGGLE)
    // ==============================

    const handleDateClick = async (date) => {
        // If clicking the currently open date, close it
        if (selectedDate === date) {
            setSelectedDate(null);
            setSelectedDayLogs([]);
            return;
        }

        try {
            setSelectedDate(date);
            setLoadingDay(true);
            setError("");

            const logs = await nutritionService.getFoodLogsByDate(date);
            setSelectedDayLogs(logs);

        } catch (error) {
            console.error("Failed to load food logs for date", error);
            setError("Unable to load food logs for this date.");
        } finally {
            setLoadingDay(false);
        }
    };


    // ==============================
    // FORMAT DATE
    // ==============================

    const formatDate = (date) => {
        return new Date(`${date}T00:00:00`)
            .toLocaleDateString("en-IN", {
                day: "2-digit",
                month: "short",
                year: "numeric"
            });
    };


    // ==============================
    // INITIAL LOADING
    // ==============================

    if (loading) {
    return <LoadingSkeleton />;
}

    return (
        <section className="nutrition-history">

            <div className="nutrition-history-header">
                <div>
                    <span className="section-label">HISTORY</span>
                    <h2>Nutrition History</h2>
                </div>
            </div>

            {error && (
                <p className="nutrition-history-error">{error}</p>
            )}

            {history.length === 0 ? (
                <div className="nutrition-history-empty">
                    No nutrition history available.
                </div>
            ) : (
                <>
                    {/* ============================== */}
                    {/* HISTORY LIST (ACCORDION)       */}
                    {/* ============================== */}
                    <div className="nutrition-history-list">
                        {history.map((day) => (
                            <div className="history-accordion-item" key={day.date}>
                                
                                {/* 1. The Clickable Row */}
                                <button
                                    type="button"
                                    className={`nutrition-history-row ${
                                        selectedDate === day.date ? "selected" : ""
                                    }`}
                                    onClick={() => handleDateClick(day.date)}
                                >
                                    <div className="history-date">
                                        {formatDate(day.date)}
                                    </div>

                                    <div className="history-stat">
                                        <span>Calories</span>
                                        <strong>{Math.round(day.totalCalories)} kcal</strong>
                                    </div>

                                    <div className="history-stat">
                                        <span>Protein</span>
                                        <strong>{Math.round(day.totalProtein)} g</strong>
                                    </div>

                                    <div className="history-stat">
                                        <span>Carbs</span>
                                        <strong>{Math.round(day.totalCarbohydrates)} g</strong>
                                    </div>

                                    <div className="history-stat">
                                        <span>Fat</span>
                                        <strong>{Math.round(day.totalFat)} g</strong>
                                    </div>

                                    <div className="history-stat">
                                        <span>Meals</span>
                                        <strong>{day.mealCount}</strong>
                                    </div>
                                </button>

                                {/* 2. The Expanded Section directly underneath */}
                                {selectedDate === day.date && (
                                    <div className="history-expanded-content">
                                        {loadingDay ? (
                                            <p className="expanded-msg">Loading foods...</p>
                                        ) : selectedDayLogs.length === 0 ? (
                                            <p className="expanded-msg">No foods logged on this day.</p>
                                        ) : (
                                            <div className="expanded-foods-list">
                                                {selectedDayLogs.map((food) => (
                                                    <div className="history-food-row" key={food.id}>
                                                        <div>
                                                            <strong>{food.foodName}</strong>
                                                            <span>{food.quantity} {food.unit}</span>
                                                        </div>

                                                        <div className="history-food-macros">
                                                            <span>{Math.round(food.calories)} kcal</span>
                                                            <span>P {Math.round(food.protein)}g</span>
                                                            <span>C {Math.round(food.carbohydrates)}g</span>
                                                            <span>F {Math.round(food.fat)}g</span>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>

                    {/* ============================== */}
                    {/* SHOW MORE                      */}
                    {/* ============================== */}
                    {page < totalPages - 1 && (
                        <div className="history-show-more">
                            <button
                                type="button"
                                className="show-more-btn"
                                onClick={handleShowMore}
                                disabled={loadingMore}
                            >
                                {loadingMore ? "Loading..." : "Show More"}
                            </button>
                        </div>
                    )}
                </>
            )}
        </section>
    );
}

export default NutritionHistory;