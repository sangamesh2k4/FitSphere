import { useState } from 'react';
import "../WorkoutSetRow/WorkoutSetRow.css";

function WorkoutSetRow({ set, onUpdate, onDeleteRequest }) {
    const [weight, setWeight] = useState(set.weight ?? '');
    const [reps, setReps] = useState(set.reps ?? '');
    const [isEditing, setIsEditing] = useState(false);

    const handleSave = () => {
        if (weight !== '' && reps !== '') {
            onUpdate(set.id, Number(weight), Number(reps));
            setIsEditing(false); // Exit edit mode after saving
        }
    };

    return (
        <div className={`set-card ${isEditing ? 'editing' : ''}`}>
            <div className="set-card-header">
                <div className="set-title-group">
                    <span className="set-title">Set {set.setNumber}</span>
                    {set.isPR && <span className="set-badge pr-badge">PR</span>}
                </div>
                
                {/* Edit Toggle Button */}
                <button 
                    type="button" 
                    className="btn-edit-toggle"
                    onClick={() => setIsEditing(!isEditing)}
                >
                    {isEditing ? 'Cancel' : 'Edit'}
                </button>
            </div>

            <div className="set-card-inputs">
                <div className="input-group">
                    <label>WEIGHT</label>
                    <input 
                        type="number" 
                        value={weight} 
                        onChange={(e) => setWeight(e.target.value)}
                        placeholder="0"
                        disabled={!isEditing} // Inputs are locked until Edit is clicked
                    />
                </div>
                <div className="input-group">
                    <label>REPS</label>
                    <input 
                        type="number" 
                        value={reps} 
                        onChange={(e) => setReps(e.target.value)}
                        placeholder="0"
                        disabled={!isEditing}
                    />
                </div>
            </div>

            <div className="set-card-metrics">
                <span>Vol <strong>{set.volume ?? 0}</strong></span>
                <span>1RM <strong>{set.estimatedOneRepMax ?? "-"}</strong></span>
            </div>

            {/* Conditionally Render Action Buttons */}
            {isEditing && (
                <div className="set-card-actions">
                    <button type="button" className="btn-save-set" onClick={handleSave}>
                        Save
                    </button>
                    <button type="button" className="btn-delete-set" onClick={() => onDeleteRequest(set.id)}>
                        Delete
                    </button>
                </div>
            )}
        </div>
    );
}

export default WorkoutSetRow;