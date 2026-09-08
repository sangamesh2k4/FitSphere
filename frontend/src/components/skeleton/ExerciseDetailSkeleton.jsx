import Skeleton from './Skeleton'; // Adjust path to your base Skeleton component
import '../../css/ExerciseDetail.css';

export function ExerciseDetailSkeleton() {
  return (
    <main className="exercise-detail-wrapper">
      {/* Back Button Skeleton */}
      <div style={{ marginBottom: '1.5rem' }}>
        <Skeleton width="80px" height="36px" borderRadius="8px" />
      </div>

      <header className="detail-header">
        {/* Category */}
        <Skeleton width="120px" height="16px" style={{ marginBottom: '0.75rem' }} />
        
        {/* Title Row */}
        <div className="title-row">
          <Skeleton width="60%" height="48px" />
          <Skeleton width="180px" height="44px" borderRadius="8px" className="hidden-mobile" />
        </div>

        {/* Badges */}
        <div className="badge-group" style={{ marginTop: '1rem' }}>
          <Skeleton width="90px" height="28px" borderRadius="16px" />
          <Skeleton width="110px" height="28px" borderRadius="16px" />
          <Skeleton width="100px" height="28px" borderRadius="16px" />
        </div>
      </header>

      <section className="detail-main">
        <div className="detail-grid">
          
          {/* Sidebar (Media + Metadata) */}
          <aside className="detail-sidebar">
            {/* Video/Image Media Placeholder */}
            <div style={{ width: '100%', aspectRatio: '16/9', marginBottom: '1.5rem' }}>
              <Skeleton width="100%" height="100%" borderRadius="16px" />
            </div>
            
            {/* Metadata Block (Muscles, Target, etc) */}
            <div className="content-card" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Skeleton width="40%" height="24px" />
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <Skeleton width="100%" height="40px" borderRadius="8px" />
                <Skeleton width="100%" height="40px" borderRadius="8px" />
                <Skeleton width="100%" height="40px" borderRadius="8px" />
                <Skeleton width="100%" height="40px" borderRadius="8px" />
              </div>
            </div>
          </aside>

          {/* Main Content Area */}
          <section className="detail-content">
            
            {/* Description */}
            <div className="content-card">
              <Skeleton width="140px" height="24px" style={{ marginBottom: '1rem' }} />
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                <Skeleton width="100%" height="16px" />
                <Skeleton width="100%" height="16px" />
                <Skeleton width="85%" height="16px" />
              </div>
            </div>

            {/* Instructions */}
            <div className="content-card" style={{ marginTop: '1.5rem' }}>
              <Skeleton width="160px" height="24px" style={{ marginBottom: '1.5rem' }} />
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                {[1, 2, 3, 4].map((step) => (
                  <div key={step} style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
                    <Skeleton width="32px" height="32px" borderRadius="50%" style={{ flexShrink: 0 }}/>
                    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '0.5rem', marginTop: '6px' }}>
                      <Skeleton width="100%" height="16px" />
                      <Skeleton width="90%" height="16px" />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Tips & Mistakes Grid */}
            <div className="tips-mistakes-grid" style={{ marginTop: '1.5rem' }}>
              {/* Pros/Tips */}
              <div className="content-card">
                <Skeleton width="120px" height="24px" style={{ marginBottom: '1.25rem' }} />
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                  <Skeleton width="100%" height="20px" />
                  <Skeleton width="95%" height="20px" />
                  <Skeleton width="90%" height="20px" />
                </div>
              </div>
              
              {/* Mistakes */}
              <div className="content-card">
                <Skeleton width="160px" height="24px" style={{ marginBottom: '1.25rem' }} />
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                  <Skeleton width="100%" height="20px" />
                  <Skeleton width="95%" height="20px" />
                  <Skeleton width="90%" height="20px" />
                </div>
              </div>
            </div>

          </section>
        </div>
      </section>

      {/* Recommended Videos (Optional visual filler) */}
      <section className="videos-section" style={{ marginTop: '3rem' }}>
        <Skeleton width="220px" height="24px" style={{ marginBottom: '1.5rem' }} />
        <div className="videos-grid" style={{ display: 'flex', gap: '1.5rem', overflow: 'hidden' }}>
          {[1, 2, 3].map((vid) => (
            <div key={vid} style={{ flex: '1', minWidth: '250px' }}>
              <div style={{ aspectRatio: '16/9', marginBottom: '0.75rem' }}>
                <Skeleton width="100%" height="100%" borderRadius="12px" />
              </div>
              <Skeleton width="85%" height="16px" />
            </div>
          ))}
        </div>
      </section>

    </main>
  );
}