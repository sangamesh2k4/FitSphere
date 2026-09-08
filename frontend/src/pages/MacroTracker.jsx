import { useEffect, useRef, useState } from "react";
import { nutritionService } from "../services/nutritionService";

// Extracted Components
import MacroHeader from "../components/macrotracker/Macroheader/MacroHeader";
import FoodSearchBox from "../components/macrotracker/FoodSearchBox/FoodSearchBox";

// Existing Components
import FoodSearchResults from "../components/macrotracker/FoodSearchResults/FoodSearchResults";
import MacroCalculator from "../components/macrotracker/MacroCalculator/MacroCalculator";
import MacroResult from "../components/macrotracker/MacroResult/MacroResult";

import "../styles/nutrition/macroTracker.css";

function MacroTracker() {
    /* --- STATE --- */
    const [query, setQuery] = useState("");
    const [foods, setFoods] = useState([]);
    const [selectedFood, setSelectedFood] = useState(null);
    const [macroResult, setMacroResult] = useState(null);

    const [loading, setLoading] = useState(false);
    const [loadingFood, setLoadingFood] = useState(false);
    const [loadingMore, setLoadingMore] = useState(false);
    const [error, setError] = useState("");

    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);

    /* --- REFS --- */
    const detailCardRef = useRef(null);

    /* --- AUTO SCROLL (Useful for Mobile) --- */
    useEffect(() => {
        // Scroll to the detail card on mobile when a food is selected
        if (selectedFood && detailCardRef.current && window.innerWidth < 992) {
            detailCardRef.current.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });
        }
    }, [selectedFood, macroResult]);


    /* --- HANDLERS --- */
    const handleSearch = async (e) => {
        e.preventDefault();
        const trimmedQuery = query.trim();
        if (!trimmedQuery) {
            setError("Enter a food to search.");
            return;
        }

        setError("");
        try {
            setLoading(true);
            const response = await nutritionService.searchFoods(trimmedQuery, 1);
            const newFoods = response.foods || [];
            setFoods(newFoods);
            setPage(1);
            setHasMore(newFoods.length === 10);
            
            // Reset right panel
            setSelectedFood(null);
            setMacroResult(null);
        } catch  {
            setError("Unable to search foods. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleFoodSelect = async (fdcId) => {
        try {
            setLoadingFood(true);
            setError("");
            const response = await nutritionService.getFoodDetails(fdcId);
            setMacroResult(null);
            setSelectedFood(response);
        } catch  {
            setError("Unable to load food details. Please try again.");
        } finally {
            setLoadingFood(false);
        }
    };

    const handleShowMore = async () => {
        if (loadingMore || !hasMore || !query.trim()) return;
        
        const nextPage = page + 1;
        try {
            setLoadingMore(true);
            setError("");
            const response = await nutritionService.searchFoods(query.trim(), nextPage);
            const newFoods = response.foods || [];
            
            setFoods(prev => [...prev, ...newFoods]);
            setPage(nextPage);
            if (newFoods.length < 10) setHasMore(false);
        } catch {
            setError("Unable to load more foods. Please try again.");
        } finally {
            setLoadingMore(false);
        }
    };

    /* --- RENDER --- */
    return (
        <main className="macro-tracker-page">
            
            <MacroHeader />

            <FoodSearchBox 
                query={query}
                setQuery={setQuery}
                onSearch={handleSearch}
                loading={loading}
            />

            {error && <p className="macro-error">{error}</p>}

            {/* TWO-COLUMN GRID LAYOUT */}
            <section className="macro-content-grid">
    
    {/* LEFT COLUMN: List */}
    <div className="macro-list-column">
        {foods.length > 0 ? (
            <FoodSearchResults
                foods={foods}
                onSelect={handleFoodSelect}
                loadingFood={loadingFood}
                onShowMore={handleShowMore}
                loadingMore={loadingMore}
                hasMore={hasMore}
                searchQuery={query}
            />
        ) : (
            /* --- LEFT COLUMN PLACEHOLDER --- */
            <div className="macro-placeholder">
                <div className="placeholder-content">
                    {/* Optional: A subtle search icon */}
                    <i 
                        className="ti ti-search" 
                        style={{ fontSize: '2.5rem', color: 'rgba(255, 255, 255, 0.15)', marginBottom: '1rem', display: 'inline-block' }}
                    ></i>
                    <h3>What’s on the menu? </h3>
<p>Type a snack or meal above to check the macros and run the stats.</p>
                </div>
            </div>
        )}
    </div>

    {/* RIGHT COLUMN: Calculator & Result Card OR Placeholder */}
    <div className="macro-detail-column">
        {selectedFood ? (
            <div className="food-detail-card" ref={detailCardRef}>
                <MacroCalculator
                    food={selectedFood}
                    onCalculated={setMacroResult}
                />
                
                {macroResult && (
                    <MacroResult result={macroResult} />
                )}
            </div>
        ) : (
            /* --- RIGHT COLUMN PLACEHOLDER --- */
            <div className="macro-placeholder">
                <div className="placeholder-content">
                    <i 
                        className="ti ti-calculator" 
                        style={{ fontSize: '2.5rem', color: 'rgba(255, 255, 255, 0.15)', marginBottom: '1rem', display: 'inline-block' }}
                    ></i>
                    <h3>No Food Selected</h3>
                    <p>Search and select a food from the list to view its nutritional details and calculate macros.</p>
                </div>
            </div>
        )}
    </div>

</section>
        </main>
    );
}

export default MacroTracker;