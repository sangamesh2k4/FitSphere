import { getBadgeIcon, getBadgeType } from "../../../utils/badgeUtils";

export default function Badge({ label, type }) {
    if (!label) return null;

    const badgeType = type || getBadgeType(label);
    const icon = getBadgeIcon(badgeType);

    return (
        <span className={`badge badge-${badgeType}`}>
            <span className="badge-icon">{icon}</span>
            {label}
        </span>
    );
}