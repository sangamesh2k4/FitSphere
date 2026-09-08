import FeatureCard from '../FeatureCard'

function Features() {

  return (
    <section className="features" id="features">

      <div className="features-heading">
        <p className="section-label">
          BUILT FOR PROGRESS
        </p>

        <h2>
          EVERYTHING YOU NEED.
          <br />
          NOTHING YOU DON'T.
        </h2>
      </div>

      <div className="feature-grid">

        <FeatureCard
          title="TRAIN SMARTER"
          description="Log every set, rep, and weight. Track your estimated 1RM and know exactly when you hit a new personal record."
        />

        <FeatureCard
          title="EAT WITH PURPOSE"
          description="Track calories, protein, carbohydrates, and fats against targets calculated from your own health profile."
        />

        <FeatureCard
          title="SEE REAL PROGRESS"
          description="Follow your weight, body fat, strength, and training volume through real historical analytics."
        />

      </div>

    </section>
  )
}

export default Features