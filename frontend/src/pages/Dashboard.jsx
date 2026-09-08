import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import DashboardGreeting
    from "../components/dashboard/DashboardGreeting/DashboardGreeting";

import DashboardMetricCard
    from "../components/dashboard/DashboardMetricCard/DashboardMetricCard";

import DashboardQuickActions
    from "../components/dashboard/DashboardQuickActions/DashboardQuickActions";

import DashboardNutrition
    from "../components/dashboard/DashboardNutrition/DashboardNutrition";

import DashboardConsistency
    from "../components/dashboard/DashboardConsistency/DashboardConsistency";

import { analyticsService } from "../services/analyticsService";

import "../css/Dashboard.css";


function Dashboard() {

    const navigate = useNavigate();

    const [analytics, setAnalytics] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");


    useEffect(() => {

        const fetchDashboardData = async () => {

            try {

                setLoading(true);
                setError("");

                const response =
                    await analyticsService.getDashboardAnalytics();

                setAnalytics(
                    response?.data ?? response
                );

            } catch (err) {

                console.error(
                    "Failed to load dashboard metrics:",
                    err
                );

                setError(
                    "Failed to load dashboard metrics."
                );

            } finally {

                setLoading(false);

            }
        };

        fetchDashboardData();

    }, []);


    return (
        <main className="dashboard-page">

            <DashboardGreeting />


            {loading && (
                <div className="dashboard-loading">
                    Loading metrics...
                </div>
            )}


            {!loading && error && (
                <div className="dashboard-error">
                    {error}
                </div>
            )}


            {!loading && !error && analytics && (
                <>

                    {/* =========================
                        Weight + Body Fat
                    ========================= */}

                    <section className="dashboard-metrics">

                        <DashboardMetricCard
                            label="CURRENT WEIGHT"
                            value={analytics.currentWeight}
                            unit="kg"
                            change={analytics.weightChange}
                            changeUnit=" kg"
                            decreaseIsPositive={true}
                            onClick={() =>
                                navigate(
                                    "/app/measurements"
                                )
                            }
                        />

                        <DashboardMetricCard
                            label="BODY FAT"
                            value={
                                analytics.currentBodyFatPercentage
                            }
                            unit="%"
                            change={analytics.bodyFatChange}
                            changeUnit="%"
                            decreaseIsPositive={true}
                            onClick={() =>
                                navigate(
                                    "/app/measurements"
                                )
                            }
                        />

                    </section>


                    {/* =========================
                        Quick Actions
                    ========================= */}

                    <DashboardQuickActions />


                    {/* =========================
                        Nutrition
                    ========================= */}

                    <DashboardNutrition
                        analytics={analytics}
                    />


                    {/* =========================
                        Consistency
                    ========================= */}

                    <DashboardConsistency
                        daysLogged={analytics.daysLogged}
                    />

                </>
            )}

        </main>
    );
}


export default Dashboard;