import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import AdminExerciseCard from "../../components/admin/AdminExerciseCard/AdminExerciseCard";
import adminService from "../../services/adminService";
import Select from "../../components/common/CustomSelect";

import "../../css/admin/AdminExercises.css";

const CATEGORY_OPTIONS = [
    { value: "", label: "All Categories" },
    { value: "CHEST", label: "Chest" },
    { value: "BACK", label: "Back" },
    { value: "SHOULDERS", label: "Shoulders" },
    { value: "BICEPS", label: "Biceps" },
    { value: "TRICEPS", label: "Triceps" },
    { value: "LEGS", label: "Legs" },
    { value: "CORE", label: "Core" },
    { value: "CARDIO", label: "Cardio" },
    { value: "FUNCTIONAL", label: "Functional" },
    { value: "FULL_BODY", label: "Full Body" }
];

const DIFFICULTY_OPTIONS = [
    { value: "", label: "All Difficulties" },
    { value: "BEGINNER", label: "Beginner" },
    { value: "INTERMEDIATE", label: "Intermediate" },
    { value: "ADVANCED", label: "Advanced" }
];

const EQUIPMENT_OPTIONS = [
    { value: "", label: "All Equipment" },
    { value: "BARBELL", label: "Barbell" },
    { value: "DUMBBELL", label: "Dumbbell" },
    { value: "CABLE", label: "Cable" },
    { value: "MACHINE", label: "Machine" },
    { value: "BODYWEIGHT", label: "Bodyweight" },
    { value: "SMITH_MACHINE", label: "Smith Machine" },
    { value: "EZ_BAR", label: "EZ Bar" },
    { value: "KETTLEBELL", label: "Kettlebell" },
    { value: "MEDICINE_BALL", label: "Medicine Ball" },
    { value: "TRX", label: "TRX" },
    { value: "RESISTANCE_BAND", label: "Resistance Band" },
    { value: "WEIGHT_PLATE", label: "Weight Plate" },
    { value: "AB_WHEEL", label: "Ab Wheel" },
    { value: "SLED", label: "Sled" },
    { value: "BATTLE_ROPE", label: "Battle Rope" },
    { value: "SANDBAG", label: "Sandbag" },
    { value: "LANDMINE", label: "Landmine" }
];

const TYPE_OPTIONS = [
    { value: "", label: "All Types" },
    { value: "COMPOUND", label: "Compound" },
    { value: "ISOLATION", label: "Isolation" }
];

const STATUS_OPTIONS = [
    { value: "", label: "All Status" },
    { value: "true", label: "Active" },
    { value: "false", label: "Disabled" }
];

function AdminExercises() {
    const [exercises, setExercises] = useState([]);
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);
    const [totalElements, setTotalElements] = useState(0);
    const [primaryMuscleOptions, setPrimaryMuscleOptions] = useState([]);
    const [musclesLoading, setMusclesLoading] = useState(false);
    const [, setPage] = useState(0);
    const [search, setSearch] = useState("");
    
    const [filters, setFilters] = useState({
        category: "",
        primaryMuscle: "",
        difficulty: "",
        equipment: "",
        exerciseType: "",
        active: ""
    });

    const observer = useRef(null);
    const fetchingRef = useRef(false);
    const navigate = useNavigate();

    const fetchExercises = useCallback(async (pageNumber, reset = false) => {
        if (fetchingRef.current) return;

        try {
            fetchingRef.current = true;
            setLoading(true);

            const data = await adminService.getAllExercises({
                page: pageNumber,
                keyword: search,
                ...filters
            });

            setExercises(prev => {
                if (reset) {
                    return data.content;
                }

                const existingIds = new Set(
                    prev.map(exercise => exercise.id)
                );

                const newExercises = data.content.filter(
                    exercise => !existingIds.has(exercise.id)
                );

                return [...prev, ...newExercises];
            });

            if (data.totalElements !== undefined) {
                setTotalElements(data.totalElements);
            }

            setHasMore(!data.last);
        } catch (error) {
            console.error("Failed to load exercises:", error);
        } finally {
            fetchingRef.current = false;
            setLoading(false);
        }
    }, [search, filters]);


