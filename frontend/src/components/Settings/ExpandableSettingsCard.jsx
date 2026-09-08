import { ChevronDown, ChevronUp } from "lucide-react";

function ExpandableSettingsCard({
    title,
    isOpen,
    onToggle,
    children,
}) {
    return (
        <div className={`settings-card ${isOpen ? "expanded" : ""}`}>

            <button
                className="settings-card-header"
                onClick={onToggle}
                type="button"
            >
                <h2>{title}</h2>

                {isOpen
                    ? <ChevronUp size={22} />
                    : <ChevronDown size={22} />
                }

            </button>

            <div className="settings-card-content">
                <div>
                {children}
                </div>

            </div>

        </div>
    );
}

export default ExpandableSettingsCard;