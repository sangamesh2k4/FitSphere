import "./LoadingSkeleton.css";

function LoadingSkeleton() {
    return (
        <div className="loading-skeleton" aria-label="Loading">
            <div className="loading-skeleton-line loading-skeleton-title"></div>
            <div className="loading-skeleton-line loading-skeleton-text"></div>
            <div className="loading-skeleton-line loading-skeleton-text short"></div>
        </div>
    );
}

export default LoadingSkeleton;