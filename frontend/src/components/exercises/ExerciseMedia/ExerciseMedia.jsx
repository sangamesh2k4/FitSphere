import "./ExerciseMedia.css";

export function ExerciseMedia({ videoUrl, imageUrl, altText }) {
    
  return (
    <div className="hero-media-card">
      {videoUrl ? (
        <video 
          src={videoUrl} 
          autoPlay 
          loop 
          muted 
          playsInline 
          className="detail-media" 
        />
      ) : imageUrl ? (
        <img 
          src={imageUrl} 
          alt={altText || 'Exercise Media'} 
          className="detail-media"
        />
      ) : (
        <div className="image-placeholder">No Media Available</div>
      )}
    </div>
  );
}