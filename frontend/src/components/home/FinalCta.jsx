import { useAuthModal } from "../../context/AuthModalContext";

function FinalCta() {
  const { openRegister } = useAuthModal();

  return (
    <section className="final-cta">

      <p className="section-label">
        YOUR NEXT SESSION STARTS HERE
      </p>

      <h2>
        STOP GUESSING.
        <br />
        START TRACKING.
      </h2>
      <p>
        Build your profile, log the work,
        and let your progress speak for itself.
      </p>
      <br></br>
      <button className="primary-button cta-button" onClick={openRegister}>
        Create Free Account
      </button>

    </section>
  )
}

export default FinalCta