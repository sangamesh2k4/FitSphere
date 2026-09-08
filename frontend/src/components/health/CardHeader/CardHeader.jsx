import "./CardHeader.css";

export default function CardHeader({
    icon: Icon,
    title,
    color
}) {
    return (
        <h3 className="card-header">
            <Icon
                size={20}
                color={color}
                className="card-header-icon"
            />
            {title}
        </h3>
    );
}