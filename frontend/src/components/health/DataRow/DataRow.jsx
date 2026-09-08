import "./DataRow.css";
export default function DataRow({
    icon,
    label,
    value,
    badge
}) {
    return (
        <li className="data-row">
            <span className="data-row-label">
                {icon}
                {label}
            </span>

            <div className="data-row-value">
                <strong>{value}</strong>
                {badge}
            </div>
        </li>
    );
}