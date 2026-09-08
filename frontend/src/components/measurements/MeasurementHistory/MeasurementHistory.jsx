import { useState } from "react";
import ConfirmDialog from "../../common/ConfirmDailog/ConfirmDailog";
import "./MeasurementHistory.css";

function MeasurementHistory({
    history,
    onEdit,
    onDelete,
    onLoadMore,
    hasMore
}) {
    const [deleteId, setDeleteId] = useState(null);

    const handleDeleteClick = (id) => {
        setDeleteId(id);
    };

    const handleConfirmDelete = async () => {
        if (!deleteId) return;

        await onDelete(deleteId);

        setDeleteId(null);
    };

    const handleCancelDelete = () => {
        setDeleteId(null);
    };

    return (
        <>
            <section className="measurements-section">

                <div className="section-title">
                    HISTORY
                </div>

                <div className="history-list">

                    {history.length === 0 ? (

                        <div className="history-empty">
                            No measurements recorded yet.
                        </div>

                    ) : (

                        history.map((measurement) => (

                            <div
                                key={measurement.id}
                                className="history-row"
                            >

                                <span>
                                    {new Date(
                                        measurement.recordedAt
                                    ).toLocaleDateString(
                                        "en-IN",
                                        {
                                            day: "2-digit",
                                            month: "short",
                                            year: "numeric"
                                        }
                                    )}
                                </span>

                                <span>
                                    {measurement.weight
                                        ? `${measurement.weight} kg`
                                        : "-"}{" "}
                                    ·{" "}
                                    {measurement.bodyFatPercentage
                                        ? `${measurement.bodyFatPercentage}%`
                                        : "-"}
                                </span>

                                <span>
                                    Chest {measurement.chest ?? "-"} ·{" "}
                                    Waist {measurement.waist ?? "-"}
                                </span>

                                <div className="history-actions">

                                    <button
                                        className="edit-button"
                                        onClick={() =>
                                            onEdit(measurement)
                                        }
                                    >
                                        Edit
                                    </button>

                                    <button
                                        className="delete-button"
                                        onClick={() =>
                                            handleDeleteClick(measurement.id)
                                        }
                                    >
                                        Delete
                                    </button>

                                </div>

                            </div>

                        ))
                    )}
                    {hasMore && (
    <button
        className="load-more-button"
        onClick={onLoadMore}
    >
        Load More
    </button>
)}

                </div>

            </section>

            <ConfirmDialog
                open={deleteId !== null}
                title="Delete Measurement"
                message="Are you sure you want to delete this measurement?"
                confirmText="Delete"
                cancelText="Cancel"
                onConfirm={handleConfirmDelete}
                onCancel={handleCancelDelete}
            />
        </>
    );
}

export default MeasurementHistory;