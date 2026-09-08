import { useEffect, useState } from "react";
import favoriteService from "../services/favoriteService";
import FoodCard from "../components/nutrition/FoodCard/FoodCard";
import ExerciseCard from "../components/exercises/ExerciseCard";
import ExerciseCardSkeleton from "../components/skeleton/ExerciseCardSkeleton";
import FoodCardSkeleton from "../components/skeleton/FoodCardSkeleton";
import "../styles/favorites/favorites.css";

function FavoritesPage() {
    const [activeTab, setActiveTab] = useState("exercises");
    

    // States
    const [favoriteExercises, setFavoriteExercises] = useState([]);
    const [favoriteFoods, setFavoriteFoods] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // Tracks which tabs have already loaded their data for lazy loading
    const [fetchedTabs, setFetchedTabs] = useState({
        exercises: false,
        foods: false
    });

    // loadFavorites is moved out so it can be triggered on deletion
    const loadFavorites = async (forceRefresh = false) => {
        // Skip fetching if already fetched (unless we force a refresh after deleting an item)
        if (!forceRefresh && fetchedTabs[activeTab]) {
            return;
        }

        try {
            setError("");
            setLoading(true);

            if (activeTab === "exercises") {
                const exerciseData = await favoriteService.getUserFavorites();
                setFavoriteExercises(exerciseData);
            } else if (activeTab === "foods") {
                const foodData = await favoriteService.getUserFoodFavorites();
                setFavoriteFoods(foodData);
            }

            // Mark the current tab as fetched so we don't spam the API on tab switches
            setFetchedTabs(prev => ({ ...prev, [activeTab]: true }));

        } catch (err) {
            setError(
                err.response?.data?.message || 
                "Failed to load favorites."
            );
        } finally {
            setLoading(false);
        }
    };

    // Trigger loadFavorites whenever the activeTab changes
    useEffect(() => {
        loadFavorites();
    }, [activeTab]);

    // Better Error UI
    if (error) {
        return (
            <div className="favorites-empty">
                <h2>Something went wrong</h2>
                <p>{error}</p>
            </div>
        );
    }

    return (
        <div className="favorites-page">
            {/* Toggle Navigation */}
            <div className="favorites-toggle">
                <button
                    className={activeTab === "exercises" ? "active" : ""}
                    onClick={() => setActiveTab("exercises")}
                >
                    Exercises
                </button>

                <button
                    className={activeTab === "foods" ? "active" : ""}
                    onClick={() => setActiveTab("foods")}
                >
                    Foods
                </button>
            </div>

            {/* Content Area */}
            <div className="favorites-content">
                {activeTab === "exercises" ? (
                    loading ? (
                        <div className="favorites-grid">
                            <ExerciseCardSkeleton count={6} />
                        </div>
                    ) : favoriteExercises.length === 0 ? (
                        <div className="favorites-empty">
                            <h2>No favorite exercises yet</h2>
                            <p>Save exercises to quickly access them here.</p>
                        </div>
                    ) : (
                        <div className="favorites-grid">
                            {favoriteExercises.map((exercise) => (
                                <ExerciseCard
    key={exercise.id}
    exercise={exercise}
    detailsPath={`/app/exercises/${exercise.id}`}
/>
                            ))}
                        </div>
                    )
                ) : (
                    loading ? (
                        <div className="favorites-grid">
                            <FoodCardSkeleton count={6} />
                        </div>
                    ) : favoriteFoods.length === 0 ? (
                        <div className="favorites-empty">
                            <h2>No favorite foods yet</h2>
                            <p>Save foods to quickly access them here.</p>
                        </div>
                    ) : (
                        <div className="favorites-grid">
                            {favoriteFoods.map((food) => (
                                <FoodCard
                                    key={food.fdcId}
                                    food={food}
                                    detailsPath={`/app/macro-tracker/food/${food.fdcId}`}
                                />
                            ))}
                        </div>
                    )
                )}
            </div>
        </div>
    );
}

export default FavoritesPage;