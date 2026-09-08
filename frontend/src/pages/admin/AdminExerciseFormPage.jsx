import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import ExerciseForm from "../../components/admin/ExerciseForm/ExerciseForm";
import  adminService from "../../services/adminService";
import "../../css/admin/AdminExerciseFormPage.css";
import LoadingSkeleton from "../../components/skeleton/LoadingSkeleton/LoadingSkeleton";

function AdminExerciseFormPage() {
    const navigate = useNavigate();
    const { id } = useParams();

    const isEdit = Boolean(id);

    const [exercise, setExercise] = useState(null);
    const [loading, setLoading] = useState(isEdit);

    useEffect(() => {
        if (!isEdit) {
            return;
        }

        const fetchExercise = async () => {
            try {
                setLoading(true);

                const data = await adminService.getExerciseById(id);        

                setExercise(data);
            } catch (error) {
                console.error("Failed to load exercise:", error);
                navigate("/admin/exercises");
            } finally {
                setLoading(false);
            }
        };

        fetchExercise();
    }, [id, isEdit, navigate]);

    if (loading) {
        return (
            <LoadingSkeleton />
        );
    }

    return (
        <section className="admin-exercise-form-page">

            <div className="admin-exercise-form-page-header">
                <button
                    type="button"
                    onClick={() => navigate("/admin/exercises")}
                >
                    ← Back to Exercises
                </button>
            </div>

            <ExerciseForm
                mode={isEdit ? "edit" : "add"}
                exercise={exercise}
                onSuccess={() => navigate("/admin/exercises")}
                onClose={() => navigate("/admin/exercises")}
            />

        </section>
    );
}

export default AdminExerciseFormPage;