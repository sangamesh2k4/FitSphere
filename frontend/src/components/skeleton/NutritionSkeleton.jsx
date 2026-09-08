import "./Skeleton.css"
export default function NutritionSkeleton() {
    return (
        <div className="nutrition-skeleton">

            {/* Header */}
            <div className="skeleton-header">
                <div className="skeleton skeleton-title"></div>
                <div className="skeleton skeleton-subtitle"></div>
            </div>

            {/* Summary */}
            <div className="skeleton-summary">

                <div className="skeleton-card">
                    <div className="skeleton skeleton-small"></div>
                    <div className="skeleton skeleton-number"></div>
                </div>

                <div className="skeleton-card">
                    <div className="skeleton skeleton-small"></div>
                    <div className="skeleton skeleton-number"></div>
                </div>

                <div className="skeleton-card">
                    <div className="skeleton skeleton-small"></div>
                    <div className="skeleton skeleton-number"></div>
                </div>

                <div className="skeleton-card">
                    <div className="skeleton skeleton-small"></div>
                    <div className="skeleton skeleton-number"></div>
                </div>

            </div>

            {/* Meals */}
            <div className="skeleton-meals">

                <div className="skeleton-section-header">
                    <div className="skeleton skeleton-section-title"></div>
                    <div className="skeleton skeleton-button"></div>
                </div>

                {[1, 2, 3].map((item) => (
                    <div
                        className="skeleton-food-card"
                        key={item}
                    >
                        <div>
                            <div className="skeleton skeleton-food-name"></div>
                            <div className="skeleton skeleton-food-quantity"></div>
                        </div>

                        <div className="skeleton skeleton-calories"></div>
                    </div>
                ))}

            </div>

        </div>
    );
}