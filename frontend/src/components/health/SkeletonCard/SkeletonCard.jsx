export default function SkeletonCard({ className = "" }) {
    return (
        <div className={`skeleton-card ${className}`}>
            <div className="skeleton-title"></div>

            <div className="skeleton-line"></div>
            <div className="skeleton-line"></div>
            <div className="skeleton-line short"></div>
        </div>
    );
}