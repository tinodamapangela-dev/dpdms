import { Routes, Route, Link, NavLink, Navigate, useLocation } from "react-router-dom";
import {
  Shield, LayoutDashboard, AlertTriangle, FileWarning, Clock,
  FileBarChart, MapPin, ChevronDown
} from "lucide-react";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Incidents from "./pages/Incidents";
import NewIncident from "./pages/NewIncident";
import Reports from "./pages/Reports";
import Map from "./pages/Map";
import PendingApprovals from "./pages/PendingApprovals";
import { useAuth } from "./auth/useAuth";

function initials(name) {
  if (!name) return "U";
  return name.split(".").map(s => s[0]).slice(0, 2).join("").toUpperCase();
}

function Header() {
  const { isAuthed, role, logout } = useAuth();
  const username = localStorage.getItem("dpdms_user") || role || "User";

  const NavItem = ({ to, icon: Icon, label }) => (
    <NavLink
      to={to}
      className={({ isActive }) => (isActive ? "active" : "")}
    >
      <Icon size={15} />
      <span>{label}</span>
    </NavLink>
  );

  return (
    <header>
      <div className="brand">
        <div className="brand-logo">
          <Shield size={22} />
        </div>
        <div className="brand-text">
          <div className="name">DPDMS</div>
          <div className="full">Rushinga Provincial Disaster Monitoring</div>
        </div>
      </div>

      {isAuthed && (
        <nav className="nav-links">
          <NavItem to="/dashboard"      icon={LayoutDashboard} label="Dashboard" />
          <NavItem to="/incidents"      icon={AlertTriangle}   label="Incidents" />
          <NavItem to="/incidents/new"  icon={FileWarning}     label="Report Incident" />
          <NavItem to="/pending"        icon={Clock}           label="Pending" />
          <NavItem to="/reports"        icon={FileBarChart}    label="Reports" />
          <NavItem to="/map"            icon={MapPin}          label="Map" />
        </nav>
      )}

      {isAuthed && (
        <div className="user-menu">
          <div className="welcome">
            <div className="greeting">Welcome,</div>
            <div className="who">{username}</div>
          </div>
          <div className="avatar">{initials(username)}</div>
          <button className="logout-btn" onClick={logout}>Logout</button>
        </div>
      )}
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
          <Route path="/dashboard"     element={<Private><Dashboard /></Private>} />
          <Route path="/incidents"     element={<Private><Incidents /></Private>} />
          <Route path="/incidents/new" element={<Private><NewIncident /></Private>} />
          <Route path="/pending"       element={<Private><PendingApprovals /></Private>} />
          <Route path="/reports"       element={<Private><Reports /></Private>} />
          <Route path="/map"           element={<Private><Map /></Private>} />
        </Routes>
      </div>

      <footer>
        <div><b>DPDMS</b> — Rushinga Provincial Disaster Monitoring</div>
        <div>Group Authors: Tinodaishe Mapangela · Sisilisiwe Ndhlovu · Nokutenda Zvenyika · Blessing Berejena · Elshama Chivete</div>
        <div>University of Zimbabwe · HCS201 / HCC201 / HAI201 · 2026</div>
      </footer>
    </>
  );
}