import { useEffect, useState } from "react";
import { nutritionService } from "../../../services/nutritionService";
import { toast } from "react-toastify";

import FoodSearch from "../FoodSearch/FoodSearch";
import FoodSearchResults from "../../macrotracker/FoodSearchResults/FoodSearchResults";
import FoodForm from "../FoodForm/FoodForm";
import "./AddFoodModal.css"; // Ensure this is imported

function AddFoodModal({
    selectedMeal,
    editingFood,
    onClose,
    onFoodSaved
}) {

    const [search, setSearch] = useState("");
    const [selectedFood, setSelectedFood] = useState(null);
    const [quantity, setQuantity] = useState(100);
    const [unit, setUnit] = useState("g");
    const [mealType, setMealType] = useState(selectedMeal);
    const [saving, setSaving] = useState(false);
    const [foods, setFoods] = useState([]);
    const [loadingSearch, setLoadingSearch] = useState(false);
    const [searchError, setSearchError] = useState("");
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(false);
    const [loadingMore, setLoadingMore] = useState(false);

    // ==============================
    // INITIALIZE
    // ==============================
    useEffect(() => {
        if (editingFood) {
            setQuantity(editingFood.quantity);
            setUnit(editingFood.unit);
            setMealType(editingFood.mealType);

            const loadFood = async () => {
                try {
                    setLoadingSearch(true);
                    const food = await nutritionService.getFoodDetails(editingFood.fdcId);
                    setSelectedFood(food);
                } catch (error) {
                    console.error(error);
                    toast.error("Unable to load food details.");
                } finally {
                    setLoadingSearch(false);
                }
            };
            loadFood();
        } else {
            setSelectedFood(null);
            setSearch("");
            setQuantity(100);
            setUnit("g");
            setMealType(selectedMeal);
        }
    }, [editingFood, selectedMeal]);

    // ==============================
    // SEARCH
    // ==============================
    const handleSearch = async () => {
        const query = search.trim();
        if (query.length < 3) {
            setFoods([]);
            setSelectedFood(null);
            setHasMore(false);
            setPage(1);
            return;
        }

        try {
            setLoadingSearch(true);
            setSearchError("");
            const response = await nutritionService.searchFoods(query, 1);
            const results = response.foods || [];
            
            setFoods(results);
            setPage(1);
            setHasMore(results.length === 10);
            setSelectedFood(null);
        } catch (error) {
            console.error(error);
            setSearchError("Unable to search foods.");
            toast.error("Unable to search foods.");
        } finally {
            setLoadingSearch(false);
        }
    };

    useEffect(() => {
        const query = search.trim();
        if (!query) {
            setFoods([]);
            return;
        }
        const timer = setTimeout(() => {
            handleSearch();
        }, 500);
        return () => clearTimeout(timer);
    }, [search]);

    // ==============================
    // SELECT FOOD
    // ==============================
    const handleFoodSelect = async (fdcId) => {
        try {
            setLoadingSearch(true);
            setSearchError("");
            const food = await nutritionService.getFoodDetails(fdcId);
            setSelectedFood(food);
        } catch (error) {
            console.error("Food details API failed:", error);
            setSearchError("Unable to load food details.");
            toast.error("Failed to load food details.");
        } finally {
            setLoadingSearch(false);
        }
    };

    // ==============================
    // SAVE / UPDATE
    // ==============================
    const handleSave = async () => {
        if (!selectedFood) return;
        if (quantity <= 0) {
            toast.warning("Quantity must be greater than zero.");
            return;
        }

        try {
            setSaving(true);
            const request = {
                fdcId: selectedFood.fdcId,
                quantity: Number(quantity),
                unit,
                mealType
            };

            if (editingFood) {
                await nutritionService.updateFoodLog(editingFood.id, request);
                toast.success("Food updated successfully.");
            } else {
                await nutritionService.addFoodLog(request);
                toast.success("Food added successfully.");
            }
            await onFoodSaved();
        } catch (error) {
            console.error(error);
            toast.error(editingFood ? "Unable to update food." : "Unable to save food.");
        } finally {
            setSaving(false);
        }
    };

    // ==============================
    // LOAD MORE
    // ==============================
    const handleShowMore = async () => {
        if (!hasMore || loadingMore) return;
        const nextPage = page + 1;
        try {
            setLoadingMore(true);
            const response = await nutritionService.searchFoods(search.trim(), nextPage);
            const newFoods = response.foods || [];
            
            setFoods(previous => [...previous, ...newFoods]);
            setPage(nextPage);
            if (newFoods.length < 10) {
                setHasMore(false);
            }
        } catch (error) {
            console.error(error);
            toast.error("Unable to load more foods.");
        } finally {
            setLoadingMore(false);
        }
    };

    // ==============================
    // MODAL UI
    // ==============================
    return (
        <div className="add-food-modal-overlay">
            <div className="add-food-modal">
                <div className="modal-header">
                    <h2>{editingFood ? "Edit Food" : "Add Food"}</h2>
                    <button className="close-btn" onClick={onClose}>✕</button>
                </div>

                <FoodSearch
                    search={search}
                    setSearch={setSearch}
                    loading={loadingSearch}
                />

                <FoodSearchResults
                    foods={foods}
                    selectedFood={selectedFood}
                    onSelect={handleFoodSelect}
                    loadingFood={loadingSearch}
                    loadingMore={loadingMore}
                    hasMore={hasMore}
                    onShowMore={handleShowMore}
                />

                <FoodForm
                    selectedFood={selectedFood}
                    quantity={quantity}
                    setQuantity={setQuantity}
                    unit={unit}
                    setUnit={setUnit}
                    mealType={mealType}
                    setMealType={setMealType}
                    saving={saving}
                    onCancel={onClose}
                    onSave={handleSave}
                    editingFood={editingFood}
                />

                {searchError && (
                    <p className="search-error">{searchError}</p>
                )}
            </div>
        </div>
    );
}

export default AddFoodModal;