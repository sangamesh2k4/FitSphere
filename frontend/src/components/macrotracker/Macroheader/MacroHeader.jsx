import "./MacroHeader.css";

export default function MacroHeader() {
    return (
        <header className="macro-header">
            <div className="header-title-group">
                <h1>MACRO TRACKER</h1>
                {/* Updated paragraph to reflect search/lookup functionality */}
                <p>Search across<strong style={{ fontSize: '1.1rem', fontWeight: '900',color: '#a09c9c' }}> 3.5 lakh food items</strong> to instantly discover the exact calories, protein, carbohydrates, and fats contained in any food item.</p>
                
                {/* USDA attribution */}
                <p className="usda-attribution" style={{ fontSize: '1.05rem', fontWeight: 'bold', color: '#a09c9c', marginTop: '8px' }}>
                    Powered by USDA
                </p>
            </div>
          
        </header>
    );
}