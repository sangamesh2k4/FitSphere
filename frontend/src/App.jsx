import { Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
// Layouts
import PublicLayout from "./layouts/PublicLayout";
import AppLayout from "./layouts/AppLayout";
import AdminLayout from "./layouts/AdminLayout";

// Pages
import HomePage from "./pages/HomePage";
import ErrorPage from "./pages/ErrorPage";
import ContactUs from "./pages/ContactUs";

import Dashboard from './pages/Dashboard';
import HealthAssessment from './pages/HealthAssessment';
import ExploreExercises from "./pages/ExploreExercises";
import ExerciseDetails from "./pages/ExerciseDetail";
import ProfilePage from "./pages/ProfilePage";
import SettingsPage from "./pages/SettingsPage";
import FavoritesPage from "./pages/FavoritesPage";
import FoodDetail from "./pages/FoodDetail";
import WorkoutPage from "./pages/workouts/Workouts";
import WorkoutSummary from "./pages/workouts/WorkoutSummary";
import ActiveWorkout from "./pages/workouts/ActiveWorkout";
import WorkoutDetails from "./components/workouts/WorkoutDetails/WorkoutDetails";
import EditWorkout from "./components/workouts/EditWorkout/EditWorkout";
import BodyMeasurements from "./pages/BodyMeasurements";
import NutritionPage from "./pages/NutritionPage";
import Progress from "./pages/Progress";

import AdminExercises from "./pages/admin/AdminExercises";
import AdminExerciseFormPage from "./pages/admin/AdminExerciseFormPage";
import AdminUsers from "./pages/admin/AdminUsers";
import AdminInbox from "./pages/admin/AdminInbox";
import AdminMessage from "./pages/admin/AdminMessage";
import AdminUsage from "./pages/admin/AdminUsage";



import AuthModalManager from "./components/auth/AuthModalManager";
import MacroTracker from './pages/MacroTracker'


// Route Guards
import ProtectedRoute from "./routes/ProtectedRoute";
import AdminRoute from "./routes/AdminRoute";
import PublicRoute from "./routes/publicRoute";
import AuthPage from "./pages/auth/AuthPage";

function App() {
  return (
    <>
    <Routes>
        <Route element={<PublicRoute/>}>
          {/* ========================================== */}
          {/* 1. MARKETING PAGE (Navbar + Footer)          */}
          {/* ========================================== */}
          {/* If logged in, send them to dashboard instead of showing the homepage */}

          <Route element={<PublicLayout/>}>
          <Route path="/" element={<HomePage />} />
          <Route path="/exercises" element={ <ExploreExercises /> }/> 
          <Route path="/health-assessment"element={<HealthAssessment />}/>
          <Route path="/macro-tracker"element={<MacroTracker />}/>
          <Route path="/exercises/:id" element={<ExerciseDetails />}/>
          <Route path="/contact" element={<ContactUs />} />
          </Route>
          
          <Route path="/login" element={<AuthPage/>} />
            <Route path="/register" element={<AuthPage />} />
            <Route path="/401" element={ <ErrorPage
        code="401"
        title="Unauthorized"
        message="You need to be logged in to access this page."/>
} />

<Route path="/404" element={<ErrorPage
        code="404"
        title="Page Not Found"
        message="The page you're looking for doesn't exist."/>
} />
</Route>

          {/* ========================================== */}
          {/* 2. AUTH PAGES (Minimal Header)               */}
          {/* ========================================== */}
          <Route element={<PublicRoute/>}>
            <Route path="/login" element={<AuthPage/>} />
            <Route path="/register" element={<AuthPage />} />
          </Route>

          

          {/* ========================================== */}
          {/* 3. PROTECTED APP PAGES (Sidebar/App Navbar)  */}
          {/* ========================================== */}
          <Route element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          }>
            <Route path="/app/dashboard"element={<Dashboard/>}/>
             <Route path="/app/nutrition"element={<NutritionPage />}/>
            <Route path="/app/profile" element={<ProfilePage />}/>
            <Route path="/app/settings" element={<SettingsPage />} />
            <Route path="/app/favorites" element={<FavoritesPage />}/> 
            <Route path="/app/exercises" element={<ExploreExercises />} />
            <Route path="/app/exercises/:id" element={<ExerciseDetails />} />
            <Route path="/app/macro-tracker/food/:fdcId" element={<FoodDetail />}/>
            <Route path="/app/macro-tracker"element={<MacroTracker />}/>
            <Route path="/app/health-assessment" element={<HealthAssessment />}/>
            <Route path="/app/measurements" element={<BodyMeasurements />}/>
            <Route path="/app/workouts"element={<WorkoutPage />} />
            <Route path="/app/workouts/active" element={<ActiveWorkout/>}/>
            <Route path="/app/workouts/summary" element={<WorkoutSummary />}/>
            <Route path="/app/workouts/:workoutId" element={<WorkoutDetails />}/>
            <Route path="/app/workouts/:workoutId/edit" element={<EditWorkout />}/>
            <Route path="/app/progress" element={<Progress />}/>
            <Route path="/app/contact" element={<ContactUs />} />
            <Route path="*" element={<ErrorPage
                code="404"
                title="Page Not Found"
                message="The page you're looking for doesn't exist."/>}/>
            
          </Route>
          <Route path="*" element={<ErrorPage code="404" title="Page Not Found"
            message="The page you're looking for doesn't exist."/>}/> 


            {/* ========================================== */}
            {/*               2. ADMIN PAGE                */}
            {/* ===========================================*/}
            <Route  path="/admin" element={<AdminRoute />}>
            <Route element={<AdminLayout />}>
            <Route path="exercises" element={<AdminExercises />} />
            <Route path="exercises/add" element={<AdminExerciseFormPage />} />
            <Route path="exercises/edit/:id" element={<AdminExerciseFormPage />} />
            <Route path="inbox" element={<AdminInbox />} />
            <Route path="users" element={<AdminUsers />} />
            <Route path="inbox/:id" element={<AdminMessage />} />
            <Route path="usage" element={<AdminUsage />} />
          <Route path="*" element={<ErrorPage code="404" title="Page Not Found"
            message="The page you're looking for doesn't exist."/>}/> 
            </Route>
            </Route>


        </Routes>
        <ToastContainer
    position="top-right"
    autoClose={3000}
    hideProgressBar={false}
    newestOnTop
    closeOnClick
    pauseOnHover
    draggable
    theme="dark"
/>
                   <AuthModalManager />
                   </>
  );
}

export default App;