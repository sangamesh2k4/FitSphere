import "./videoCard.css"

export function VideoCard({ video }) {
  if (!video) return null;

  return (
    <a 
      href={video.videoUrl} 
      target="_blank" 
      rel="noreferrer" 
      className="video-card"
    >
      <div className="video-thumbnail">
        <img src={video.thumbnailUrl} alt={video.title} />
        <i className="ti ti-player-play-filled play-icon"></i>
      </div>
      <div className="video-info">
        <h3 className="video-title">{video.title}</h3>
        {video.channelName && <p className="video-channel">{video.channelName}</p>}
      </div>
    </a>
  );
}