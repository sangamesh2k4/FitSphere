import Skeleton from './Skeleton'; // Adjust path to your base Skeleton component

function FoodDetailSkeleton() {
    return (
        <main className="food-detail-wrapper">
            
            {/* Header Area (Mimics FoodHeader) */}
            <div style={{ marginBottom: '1.5rem' }}>
                {/* Back Button */}
                <Skeleton width="80px" height="36px" borderRadius="8px" style={{ marginBottom: '1.5rem' }} />
                
                <header className="detail-header">
                    <div className="title-row" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div style={{ flex: 1 }}>
                            {/* Brand/Category subtitle */}
                            <Skeleton width="120px" height="16px" style={{ marginBottom: '0.75rem' }} />
                            {/* Main Food Title */}
                            <Skeleton width="60%" height="48px" />
                        </div>
                        {/* Favorite Button */}
                        <Skeleton width="180px" height="44px" borderRadius="8px" className="hidden-mobile" style={{ flexShrink: 0 }} />
                    </div>
                </header>
            </div>

            {/* Main Content Area */}
            <section className="detail-main">
                <div className="detail-grid">
                    
                    {/* Sidebar (Mimics FoodSummaryCard) */}
                    <aside className="detail-sidebar">
                        <div className="content-card" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                            <Skeleton width="140px" height="24px" />
                            
                            {/* Large Calorie/Chart Placeholder */}
                            <div style={{ display: 'flex', justifyContent: 'center', margin: '1rem 0' }}>
                                <Skeleton width="160px" height="160px" borderRadius="50%" />
                            </div>

                            {/* Macros List (Protein, Carbs, Fats) */}
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                                <Skeleton width="100%" height="36px" borderRadius="8px" />
                                <Skeleton width="100%" height="36px" borderRadius="8px" />
                                <Skeleton width="100%" height="36px" borderRadius="8px" />
                            </div>
                        </div>
                    </aside>

                    {/* Content Section (Mimics NutritionFactsCard & MineralsCard) */}
                    <section className="detail-content">
                        
                        {/* Nutrition Facts Card */}
                        <div className="content-card" style={{ marginBottom: '1.5rem' }}>
                            <Skeleton width="180px" height="28px" style={{ marginBottom: '1.5rem' }} />
                            
                            {/* Simulated list of nutritional rows */}
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                <Skeleton width="100%" height="24px" />
                                <hr style={{ borderTop: '1px solid var(--fs-border)', margin: 0, opacity: 0.5 }} />
                                <Skeleton width="90%" height="24px" />
                                <hr style={{ borderTop: '1px solid var(--fs-border)', margin: 0, opacity: 0.5 }} />
                                <Skeleton width="95%" height="24px" />
                                <hr style={{ borderTop: '1px solid var(--fs-border)', margin: 0, opacity: 0.5 }} />
                                <Skeleton width="85%" height="24px" />
                                <hr style={{ borderTop: '1px solid var(--fs-border)', margin: 0, opacity: 0.5 }} />
                                <Skeleton width="90%" height="24px" />
                            </div>
                        </div>

                        {/* Minerals/Vitamins Card */}
                        <div className="content-card">
                            <Skeleton width="140px" height="28px" style={{ marginBottom: '1.5rem' }} />
                            
                            {/* Simulated grid of micronutrients */}
                            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '1.25rem' }}>
                                <Skeleton width="100%" height="32px" borderRadius="6px" />
                                <Skeleton width="100%" height="32px" borderRadius="6px" />
                                <Skeleton width="100%" height="32px" borderRadius="6px" />
                                <Skeleton width="100%" height="32px" borderRadius="6px" />
                                <Skeleton width="100%" height="32px" borderRadius="6px" />
                                <Skeleton width="100%" height="32px" borderRadius="6px" />
                            </div>
                        </div>

                    </section>
                </div>
            </section>
            
        </main>
    );
}

export default FoodDetailSkeleton;