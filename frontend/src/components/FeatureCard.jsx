function FeatureCard(props) {

  return (
    <div className="feature-card">

      <h3 className="feature-card-title">
        {props.title}
      </h3>

      <p className="feature-card-description">
        {props.description}
      </p>

    </div>
  )
}

export default FeatureCard