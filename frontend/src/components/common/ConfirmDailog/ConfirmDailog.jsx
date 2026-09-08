
import '../ConfirmDailog/ConfirmDailog.css';
const ConfirmDialog = ({
    open,
    message,
    confirmText = "Confirm",
    cancelText = "Cancel",
    onConfirm,
    onCancel,
}) => {

    if (!open) return null;

    return (
        <div className="dialog-overlay">

            <div className="dialog">


                <p>{message}</p>

                <div className="dialog-actions">

                    <button
                        className="btn btn-secondary"
                        onClick={onCancel}
                    >
                        {cancelText}
                    </button>
                    
                    <button
                        className="btn btn-danger"
                        onClick={onConfirm}
                    >
                        {confirmText}
                    </button>

                </div>

            </div>

        </div>
    );
};

export default ConfirmDialog;