import { useEffect, useState, useContext } from "react";
import { exerciseService } from "../../../services/exerciseService";
import { formatEnum } from "../../../utils/formatEnum";

import { useAuth } from "../../../hooks/useAuth";
import { useFavorites } from "../../../hooks/useFavorites";
import { AuthModalContext } from "../../../context/AuthModalContext";

import { ExerciseBadge } from "../../ExerciseBadge";
import { ExerciseMedia } from "../ExerciseMedia/ExerciseMedia";
import { ExerciseMetadata } from "../ExerciseMetadata/ExerciseMetadata";
import { InstructionList } from "../InstructionList/InstructionList";
import { InfoListCard } from "../InfoListCard/InfoListCard";
import { VideoCard } from "../VideoCard/VideoCard";

import { ExerciseDetailSkeleton } from "../../skeleton/ExerciseDetailSkeleton";

import "../ExerciseDetailModal/ExerciseDetailModal.css";

function ExerciseDetailModal({ exerciseId, onClose, onAddToWorkout }) {
    const [exercise, setExercise] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    const { isAuthenticated } = useAuth();
    const { openLogin } = useContext(AuthModalContext);

    const { isExerciseFavorite, toggleExerciseFavorite } = useFavorites();

    const isFavorite = exercise && isExerciseFavorite(exercise.id);

    // Prevent background scrolling when modal is open
    useEffect(() => {
        document.body.style.overflow = "hidden";
        return () => {
            document.body.style.overflow = "auto";
        };
    }, []);

    // Fetch exercise details
    useEffect(() => {
        const fetchExercise = async () => {
            try {
                setLoading(true);
                setError(false);
                const data = await exerciseService.getExerciseById(exerciseId);
                setExercise(data);
            } catch (err) {
                console.error("Failed to load exercise details", err);
                setError(true);
            } finally {
                setLoading(false);
            }
        };

        if (exerciseId) {
            fetchExercise();
        }
    }, [exerciseId]);

    const handleToggleFavorite = async () => {
        if (!isAuthenticated) {
            openLogin();
            return;
        }
        await toggleExerciseFavorite(exercise.id);
    };

    return (
        <div className="exercise-detail-modal-overlay" onClick={onClose}>
            <div className="exercise-detail-modal" onClick={(e) => e.stopPropagation()}>
                
                {/* Close Button Container */}
                <div className="modal-close-container">
                    <button
                        type="button"
                        className="exercise-detail-modal-close"
                        onClick={onClose}
                        aria-label="Close modal"
                    >
                        Close
                    </button>
                </div>

                {/* SKELETON LOADING STATE */}
                {loading && <ExerciseDetailSkeleton />}

                {/* ERROR STATE */}
                {error && (
                    <div className="exercise-detail-modal-error">
                        <h2>Something went wrong</h2>
                        <p>We couldn't load this exercise.</p>
                    </div>
                )}

                {/* LOADED STATE */}
                {!loading && !error && exercise && (
                    <main className="exercise-detail-wrapper">
                        {/* Header Section */}
                        <header className="detail-header">
                            {exercise.category && (
                                <p className="exercise-category">
                                    {formatEnum(exercise.category)}
                                </p>
                            )}

                            <div className="title-row">
                                <h1>{exercise.name}</h1>

                                <button
                                    type="button"
                                    className={`favorite-btn ${isFavorite ? "is-favorite" : ""}`}
                                    onClick={handleToggleFavorite}
                                >
                                    <i
                                        className={
                                            isFavorite
                                                ? "ti ti-heart-filled"
                                                : "ti ti-heart"
                                        }
                                    ></i>
                                    {isFavorite
                                        ? "Remove from Favorites"
                                        : "Add to Favorites"}
                                </button>
                            </div>

                            <div className="badge-group">
                                {exercise.difficulty && (
                                    <ExerciseBadge
                                        label={formatEnum(exercise.difficulty)}
                                        type="auto"
                                    />
                                )}
                                {exercise.equipment && (
                                    <ExerciseBadge
                                        label={formatEnum(exercise.equipment)}
                                        type="info"
                                    />
                                )}
                                {exercise.exerciseType && (
                                    <ExerciseBadge
                                        label={formatEnum(exercise.exerciseType)}
                                        type="info"
                                    />
                                )}
                            </div>

                            {/* Optional Action Button when opened from AddExerciseModal */}
                            {onAddToWorkout && (
                                <div className="exercise-detail-actions">
                                    <button
                                        type="button"
                                        className="add-to-workout-button"
                                        onClick={() => onAddToWorkout(exercise.id)}
                                    >
                                        Add to Workout
                                    </button>
                                </div>
                            )}
                        </header>

                        {/* Main Grid Content */}
                        <section className="detail-main">
                            <div className="detail-grid">
                                <aside className="detail-sidebar">
                                    <ExerciseMedia
                                        videoUrl={exercise.videoUrl}
                                        imageUrl={exercise.imageUrl}
                                        altText={exercise.name}
                                    />
                                    <ExerciseMetadata exercise={exercise} />
                                </aside>

                                <section className="detail-content">
                                    {exercise.description && (
                                        <div className="content-card">
                                            <h2 className="card-heading">DESCRIPTION</h2>
                                            <p className="card-text">{exercise.description}</p>
                                        </div>
                                    )}

                                    <InstructionList instructions={exercise.instructions} />

                                    {(exercise.tips?.length > 0 ||
                                        exercise.commonMistakes?.length > 0) && (
                                        <div className="tips-mistakes-grid">
                                            {exercise.tips?.length > 0 && (
                                                <InfoListCard
                                                    title="PRO TIPS"
                                                    icon="bulb"
                                                    colorClass="icon-green"
                                                    items={exercise.tips}
                                                />
                                            )}
                                            {exercise.commonMistakes?.length > 0 && (
                                                <InfoListCard
                                                    title="MISTAKES TO AVOID"
                                                    icon="alert-triangle"
                                                    colorClass="icon-red"
                                                    items={exercise.commonMistakes}
                                                />
                                            )}
                                        </div>
                                    )}
                                </section>
                            </div>
                        </section>

                        {/* Videos Section */}
                        {exercise.recommendedVideos?.length > 0 && (
                            <section className="videos-section">
                                <h2 className="card-heading">RECOMMENDED VIDEOS</h2>
                                <div className="videos-grid">
                                    {exercise.recommendedVideos.map((video) => (
                                        <VideoCard
                                            key={video.id || video.videoUrl}
                                            video={video}
                                        />
                                    ))}
                                </div>
                            </section>
                        )}
                    </main>
                )}
            </div>
        </div>
    );
}

export default ExerciseDetailModal;