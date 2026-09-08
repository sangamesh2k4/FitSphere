import { Outlet } from "react-router-dom";
import AdminBottomNav from "../components/common/BottomNav/BottomNav";

function AdminLayout() {
    return (
        <div className="admin-layout">

            <main className="admin-content">
                <Outlet />
            </main>

            <AdminBottomNav />

        </div>
    );
}

export default AdminLayout;