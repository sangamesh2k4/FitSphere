import { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import nutritionService from "../services/nutritionService";

import { useAuth } from "../hooks/useAuth";
import { useFavorites } from "../hooks/useFavorites";
import { AuthModalContext } from "../context/AuthModalContext";

import FoodHeader from "../components/nutrition/FoodHeader/FoodHeader";
import FoodSummaryCard from "../components/nutrition/FoodSummaryCard/FoodSummaryCard";
import NutritionFactsCard from "../components/nutrition/NutritionFactsCard/NutritionFactsCard";
import MineralsCard from "../components/nutrition/MineralsCard/MineralsCard";

import FoodDetailSkeleton from "../components/skeleton/FoodDetailSkeleton";

 import "../css/FoodDetail.css";

function FoodDetail() {
    const { fdcId } = useParams();
    const navigate = useNavigate();

    const [food, setFood] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    const { isAuthenticated } = useAuth();
    const { openLogin } = useContext(AuthModalContext);

    const {
        isFoodFavorite,
        toggleFoodFavorite
    } = useFavorites();

    const foodId = Number(fdcId);
    const isFavorite = isFoodFavorite(foodId);

    useEffect(() => {
        const loadFood = async () => {
            try {
                setLoading(true);
                setError(false);

                const data = await nutritionService.getFoodByDetails(foodId);
                setFood(data);
            } catch (err) {
                console.error(err);
                setError(true);
            } finally {
                setLoading(false);
            }
        };

        loadFood();
    }, [foodId]);

    const handleToggleFavorite = async () => {
        if (!isAuthenticated) {
            openLogin();
            return;
        }

        await toggleFoodFavorite(foodId);
    };

    if (loading) {
        return <FoodDetailSkeleton />;
    }

    if (error || !food) {
        return (
            <div className="empty-state">
                <h2>404</h2>
                <p>We couldn't find that food.</p>
                <button
                    className="back-button"
                    onClick={() => navigate(-1)}
                >
                    Back
                </button>
            </div>
        );
    }

    return (
        <main className="food-detail-wrapper">
            <FoodHeader
                food={food}
                isFavorite={isFavorite}
                onToggleFavorite={handleToggleFavorite}
                onBack={() => navigate(-1)}
            />

            <section className="detail-main">
                <div className="detail-grid">
                    <aside className="detail-sidebar">
                        <FoodSummaryCard food={food} />
                    </aside>

                    <section className="detail-content">
                        <NutritionFactsCard food={food} />
                        <MineralsCard food={food} />
                    </section>
                </div>
            </section>
        </main>
    );
}

export default FoodDetail;