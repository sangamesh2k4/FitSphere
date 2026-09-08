import { useState } from "react";
import ChangeUsernameCard from "./ChangeUsernameCard";
import ChangePasswordCard from "./ChangePasswordCard";
import ChangeEmailCard from "./ChangeEmailCard";
import AppearanceCard from "./AppearanceCard/ApperanceCard";

import "../../styles/settings/settings.css";

function SettingsView() {

    const [openCard, setOpenCard] = useState(null);

    const toggleCard = (card) => {
        setOpenCard((prev) => (prev === card ? null : card));
    };

    return (
        <div className="settings-page">

            <div className="settings-content">
                <AppearanceCard
    isOpen={openCard === "appearance"}
    onToggle={() => toggleCard("appearance")}
/>

                <ChangeUsernameCard
                    isOpen={openCard === "username"}
                    onToggle={() => toggleCard("username")}
                />

                <ChangePasswordCard
                    isOpen={openCard === "password"}
                    onToggle={() => toggleCard("password")}
                />

                <ChangeEmailCard
    isOpen={openCard === "email"}
    onToggle={() => toggleCard("email")}
    
/>

            </div>

        </div>
    );
}

export default SettingsView;