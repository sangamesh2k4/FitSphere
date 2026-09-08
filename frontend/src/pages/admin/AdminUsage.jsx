import { useEffect, useState } from "react";
import adminService from "../../services/adminService";
import LoadingSkeleton from "../../components/skeleton/LoadingSkeleton/LoadingSkeleton";
import "../../css/admin/AdminUsage.css";

function AdminUsage() {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUsageStats = async () => {
            try {
                const data = await adminService.getUsageStats();
                setStats(data);
            } catch (error) {
                console.error("Failed to load usage stats:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchUsageStats();
    }, []);

    if (loading) {
        return <LoadingSkeleton />;
    }

    if (!stats) {
        return <p className="admin-usage-loading">Failed to load usage statistics.</p>;
    }

    const totalExercises = stats.activeExercises + stats.disabledExercises;
    const activePercentage = totalExercises > 0 ? (stats.activeExercises / totalExercises) * 100 : 0;

    return (
        <section className="admin-usage-page">
            <div className="admin-usage-header">
                <h1>Good afternoon, Admin</h1>
                <p>Here's how Fitsphere is doing today.</p>
            </div>

            {/* Top Stat Cards Grid */}
            <div className="admin-usage-top-grid">
                <div className="admin-usage-card dark-card">
                    <span className="card-label">TOTAL USERS</span>
                    <strong className="card-value">{stats.totalUsers}</strong>
                </div>

                <div className="admin-usage-card">
                    <span className="card-label">FOOD LOGS</span>
                    <strong className="card-value">{stats.totalFoodLogs}</strong>
                </div>

                <div className="admin-usage-card">
                    <span className="card-label">EXERCISES</span>
                    <strong className="card-value">{stats.totalExercises}</strong>
                </div>
            </div>

            {/* Exercise Library Section */}
            <div className="admin-usage-panel">
                <div className="panel-header-row">
                    <h2>EXERCISE LIBRARY</h2>
                    <span className="panel-sub-right">{Math.round(activePercentage)}% active</span>
                </div>
                
                <div className="progress-bar-container">
                    <div 
                        className="progress-bar-fill" 
                        style={{ width: `${activePercentage}%` }}
                    ></div>
                </div>

                <div className="exercise-library-footer">
                    <div className="legend-item">
                        <span className="dot active-dot"></span>
                        <span>Active <strong>{stats.activeExercises}</strong></span>
                    </div>
                    <div className="legend-item">
                        <span className="dot disabled-dot"></span>
                        <span>Disabled <strong>{stats.disabledExercises}</strong></span>
                    </div>
                </div>
            </div>

            {/* Today's Usage Section */}
            <div className="admin-usage-panel">
                <div className="panel-header-row">
                    <h2>TODAY'S USAGE</h2>
                    <span className="panel-sub-right muted-text">Resets at midnight</span>
                </div>

                <div className="today-usage-metrics-grid">
                    <div className="metric-item">
                        <span className="metric-value">{stats.todayFoodLogs}</span>
                        <span className="metric-label">Food Logs</span>
                    </div>
                    <div className="metric-item">
                        <span className="metric-value">{stats.todayLoginRequests}</span>
                        <span className="metric-label">Logins</span>
                    </div>
                    <div className="metric-item">
                        <span className="metric-value">{stats.todayRegistrationRequests}</span>
                        <span className="metric-label">Registrations</span>
                    </div>
                    <div className="metric-item">
                        <span className="metric-value">{stats.todayNutritionRequests}</span>
                        <span className="metric-label">Nutrition</span>
                    </div>
                    <div className="metric-item">
                        <span className="metric-value">{stats.todayYoutubeRequests}</span>
                        <span className="metric-label">YouTube</span>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default AdminUsage;