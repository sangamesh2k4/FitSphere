import { useContext } from "react";
import { Link } from "react-router-dom";
import { Heart } from "lucide-react";

import { useAuth } from "../../hooks/useAuth";
import { AuthModalContext } from "../../context/AuthModalContext";
import { useFavorites } from "../../hooks/useFavorites";
import {formatEnum} from "../../utils/formatEnum";

function ExerciseCard({ exercise,detailsPath }) {
    const { isAuthenticated } = useAuth();
    const { openLogin } = useContext(AuthModalContext);
    
    const { isExerciseFavorite, toggleExerciseFavorite } = useFavorites();
    const isFavorite = isExerciseFavorite(exercise.id);


    const handleFavoriteClick = async (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (!isAuthenticated) {
            openLogin();
            return;
        }

        await toggleExerciseFavorite(exercise.id);
    };

    return (
        <article className="exercise-card horizontal-layout">
            {/* Left Side: Square Image */}
            <div className="exercise-image-left">
                <img 
                src={exercise.imageUrl}
                alt={exercise.name}/>
            </div>

            {/* Right Side: Content */}
            <div className="exercise-content-right">
                
                {/* Top Section: Title, Badge, and Heart */}
                <div className="exercise-header-top">
                    <div className="title-badge-group">
                        <h2>{exercise.name}</h2>
                        <span className={`difficulty-badge outline ${exercise.difficulty?.toLowerCase()}`}>
                            {formatEnum(exercise.difficulty)}
                        </span>
                    </div>

                    <button
                        className={`favorite-button ${isFavorite ? "active" : ""}`}
                        onClick={handleFavoriteClick}
                        aria-label={isFavorite ? "Remove from favorites": "Add to favorites" }
                    >
                        <Heart
    size={20}
    fill={isFavorite ? "currentColor" : "none"}
/>
                    </button>
                </div>
                
                {/* Bottom Section: Category & View Link */}
                <div className="exercise-footer-bottom">
                    <span className="exercise-meta">
                        {formatEnum(exercise.category)} • {formatEnum(exercise.primaryMuscle)}
                    </span>

                    <Link
    to={detailsPath}
    className="exercise-view-button"
>
    View Exercise &rarr;
</Link>
                </div>

            </div>
        </article>
    );
}

export default ExerciseCard;