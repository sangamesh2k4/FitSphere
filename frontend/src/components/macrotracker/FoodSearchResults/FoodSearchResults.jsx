import {ChevronDown} from "lucide-react";
import "./FoodSearchResults.css";
import FoodCard from "../../nutrition/FoodCard/FoodCard";

export default function FoodSearchResults({
    foods,
    selectedFood,
    onSelect,
    loadingFood,
    loadingMore,
    hasMore,
    onShowMore
}) {

    if (!foods || foods.length === 0) {
        return null;
    }

    return (
        <section className="food-results">

            <div className="food-results-header">

                <div>
                    <span className="food-results-label">
                        SEARCH RESULTS
                    </span>

                    <h2>Choose a Food</h2>
                </div>

                <span className="food-results-count">
                   {foods.length === 1
    ? "1 food found"
    : `${foods.length} foods found`}
                </span>

            </div>


            <div className="food-results-grid">

                {foods.map((food) => (

<FoodCard
    key={food.fdcId}
    food={food}
    selected={selectedFood?.fdcId === food.fdcId}
    onClick={() => onSelect(food.fdcId)}
    disabled={loadingFood}
/>

                ))}

            </div>


            {hasMore && (
                <div className="show-more-container">

                    <button
                        type="button"
                        className="show-more-btn"
                        onClick={onShowMore}
                        disabled={loadingMore}
                    >

                        <ChevronDown size={17} />

                        {loadingMore
                            ? "Loading..."
                            : "Show More"}

                    </button>

                </div>
            )}

        </section>
    );
}