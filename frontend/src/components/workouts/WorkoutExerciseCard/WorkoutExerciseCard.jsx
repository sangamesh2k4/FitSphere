import { toast } from 'react-hot-toast';
import { useState } from 'react';
import { workoutService } from '../../../services/workoutService';
import AddSetForm from '../AddSetForm/AddSetForm';
import WorkoutSetRow from '../WorkoutSetRow/WorkoutSetRow';
import { formatNumber } from '../../../utils';
import './WorkoutExerciseCard.css'; 

function WorkoutExerciseCard({
    exercise,
    workoutId,
    onUpdate,
    onDragStart,
    onDragOver,
    onDrop,
    isReordering
}) {
    const [loadingAction, setLoadingAction] = useState(false);
    
    // Manage collapse/expand state
    const [isCollapsed, setIsCollapsed] = useState(false);
    
    // Manage modal popup state
    const [confirmPrompt, setConfirmPrompt] = useState({
        visible: false,
        message: '',
        onConfirm: null
    });

    const totalVolume = (exercise.sets ?? []).reduce(
        (total, set) => total + (set.volume ?? 0),
        0
    );

    const requestConfirm = (message, onConfirmCallback) => {
        setConfirmPrompt({ visible: true, message, onConfirm: onConfirmCallback });
    };

    const cancelConfirm = () => {
        setConfirmPrompt({ visible: false, message: '', onConfirm: null });
    };

    const handleSetUpdate = async (setId, weight, reps) => {
        if (loadingAction) return;
        setLoadingAction(true);
        try {
            await workoutService.updateSet(workoutId, exercise.id, setId, { weight, reps });
            await onUpdate();
            toast.success('Set updated successfully');
        } catch  {
            toast.error("Failed to update set.");
        } finally {
            setLoadingAction(false);
        }
    };

    const handleRemoveExercise = () => {
        if (loadingAction) return;
        requestConfirm(`Remove ${exercise.exerciseName} from workout?`, async () => {
            setLoadingAction(true);
            try {
                await workoutService.removeExercise(workoutId, exercise.id);
                onUpdate();
                toast.success(`${exercise.exerciseName} removed`);
            } catch {
                toast.error("Failed to remove exercise.");
            } finally {
                setLoadingAction(false);
                cancelConfirm();
            }
        });
    };

    const handleDeleteSetRequest = (setId) => {
        if (loadingAction) return;
        requestConfirm("Are you sure you want to delete this set?", async () => {
            setLoadingAction(true);
            try {
                await workoutService.deleteSet(workoutId, exercise.id, setId);
                onUpdate();
                toast.success("Set deleted");
            } catch  {
                toast.error("Failed to delete set.");
            } finally {
                setLoadingAction(false);
                cancelConfirm();
            }
        });
    };

    return (
        <div 
            className="workout-exercise-card"
            onDragOver={onDragOver}
            onDrop={() => onDrop(exercise.id)}
        >
            {/* Popup Modal Overlay */}
            {confirmPrompt.visible && (
                <div className="confirm-modal-overlay">
                    <div className="confirm-modal">
                        <p>{confirmPrompt.message}</p>
                        <div className="confirm-modal-actions">
                            <button 
                                type="button"
                                className="btn-modal-cancel" 
                                onClick={cancelConfirm}
                                disabled={loadingAction}
                            >
                                Cancel
                            </button>
                            <button 
                                type="button"
                                className="btn-modal-confirm" 
                                onClick={confirmPrompt.onConfirm}
                                disabled={loadingAction}
                            >
                                {loadingAction ? 'Processing...' : 'Confirm'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Header */}
            <div className="exercise-card-header">
                <div className="exercise-card-title" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <button
                        type="button"
                        className="exercise-drag-handle"
                        draggable={!isReordering}
                        onDragStart={() => onDragStart(exercise.id)}
                        aria-label="Drag to reorder exercise"
                        title="Drag to reorder"
                    >
                        ⋮⋮
                    </button>

                    <h3>
                        <span className="exercise-order">{exercise.exerciseOrder}.</span>
                        {exercise.exerciseName}
                    </h3>
                </div>
                
                {/* Header Actions grouped together */}
                <div className="header-actions" style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                    <button
                        type="button"
                        className="icon-button"
                        onClick={() => setIsCollapsed(!isCollapsed)}
                        disabled={loadingAction}
                        aria-label={isCollapsed ? "Expand exercise details" : "Collapse exercise details"}
                        style={{ 
                            background: 'transparent', 
                            border: 'none', 
                            cursor: 'pointer', 
                            fontSize: '1.2rem', 
                            color: 'var(--fs-text-secondary, #aaa)' 
                        }}
                    >
                        {isCollapsed ? '▼' : '▲'}
                    </button>
                    
                    <button
                        type="button"
                        className="icon-button danger"
                        onClick={handleRemoveExercise}
                        disabled={loadingAction}
                        aria-label="Remove exercise"
                    >
                        ✕
                    </button>
                </div>
            </div>

            {/* Collapsible Content: Existing Sets */}
            {!isCollapsed && (
                <div className="sets-container">
                    {exercise.sets?.map((set, index) => (
                        <WorkoutSetRow
                            key={set.id}
                            set={{ ...set, setNumber: set.setNumber ?? index + 1 }}
                            onUpdate={handleSetUpdate}
                            onDeleteRequest={handleDeleteSetRequest}
                        />
                    ))}
                </div>
            )}

            {/* Add Set Form (Always Visible) */}
            <AddSetForm
                disabled={loadingAction}
                onAdd={async (weight, reps) => {
                    setLoadingAction(true);
                    try {
                        await workoutService.addSet(workoutId, exercise.id, { weight, reps });
                        onUpdate();
                        toast.success('Set added');
                        
                        if (isCollapsed) setIsCollapsed(false); 
                    } catch {
                        toast.error("Failed to add set.");
                    } finally {
                        setLoadingAction(false);
                    }
                }}
            />

            {/* Collapsible Content: Footer */}
            {!isCollapsed && (
                <div className="exercise-card-footer">
                    <span className="exercise-volume">
                        Volume: {formatNumber(totalVolume)} kg
                    </span>
                </div>
            )}
        </div>
    );
}

export default WorkoutExerciseCard;