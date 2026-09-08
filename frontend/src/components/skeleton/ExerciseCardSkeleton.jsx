
function ExerciseCardSkeleton({ count = 6 }) {
    // Create an array with 'count' elements to map over
    const skeletons = Array.from({ length: count }, (_, index) => index);

    return (
        // Use a fragment <> so the cards become direct children of .favorites-grid
        <>
            {skeletons.map((index) => (
                <div key={index} className="skeleton-card">
                    {/* The image placeholder */}
                    <div className="skeleton-image skeleton"></div>
                    
                    {/* The text/content placeholders */}
                    <div className="skeleton-content">
                        <div className="skeleton-line title skeleton"></div>
                        <div className="skeleton-line badge skeleton"></div>
                        <div className="skeleton-line footer skeleton"></div>
                    </div>
                </div>
            ))}
        </>
    );
}

export default ExerciseCardSkeleton;