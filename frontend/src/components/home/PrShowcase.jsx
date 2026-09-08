function PrShowcase() {

  return (
    <section className="pr-showcase">

      <div className="pr-content">

        <p className="section-label">
          BUILT FOR THE MOMENT
        </p>

        <h2>
          WHEN YOU BEAT
          <br />
          YOUR BEST,
          <br />
          <span>YOU'LL KNOW.</span>
        </h2>

        <p className="pr-description">
          FitSphere tracks your estimated one-rep max
          against your training history and automatically
          detects new personal records.
        </p>

      </div>

      <div className="pr-card">

        <p className="pr-card-label">
          NEW PERSONAL RECORD
        </p>

        <h3>BENCH PRESS</h3>

        <div className="pr-value">
          109.33
          <span>KG</span>
        </div>

        <p className="pr-previous">
          Previous best — 88.67 kg
        </p>

        <div className="pr-improvement">
          +20.66 KG
        </div>

      </div>

    </section>
  )
}

export default PrShowcase