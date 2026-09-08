import MeasurementForm from "../MeasurementForm/MeasurementForm";
import "./MeasurementModalManager.css";

function MeasurementModalManager({
    open,
    measurement = null,
    onSubmit,
    onClose,
    loading = false
}) {
    if (!open) {
        return null;
    }

    return (
        <div
            className="measurement-modal-backdrop"
            onMouseDown={(e) => {
                if (e.target === e.currentTarget) {
                    onClose();
                }
            }}
        >
            <div className="measurement-modal">

                <MeasurementForm
                    measurement={measurement}
                    onSubmit={onSubmit}
                    onCancel={onClose}
                    loading={loading}
                />

            </div>
        </div>
    );
}

export default MeasurementModalManager;