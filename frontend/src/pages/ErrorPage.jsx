import { useNavigate } from "react-router-dom";
import "../css/ErrorPage.css";

function ErrorPage({
    code = "404",
    title = "Page Not Found",
    message = "The page you're looking for doesn't exist.",
}) {
    const navigate = useNavigate();

    const handleBack = () => {
        navigate(-1);
    };

    return (
        <main className="error-page">
            <div className="error-page-content">

                <span className="error-code">
                    {code}
                </span>

                <h1>{title}</h1>

                <p>{message}</p>

                <button
                    type="button"
                    className="error-back-button"
                    onClick={handleBack}
                >
                    ← Go Back
                </button>

            </div>
        </main>
    );
}

export default ErrorPage;