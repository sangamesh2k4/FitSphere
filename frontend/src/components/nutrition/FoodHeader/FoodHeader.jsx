import { ArrowLeft, Heart } from "lucide-react";
import "./FoodHeader.css";

function FoodHeader({
    food,
    isFavorite,
    onToggleFavorite,
    onBack
}) {

    return (

        <header className="food-detail-header">

            <button
                type="button"
                className="back-button"
                onClick={onBack}
            >
                <ArrowLeft size={18} />
                Back
            </button>

            {food.foodCategory && (
                <p className="food-category">
                    {food.foodCategory}
                </p>
            )}

            <div className="food-title-row">

                <div className="food-title-content">

                    <h1>{food.description}</h1>

                    <p className="food-subtitle">

                        {food.brandName && (
                            <>
                                <span>{food.brandName}</span>
                                <span className="food-dot">•</span>
                            </>
                        )}

                        {food.servingSize}{" "}
                        {food.servingSizeUnit}

                    </p>

                </div>

                <button
                    type="button"
                    className={`favorite-btn ${
                        isFavorite ? "is-favorite" : ""
                    }`}
                    onClick={onToggleFavorite}
                >

                    <Heart
                        size={18}
                        fill={isFavorite ? "currentColor" : "none"}
                    />

                    {isFavorite
                        ? "Remove Favorite"
                        : "Add Favorite"}

                </button>

            </div>

        </header>

    );

}

export default FoodHeader;