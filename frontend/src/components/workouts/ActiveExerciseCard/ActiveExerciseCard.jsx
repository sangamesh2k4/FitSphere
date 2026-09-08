import "../ActiveExerciseCard/ActiveExerciseCard.css";
function ActiveExerciseCard({ exercise }) {

    return (
        <article className="active-exercise-card">

            <div className="active-exercise-card-header">

                <div>
                    <h2>
                        {exercise.exerciseName}
                    </h2>

                    <span>
                        {exercise.totalVolume ?? 0} kg volume
                    </span>
                </div>

            </div>


            <div className="active-exercise-sets">

                <div className="active-exercise-sets-header">
                    <span>SET</span>
                    <span>WEIGHT</span>
                    <span>REPS</span>
                </div>


                {exercise.sets?.map(set => (

                    <div
                        className="active-exercise-set"
                        key={set.id}
                    >

                        <span>
                            {set.setNumber}
                        </span>

                        <span>
                            {set.weight} kg
                        </span>

                        <span>
                            {set.reps}
                        </span>

                    </div>

                ))}

            </div>

        </article>
    );
}

export default ActiveExerciseCard;