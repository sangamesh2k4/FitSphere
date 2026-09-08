export function InfoListCard({ title, icon, colorClass, items }) {
  if (!items || items.length === 0) return null;

  return (
    <div className="content-card">
      <h2 className="card-heading flex-heading">
        <i className={`ti ti-${icon} ${colorClass}`}></i> {title}
      </h2>
      <div className="tips-list">
        {items.map((item, index) => (
          <div className="list-item" key={index}>
            {/* Defaults to a checkmark for green, 'x' for red */}
            <i className={`ti ti-${colorClass === 'icon-green' ? 'check' : 'x'} ${colorClass}`}></i>
            <span>{item}</span>
          </div>
        ))}
      </div>
    </div>
  );
}