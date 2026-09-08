import { Heart, Search, ChevronRight } from "lucide-react";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../hooks/useAuth";
import { useFavorites } from "../../../hooks/useFavorites";
import { AuthModalContext } from "../../../context/AuthModalContext";
import "../FoodCard/FoodCard.css";

function FoodCard({
    food,
    selected = false,
    onClick,
    disabled = false,
    rightContent,
    detailsPath
}) {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const { openLogin } = useContext(AuthModalContext);
    const { isFoodFavorite, toggleFoodFavorite } = useFavorites();
    
    const isFavorite = isFoodFavorite(food.fdcId);

    const handleFavoriteClick = async (e) => {
        e.preventDefault();
        e.stopPropagation(); // Prevents click from bubbling to the parent actions div

        if (!isAuthenticated) {
            openLogin();
            return;
        }

        await toggleFoodFavorite(food.fdcId);
    };

    const handleNavigate = () => {
        if (disabled) return;
        
        if (detailsPath) {
            navigate(detailsPath);
            return;
        }
        
        if (onClick) {
            onClick();
        }
    };

    // Safeguard against null/undefined descriptions
    const title = food.description ?? "";

    return (
        <article
            className={`food-card ${selected ? "selected" : ""} ${disabled ? "disabled" : ""}`}
        >
            {/* Main clickable card body */}
            <div 
                className="food-card-body" 
                onClick={handleNavigate}
                onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        handleNavigate();
                    }
                }}
                role="button"
                tabIndex={disabled ? -1 : 0}
            >
                <div className="food-card-icon">
                    <Search size={18} />
                </div>

                <div className="food-card-info">
                    <strong className="food-card-name">
                        {title.length > 70
                            ? `${title.substring(0, 70)}...`
                            : title}
                    </strong>

                    <div className="food-card-details">
                        {food.brandName && (
                            <div className="food-detail-item">
                                <span className="food-detail-label">Brand</span>
                                <span className="food-detail-value">{food.brandName}</span>
                            </div>
                        )}

                        {food.foodCategory && (
                            <div className="food-detail-item">
                                <span className="food-detail-label">Category</span>
                                <span className="food-detail-value">{food.foodCategory}</span>
                            </div>
                        )}

                        {food.servingSize && (
                            <div className="food-detail-item">
                                <span className="food-detail-label">Serving</span>
                                <span className="food-detail-value">
                                    {food.servingSize} {food.servingSizeUnit}
                                </span>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Actions area containing standalone buttons */}
            <div className="food-card-actions" onClick={handleNavigate}>
                <button
                    type="button"
                    className={`favorite-button ${isFavorite ? "active" : ""}`}
                    onClick={handleFavoriteClick}
                    disabled={disabled}
                >
                    <Heart
                        size={20}
                        fill={isFavorite ? "currentColor" : "none"}
                    />
                </button>

                {rightContent ?? <ChevronRight size={19} />}
            </div>
        </article>
    );
}

export default FoodCard;