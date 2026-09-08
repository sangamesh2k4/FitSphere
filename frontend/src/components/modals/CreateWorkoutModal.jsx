import { useState } from "react"

function CreateWorkoutModal({
    isOpen,
    onClose,
    onCreate
}) {

    const [name, setName] = useState("")

    if (!isOpen) return null

    return (

        <div className="modal-overlay">

            <div className="modal">

                <h2>Create Workout</h2>

                <input
                    type="text"
                    placeholder="Workout Name"
                    value={name}
                    onChange={(e)=>setName(e.target.value)}
                />

                <div className="modal-actions">

                    <button
                        className="secondary-button"
                        onClick={onClose}
                    >
                        Cancel
                    </button>

                    <button
                        className="primary-button"
                        disabled={!name.trim()}
                        onClick={()=>{
                            onCreate(name)
                            setName("")
                        }}
                    >
                        Create Workout
                    </button>

                </div>

            </div>

        </div>

    )

}

export default CreateWorkoutModal