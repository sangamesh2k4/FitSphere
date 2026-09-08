import "./ProfileInfo.css";

const ProfileInfo = ({ profile }) => {
    const formatValue = (value) => {
    if (!value) return "-";

    return value
        .toString()
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, letter => letter.toUpperCase());
};

const details = [
    { label: "Age", value: `${profile.age} years` },
    { label: "Gender", value: formatValue(profile.gender) },
    { label: "Height", value: `${profile.height} cm` },
    { label: "Weight", value: `${profile.weight} kg` },
    { label: "Activity Level", value: formatValue(profile.activityLevel) },
    { label: "Goal", value: formatValue(profile.goal) },
];

    return (
        <section className="profile-info">
            <div className="section-header">
                <h2>Profile Information</h2>
                <p>Your personal details used to calculate your health metrics.</p>
            </div>

            <div className="profile-info-card">
                {details.map((detail) => (
                    <div key={detail.label} className="profile-info-row">
                        <span className="profile-info-label">{detail.label}</span>
                        <span className="profile-info-value">{detail.value}</span>
                    </div>
                ))}
            </div>
        </section>
    );
};

export default ProfileInfo;