const handleToggleExercise = async (exercise) => {
    try {
        if (exercise.active) {
            await adminService.disableExercise(exercise.id);
        } else {
            await adminService.enableExercise(exercise.id);
        }

        setExercises(prev =>
            prev.map(ex =>
                ex.id === exercise.id
                    ? { ...ex, active: !ex.active }
                    : ex
            )
        );

    } catch (error) {
        console.error("Failed to toggle exercise:", error);
    }
};


    useEffect(() => {
        const fetchPrimaryMuscles = async () => {
            if (!filters.category) {
                setPrimaryMuscleOptions([]);
                return;
            }

            try {
                setMusclesLoading(true);

                const data = await adminService.getPrimaryMusclesByCategory(filters.category);

                setPrimaryMuscleOptions(
                    data.muscles.map(muscle => ({
                        value: muscle,
                        label: muscle
                            .toLowerCase()
                            .replace(/_/g, " ")
                            .replace(/\b\w/g, char => char.toUpperCase())
                    }))
                );
            } catch (error) {
                console.error("Failed to load primary muscles:", error);
                setPrimaryMuscleOptions([]);
            } finally {
                setMusclesLoading(false);
            }
        };

        fetchPrimaryMuscles();
    }, [filters.category]);

    useEffect(() => {
        setPage(0);
        setExercises([]);
        setHasMore(true);
        fetchExercises(0, true);
    }, [search, filters, fetchExercises]);

    const lastExerciseRef = useCallback((node) => {
        if (loading || !hasMore) return;

        if (observer.current) {
            observer.current.disconnect();
        }

        observer.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting && !loading && hasMore) {
                setPage(prev => {
                    const nextPage = prev + 1;
                    fetchExercises(nextPage);
                    return nextPage;
                });
            }
        });

        if (node) {
            observer.current.observe(node);
        }
    }, [loading, hasMore, fetchExercises]);

    const handleAddExercise = () => {
        navigate("/admin/exercises/add");
    };

    const handleEditExercise = (exercise) => {
        navigate(`/admin/exercises/edit/${exercise.id}`);
    };

    const handleFilterChange = (name, eventOrValue) => {
        const value = eventOrValue?.target !== undefined
            ? eventOrValue.target.value
            : eventOrValue;

        setFilters(prev => ({
            ...prev,
            [name]: value,
            ...(name === "category" && { primaryMuscle: "" })
        }));
    };

    return (
        <section className="admin-exercises-page">
            <header className="admin-page-header">
                <h1 className="admin-page-title">Exercises</h1>
                <p className="admin-page-subtitle">Manage your exercise library.</p>
            </header>

            <div className="admin-controls-panel">
                <div className="admin-search-wrapper">
                    <input
                        type="text"
                        className="admin-search-input"
                        placeholder="Search exercises..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                </div>

                <div className="admin-filters-row">
                    <Select
                        className="admin-filter-select"
                        value={filters.category}
                        options={CATEGORY_OPTIONS}
                        onChange={(val) => handleFilterChange("category", val)}
                        placeholder="All Categories"
                    />

                    <Select
                        className="admin-filter-select"
                        value={filters.primaryMuscle}
                        options={[
                            {
                                value: "",
                                label: musclesLoading
                                    ? "Loading Muscles..."
                                    : "All Primary Muscles"
                            },
                            ...primaryMuscleOptions
                        ]}
                        onChange={(val) => handleFilterChange("primaryMuscle", val)}
                        placeholder="All Primary Muscles"
                    />

                    <Select
                        className="admin-filter-select"
                        value={filters.difficulty}
                        options={DIFFICULTY_OPTIONS}
                        onChange={(val) => handleFilterChange("difficulty", val)}
                        placeholder="All Difficulties"
                    />

                    <Select
                        className="admin-filter-select"
                        value={filters.equipment}
                        options={EQUIPMENT_OPTIONS}
                        onChange={(val) => handleFilterChange("equipment", val)}
                        placeholder="All Equipment"
                    />

                    <Select
                        className="admin-filter-select"
                        value={filters.exerciseType}
                        options={TYPE_OPTIONS}
                        onChange={(val) => handleFilterChange("exerciseType", val)}
                        placeholder="All Types"
                    />

                    <Select
                        className="admin-filter-select"
                        value={filters.active}
                        options={STATUS_OPTIONS}
                        onChange={(val) => handleFilterChange("active", val)}
                        placeholder="All Status"
                    />
                </div>

                <div className="admin-actions-row">
                    <button type="button" className="admin-btn-add" onClick={handleAddExercise}>
                        + Add Exercise
                    </button>
                </div>
            </div>

            <div className="admin-list-header">
                <span className="admin-results-count">
                    {totalElements > 0 ? totalElements : exercises.length} EXERCISES
                </span>
            </div>

            <div className="admin-list-container">
                {exercises.map((exercise, index) => {
                    const isLast = index === exercises.length - 1;
                    return (
                        <div key={exercise.id} ref={isLast ? lastExerciseRef : null}>
                            <AdminExerciseCard
                                exercise={exercise}
                                onEdit={handleEditExercise}
                                onToggle={handleToggleExercise}
                            />
                        </div>
                    );
                })}

                {loading && (
                    <div className="admin-loading-state">Loading exercises...</div>
                )}

                {!loading && exercises.length === 0 && (
                    <div className="admin-empty-state">No exercises found.</div>
                )}
            </div>
        </section>
    );
}

export default AdminExercises;