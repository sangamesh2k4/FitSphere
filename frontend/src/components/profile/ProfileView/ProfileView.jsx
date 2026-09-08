import  ProfileHeader from "../profileheader/ProfileHeader";
import HealthOverview from "../HealthOverview/HealthOverview";
import DailyTargets from "../DailyTargets/DailyTargets";
import ProfileInfo from "../ProfileInfo/ProfileInfo";
import Recommendations from "../Recommendations/Recommendations";

import "./ProfileView.css";

const ProfileView = ({ profile, onEdit }) => {
    return (
        <div className="profile-view">
            <HealthOverview profile={profile} />
            <DailyTargets profile={profile} />
            <ProfileInfo profile={profile} />
            <Recommendations profile={profile} />
            <ProfileHeader 
                updatedAt={profile.updatedAt} 
                onEdit={onEdit} 
            />
        </div>
    );
};

export default ProfileView;