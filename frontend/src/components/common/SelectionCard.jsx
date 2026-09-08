const SelectionCard = ({
    title,
    description,
    selected,
    onClick
}) => {
    return (
        <button
            type="button"
            className={`selection-card ${selected ? "selected" : ""}`}
            onClick={onClick}
        >
            <h4>{title}</h4>

            {description && (
                <p>{description}</p>
            )}
        </button>
    );
};

export default SelectionCard;