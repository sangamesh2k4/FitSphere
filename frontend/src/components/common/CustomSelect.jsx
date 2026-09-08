import { useEffect, useRef, useState } from "react";
import { ChevronDown, Check } from "lucide-react";

function CustomSelect({
    name,
    value,
    onChange,
    options,
    placeholder
}) {
    const [open, setOpen] = useState(false);
    const containerRef = useRef(null);

    useEffect(() => {
        const handleOutsideClick = (event) => {
            if (
                containerRef.current &&
                !containerRef.current.contains(event.target)
            ) {
                setOpen(false);
            }
        };

        document.addEventListener("mousedown", handleOutsideClick);

        return () => {
            document.removeEventListener("mousedown", handleOutsideClick);
        };
    }, []);

    const selectedOption = options.find(
        option => option.value === value
    );

    const handleSelect = (optionValue) => {
        onChange({
            target: {
                name,
                value: optionValue
            }
        });

        setOpen(false);
    };

    return (
        <div
            className={`custom-select ${open ? "open" : ""}`}
            ref={containerRef}
        >
            <button
                type="button"
                className="custom-select-trigger"
                onClick={() => setOpen(previous => !previous)}
            >
                <span className={!selectedOption ? "placeholder" : ""}>
                    {selectedOption?.label || placeholder}
                </span>

                <ChevronDown
                    size={17}
                    className="custom-select-arrow"
                />
            </button>

            {open && (
                <div className="custom-select-menu">
                    {options.map(option => (
                        <button
                            key={option.value}
                            type="button"
                            className={`custom-select-option ${
                                value === option.value ? "selected" : ""
                            }`}
                            onClick={() => handleSelect(option.value)}
                        >
                            <span>{option.label}</span>

                            {value === option.value && (
                                <Check size={16} />
                            )}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}

export default CustomSelect;