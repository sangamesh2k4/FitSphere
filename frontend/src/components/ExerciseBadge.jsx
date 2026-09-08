import { getBadgeType, getBadgeIcon } from '../utils/badgeUtils';

export function ExerciseBadge({ label, type = 'auto' }) {
  if (!label) return null;
  
  // If type is 'auto', it uses your utility. Otherwise, forces a specific type (e.g., 'info' for equipment)
  const badgeType = type === 'auto' ? getBadgeType(label) : type;
  const icon = type === 'auto' ? getBadgeIcon(badgeType) : null;

  return (
    <span className={`badge badge-${badgeType}`}>
      {icon && <span>{icon}</span>} {label}
    </span>
  );
}