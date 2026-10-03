import { Routes, Route, Link, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Incidents from "./pages/Incidents";
import NewIncident from "./pages/NewIncident";
import Reports from "./pages/Reports";
import PendingApprovals from "./pages/PendingApprovals";
import { useAuth } from "./auth/useAuth";

function Header() {
  const { isAuthed, role, logout } = useAuth();
  return (
    <header>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <div style={{
          width: 36, height: 36, borderRadius: 12,
          background: "linear-gradient(135deg,#c084fc,#7c3aed 45%,#06b6d4)",
          display: "flex", alignItems: "center", justifyContent: "center",
          fontWeight: 700, fontSize: 17, boxShadow: "0 6px 16px rgba(124,58,237,.35)"
        }}>D</div>
        <div>
          <div style={{ fontWeight: 700, fontSize: 15, lineHeight: 1.1 }}>DPDMS</div>
          <div style={{ fontSize: 10.5, opacity: .8, fontWeight: 500 }}>Rushinga Provincial Disaster Monitoring</div>
        </div>
      </div>
      <nav>
        {isAuthed && <Link to="/dashboard">Dashboard</Link>}
        {isAuthed && <Link to="/incidents">Incidents</Link>}
        {isAuthed && <Link to="/incidents/new">Report Incident</Link>}
        {isAuthed && <Link to="/pending">Pending</Link>}
        {isAuthed && <Link to="/reports">Reports</Link>}
        {isAuthed && <span style={{ marginLeft: 16, opacity: .8, fontSize: 13 }}>{role}</span>}
        {isAuthed && <button style={{ marginLeft: 16 }} onClick={logout}>Logout</button>}
      </nav>
    </header>
  );
}

function Private({ children }) {
  const { isAuthed } = useAuth();
  return isAuthed ? children : <Navigate to="/login" />;
}

export default function App() {
  return (
    <>
      <Header />
      <div className="container">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route path="/dashboard" element={<Private><Dashboard /></Private>} />
          <Route path="/incidents" element={<Private><Incidents /></Private>} />
          <Route path="/incidents/new" element={<Private><NewIncident /></Private>} />
          <Route path="/pending" element={<Private><PendingApprovals /></Private>} />
          <Route path="/reports" element={<Private><Reports /></Private>} />
        </Routes>
      </div>
    </>
      <footer style={{
        textAlign: "center", padding: "24px 16px", color: "#8b94a7",
        fontSize: 12, marginTop: 40, borderTop: "1px solid #e9ecf3", lineHeight: 1.8
      }}>
        <div><b>DPDMS</b> — Rushinga Provincial Disaster Monitoring</div>
        <div>Group Authors: Tinodaishe Mapangela · Sisilisiwe Ndhlovu · Nokutenda Zvenyika · Blessing Berejena · Elshama Chivete</div>
        <div>University of Zimbabwe · HCS201 / HCC201 / HAI201 · 2026</div>
      </footer>
  );
}