import { useCallback, useEffect, useRef, useState, useMemo } from "react";
import { exerciseService } from "../../../services/exerciseService";
import { formatEnum } from "../../../utils/formatEnum";
import ExerciseDetailModal from "../../exercises/ExerciseDetailModal/ExerciseDetailModal";
import "../AddExerciseModal/AddExerciseModal.css";
import CustomSelect from "../../common/CustomSelect";

function AddExerciseModal({ workoutId, workoutExercises = [], onExerciseAdded, onClose }) {
    const [exercises, setExercises] = useState([]);
    const [search, setSearch] = useState("");
    const [equipment, setEquipment] = useState("");
    const [category, setCategory] = useState("");
    const [difficulty, setDifficulty] = useState("");
    const [metadata, setMetadata] = useState(null);
    
    // Pagination & Loading States
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);
    const [totalExercises, setTotalExercises] = useState(0);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    const [addingId, setAddingId] = useState(null);
    const [viewExerciseId, setViewExerciseId] = useState(null);
    const [error, setError] = useState("");

    const observerRef = useRef(null);
    const requestIdRef = useRef(0);

    // OPTIMIZATION: Blazing fast O(1) lookup Set for added exercises
    const addedExerciseIds = useMemo(() => {
        const ids = new Set();
        workoutExercises?.forEach(we => {
            if (we.exerciseId) ids.add(we.exerciseId);
            if (we.exercise?.id) ids.add(we.exercise?.id);
        });
        return ids;
    }, [workoutExercises]);

    // Transform equipment metadata for custom select
    const equipmentOptions = [
        { label: "All Equipment", value: "" },
        ...(metadata?.equipment || []).map(item => ({
            value: item,
            label: formatEnum(item)
        }))
    ];

    // Transform category metadata for custom select
    const categoryOptions = [
        { label: "All Categories", value: "" },
        ...(metadata?.categories || []).map(item => ({
            value: item,
            label: formatEnum(item)
        }))
    ];

    // Lock background scrolling while modal is active
    useEffect(() => {
        document.body.style.overflow = "hidden";
        return () => {
            document.body.style.overflow = "auto";
        };
    }, []);

    // Load exercise metadata
    useEffect(() => {
        const loadMetadata = async () => {
            try {
                const data = await exerciseService.getExerciseMetadata();
                setMetadata(data);
            } catch (error) {
                console.error("Failed to load exercise metadata", error);
            }
        };
        loadMetadata();
    }, []);

    // Fetch exercises with pagination & race-condition protection
    const fetchExercises = useCallback(
        async (pageNumber = 0, append = false) => {
            const currentRequestId = ++requestIdRef.current;

            if (append) {
                setIsLoadingMore(true);
            } else {
                setIsInitialLoading(true);
                setHasMore(true);
                setPage(0);
            }

            const filterParams = {
                page: pageNumber,
                ...(search.trim() ? { keyword: search.trim() } : {}),
                ...(equipment ? { equipment } : {}),
                ...(difficulty ? { difficulty } : {}),
                ...(category ? { category } : {})
            };

            try {
                const data = await exerciseService.filterExercises(filterParams);

                // Ignore stale request
                if (currentRequestId !== requestIdRef.current) {
                    return;
                }

                setExercises(prev =>
                    append
                        ? [...prev, ...(data.content || [])]
                        : (data.content || [])
                );

                setTotalExercises(data.totalElements || 0);
                setPage(data.number ?? pageNumber);
                setHasMore(!data.last);
            } catch (error) {
                if (currentRequestId === requestIdRef.current) {
                    console.error("Failed to fetch exercises:", error);
                    setError("Failed to load exercises.");
                }
            } finally {
                if (currentRequestId === requestIdRef.current) {
                    setIsInitialLoading(false);
                    setIsLoadingMore(false);
                }
            }
        },
        [
            search,
            equipment,
            difficulty,
            category
        ]
    );

    // Debounced search/filter reset
    useEffect(() => {
        const timer = setTimeout(() => {
            fetchExercises(0, false);
        }, 500);

        return () => clearTimeout(timer);
    }, [
        search,
        equipment,
        difficulty,
        category,
        fetchExercises
    ]);

    // Infinite Scroll Intersection Observer Callback Ref
    const lastElementRef = useCallback((node) => {
        if (isInitialLoading || isLoadingMore || !hasMore) return;

        if (observerRef.current) observerRef.current.disconnect();

        observerRef.current = new IntersectionObserver((entries) => {
            if (entries[0].isIntersecting && hasMore) {
                fetchExercises(page + 1, true);
            }
        }, { rootMargin: "200px" });

        if (node) observerRef.current.observe(node);
    }, [isInitialLoading, isLoadingMore, hasMore, page, fetchExercises]);

    // Add exercise to workout
    const handleAddExercise = async (exerciseId, exerciseName) => {
        try {
            setAddingId(exerciseId);
            setError("");
            await onExerciseAdded(exerciseId, exerciseName);
        } catch (error) {
            console.error("Failed to add exercise", error);
            setError("Failed to add exercise.");
        } finally {
            setAddingId(null);
        }
    };

    // Clear filters
    const clearFilters = () => {
        setSearch("");
        setEquipment("");
        setCategory("");
        setDifficulty("");
    };

    const hasFilters = search.trim() !== "" || equipment !== "" || category !== "" || difficulty !== "";

    return (
        <div
            className="add-exercise-modal-overlay"
            onMouseDown={(e) => {
                if (e.target === e.currentTarget) {
                    onClose();
                }
            }}
        >
            <div 
                className="add-exercise-modal"
                role="dialog" 
                aria-modal="true"
                aria-labelledby="modal-title"
            >
                {/* Header */}
                <div className="add-exercise-modal-header">
                    <div>
                        <h2 id="modal-title">Add Exercise</h2>
                        <p>Choose an exercise to add to your workout.</p>
                    </div>
                    <button 
                        type="button" 
                        className="add-exercise-modal-close" 
                        onClick={onClose}
                        aria-label="Close modal"
                    >
                        ✕
                    </button>
                </div>

                {/* Search */}
                <div className="add-exercise-search">
                    <input
                        type="text"
                        placeholder="Search exercises..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        autoFocus
                    />
                </div>

                {/* Filters */}
                <div className="add-exercise-filters">
                    <CustomSelect
                        value={equipment}
                        onChange={(e) => setEquipment(e.target.value)}
                        options={equipmentOptions}
                        placeholder="All Equipment"
                    />

                    <CustomSelect
                        value={category}
                        onChange={(e) => setCategory(e.target.value)}
                        options={categoryOptions}
                        placeholder="All Categories"
                    />

                    {hasFilters && (
                        <button type="button" className="clear-exercise-filters" onClick={clearFilters}>
                            Clear
                        </button>
                    )}
                </div>

                {/* Error */}
                {error && <p className="add-exercise-error" role="alert">{error}</p>}

                {/* Results */}
                <div className="add-exercise-results">
                    {isInitialLoading && exercises.length === 0 ? (
                        <div className="add-exercise-grid">
                            {[...Array(6)].map((_, index) => (
                                <div className="add-exercise-card skeleton-card" key={index}>
                                    <div className="add-exercise-card-image skeleton-pulse"></div>
                                    <div className="add-exercise-card-content">
                                        <div className="skeleton-line skeleton-title skeleton-pulse"></div>
                                        <div className="add-exercise-card-meta">
                                            <div className="skeleton-line skeleton-badge skeleton-pulse"></div>
                                            <div className="skeleton-line skeleton-badge skeleton-pulse"></div>
                                        </div>
                                    </div>
                                    <div className="add-exercise-card-actions">
                                        <div className="skeleton-line skeleton-btn skeleton-pulse"></div>
                                        <div className="skeleton-line skeleton-btn skeleton-pulse"></div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : exercises.length === 0 ? (
                        <div className="add-exercise-empty">
                            <h3>No Exercises Found</h3>
                            <p>Try changing your search or filters.</p>
                        </div>
                    ) : (
                        <div className="add-exercise-grid">
                            {exercises.map((exercise, index) => {
                                const alreadyAdded = addedExerciseIds.has(exercise.id);
                                const isLastElement = index === exercises.length - 1;

                                return (
                                    <div 
                                        className="add-exercise-card" 
                                        key={exercise.id}
                                        ref={isLastElement ? lastElementRef : null}
                                    >
                                        <div className="add-exercise-card-image">
                                            {exercise.imageUrl ? (
                                                <img src={exercise.imageUrl} alt={exercise.name} />
                                            ) : (
                                                <div className="placeholder-icon">🏋️</div>
                                            )}
                                        </div>

                                        <div className="add-exercise-card-content">
                                            <h3>{exercise.name}</h3>
                                            <div className="add-exercise-card-meta">
                                                {exercise.category && (
                                                    <span>{formatEnum(exercise.category)}</span>
                                                )}
                                                {exercise.equipment && (
                                                    <span>{formatEnum(exercise.equipment)}</span>
                                                )}
                                            </div>
                                        </div>
                                        
                                        <div className="add-exercise-card-actions">
                                            <button
                                                type="button"
                                                className="view-exercise-button"
                                                onClick={() => setViewExerciseId(exercise.id)}
                                            >
                                                View Details
                                            </button>
                                            <button
                                                type="button"
                                                className="add-exercise-button"
                                                disabled={addingId === exercise.id || alreadyAdded}
                                                onClick={() => handleAddExercise(exercise.id, exercise.name)}
                                            >
                                                {addingId === exercise.id
                                                    ? "Adding..."
                                                    : alreadyAdded
                                                    ? "Added"
                                                    : "Add"}
                                            </button>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}

                    {/* Loading more indicator */}
                    {isLoadingMore && (
                        <div className="add-exercise-loading-more" style={{ textAlign: "center", padding: "1rem" }}>
                            <span>Loading more exercises...</span>
                        </div>
                    )}
                </div>
            </div>

            {/* Exercise Detail Modal Overlay */}
            {viewExerciseId && (
                <ExerciseDetailModal
                    exerciseId={viewExerciseId}
                    onClose={() => setViewExerciseId(null)}
                    onAddToWorkout={(id) => {
                        const selectedEx = exercises.find(e => e.id === id);
                        handleAddExercise(id, selectedEx?.name);
                    }}
                />
            )}
        </div>
    );
}

export default AddExerciseModal;