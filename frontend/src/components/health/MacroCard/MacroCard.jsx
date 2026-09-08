import "./MacroCard.css";

export default function MacroCard({ result }) {
    const macros = result.recommendedMacros;
    if (!macros) return null;

    const protein = Number(macros.protein) || 0;
    const carbs = Number(macros.carbohydrates) || 0;
    const fat = Number(macros.fat) || 0;

    const maxMacro = Math.max(protein, carbs, fat, 1);
    const getWidth = (value) => `${Math.min((value / maxMacro) * 100, 100)}%`;

    return (
        <div className="health-card macro-card">
            <div className="macro-row-container">
                
                <div className="macro-item">
                    <div className="macro-row-header">
                        <span className="macro-label">Protein</span>
                        <span className="macro-value">{Math.round(protein)}g</span>
                    </div>
                    <div className="macro-progress">
                        <div className="macro-progress-fill" style={{ width: getWidth(protein), background: 'var(--fs-gold)' }} /> {/*[cite: 1] */}
                    </div>
                </div>

                <div className="macro-item">
                    <div className="macro-row-header">
                        <span className="macro-label">Carbs</span>
                        <span className="macro-value">{Math.round(carbs)}g</span>
                    </div>
                    <div className="macro-progress">
                        <div className="macro-progress-fill" style={{ width: getWidth(carbs), background: 'var(--fs-green)' }} /> {/*[cite: 1] */}
                    </div>
                </div>

                <div className="macro-item">
                    <div className="macro-row-header">
                        <span className="macro-label">Fat</span>
                        <span className="macro-value">{Math.round(fat)}g</span>
                    </div>
                    <div className="macro-progress">
                        <div className="macro-progress-fill" style={{ width: getWidth(fat), background: 'var(--fs-text)' }} /> {/*[cite: 1] */}
                    </div>
                </div>

            </div>
        </div>
    );
}