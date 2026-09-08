import "./ProfileHeader.css";

const ProfileHeader = ({ updatedAt, onEdit }) => {
    const formattedDate = updatedAt
        ? new Date(updatedAt).toLocaleString("en-IN", {
              day: "2-digit",
              month: "short",
              year: "numeric",
              hour: "numeric",
              minute: "2-digit",
          })
        : "Not available";

    return (
        <div className="profile-header-footer">
            <div className="profile-last-updated">
                <span className="label">&#8226; Last Updated:</span>
                <span className="value">{formattedDate}</span>
            </div>

            <button 
                type="button" 
                className="edit-profile-btn"
                onClick={onEdit}
            >
                Edit Profile
            </button>
        </div>
    );
};

export default ProfileHeader;