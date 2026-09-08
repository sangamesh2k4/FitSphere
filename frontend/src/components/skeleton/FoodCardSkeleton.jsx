
function FoodCardSkeleton({ count = 8 }) {
    // Create an array with 'count' elements to map over for the skeletons
    const skeletons = Array.from({ length: count }, (_, index) => index);

    return (
        // The React Fragment <> ensures these divs act as direct children 
        // of .favorites-grid, allowing the 2-column layout to work perfectly.
        <>
            {skeletons.map((index) => (
                <div key={index} className="skeleton-card">
                    {/* The image placeholder (representing a food icon or image) */}
                    <div className="skeleton-image skeleton"></div>
                    
                    {/* The text/content placeholders (representing food name, calories, macros) */}
                    <div className="skeleton-content">
                        {/* Simulates the food name */}
                        <div className="skeleton-line title skeleton"></div>
                        
                        {/* Simulates a badge or calorie count */}
                        <div className="skeleton-line badge skeleton"></div>
                        
                        {/* Simulates the macro breakdown or extra info */}
                        <div className="skeleton-line footer skeleton"></div>
                    </div>
                </div>
            ))}
        </>
    );
}

export default FoodCardSkeleton;