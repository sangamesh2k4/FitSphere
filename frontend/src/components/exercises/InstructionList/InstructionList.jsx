import "./InstructionList.css"

export function InstructionList({ instructions }) {
  if (!instructions || instructions.length === 0) return null;

  return (
    <div className="content-card">
      <h2 className="card-heading">INSTRUCTIONS</h2>
      <div className="instructions-list">
        {instructions.map((step, index) => (
          <div className="instruction-step" key={index}>
            <div className="step-number">{index + 1}</div>
            <div className="step-text">{step}</div>
          </div>
        ))}
      </div>
    </div>
  );
}