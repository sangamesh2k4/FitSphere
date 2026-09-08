import { useEffect, useState } from "react";

import ProfileView from "../components/profile/ProfileView/ProfileView";
import EmptyProfile from "../components/profile/EmptyProfile";
import ProfileForm from "../components/profile/ProfileForm";

import profileService from "../services/profileService";
import "../styles/profile/Profile.css"; // Ensure this path matches your structure
import LoadingSkeleton from "../components/skeleton/LoadingSkeleton/LoadingSkeleton";

const ProfilePage = () => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [isEditing, setIsEditing] = useState(false);

        const fetchProfile = async () => {
        try {
            const data = await profileService.getProfile();
            setProfile(data);
        } catch {
            // Silently handle errors
        } finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        fetchProfile();
    }, []);



    const handleCreateProfile = async (formData) => {
        try {
            const createdProfile = await profileService.createProfile(formData);
            setProfile(createdProfile);
            setShowForm(false);
            setIsEditing(false);
        } catch {
            // Silently handle errors
        }
    };

    const handleUpdateProfile = async (formData) => {
        try {
            const updatedProfile = await profileService.updateProfile(formData);
            setProfile(updatedProfile);
            setShowForm(false);
            setIsEditing(false);
        } catch  {
            // Silently handle errors
        }
    };

    const renderContent = () => {
        if (loading) {
            return (
                <LoadingSkeleton />
            );
        }

        if (!profile && !showForm) {
            return (
                <EmptyProfile
                    onCreate={() => {
                        setIsEditing(false);
                        setShowForm(true);
                    }}
                />
            );
        }

        if (showForm) {
            return (
                <ProfileForm
                    mode={isEditing ? "edit" : "create"}
                    initialValues={isEditing ? profile : null}
                    onSubmit={isEditing ? handleUpdateProfile : handleCreateProfile}
                    onCancel={() => {
                        setShowForm(false);
                        setIsEditing(false);
                    }}
                />
            );
        }

        return (
            <ProfileView
                profile={profile}
                onEdit={() => {
                    setIsEditing(true);
                    setShowForm(true);
                }}
            />
        );
    };

    return (
        <div className="profile-page">
            <div className="profile-content">
                {renderContent()}
            </div>
        </div>
    );
};

export default ProfilePage;