import { useEffect } from "react";
import { useLocation } from "react-router-dom";

function ScrollToHash() {
    const { hash } = useLocation();

    useEffect(() => {
        if (!hash) {
            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
            return;
        }

        const id = hash.replace("#", "");

        // Wait until the new route has rendered
        setTimeout(() => {
            const element = document.getElementById(id);

            if (element) {
                element.scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
            }
        }, 0);

    }, [hash]);

    return null;
}

export default ScrollToHash;