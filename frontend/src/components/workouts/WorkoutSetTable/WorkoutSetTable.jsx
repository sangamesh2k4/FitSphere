import WorkoutSetRow from "./WorkoutSetRow";

function WorkoutSetTable({ sets, onUpdate, onDelete }) {

    return (
        <div className="workout-sets">

            <div className="set-header">
                <span>Set</span>
                <span>Weight</span>
                <span>Reps</span>
                <span>Volume</span>
                <span>1RM</span>
                <span>PR</span>
                <span></span>
            </div>

            {sets?.map((set) => (
                <WorkoutSetRow
                    key={set.id}
                    set={set}
                    onUpdate={onUpdate}
                    onDelete={onDelete}
                />
            ))}

        </div>
    );
}

export default WorkoutSetTable;