import "./Skeleton.css";

function Skeleton({
    width = "100%",
    height = "16px",
    borderRadius = "8px",
    className = ""
}) {

    return (
        <div
            className={`skeleton ${className}`}
            style={{
                width,
                height,
                borderRadius
            }}
        />
    );
}

export default Skeleton;