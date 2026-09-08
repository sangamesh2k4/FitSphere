import { useAuthModal } from "../../context/AuthModalContext";


function Hero({ onScrollToHowItWorks }) {
  const { openRegister,  } = useAuthModal();

  return (
    <section className="hero">

      <div className="hero-content">

        <h1 className="hero-title">
          YOUR FITNESS.
          <br />
          YOUR DATA.
          <br />
          <span>YOUR PROGRESS.</span>
        </h1>

        <p className="hero-description">
          Track workouts, calculate macros, and hit
          personal records — all in one place.
        </p>

        <div className="hero-actions">

          <button className="primary-button" onClick={openRegister}>
            Start for Free
          </button>

          <button className="secondary-button" onClick={onScrollToHowItWorks}>
            See how it works
          </button>

        </div>

      </div>

    </section>
  )
}

export default Hero