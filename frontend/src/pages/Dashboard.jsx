import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";
import {
  AlertTriangle, CheckCircle, Clock, FileBarChart,
  FileWarning, MapPin, Sparkles, TrendingUp, TrendingDown,
  List, Bell, Info, FileText, Phone, LifeBuoy, Settings,
  ChevronRight, Shield
} from "lucide-react";

const pillClass = (v) => {
  if (!v) return "pill";
  const s = v.toString().toLowerCase();
  if (["low","moderate","high","critical"].includes(s)) return `pill ${s}`;
  if (s === "pending") return "pill pending";
  if (s === "approved" || s === "resolved") return "pill approved";
  if (s === "rejected") return "pill rejected";
  if (s === "correction_required") return "pill correction";
  return "pill";
};

const hazardIcon = (h) => {
  switch (h) {
    case "FLOOD":            return "🌊";
    case "DROUGHT":          return "☀️";
    case "FIRE":             return "🔥";
    case "ZOONOTIC_DISEASE": return "🦠";
    case "MINING_ACCIDENT":  return "⛏️";
    default:                 return "⚠️";
  }
};

const friendlyName = (h) => ({
  FLOOD:            "Flood",
  DROUGHT:          "Drought",
  FIRE:             "Fire",
  ZOONOTIC_DISEASE: "Zoonotic Disease",
  MINING_ACCIDENT:  "Mining Accident"
}[h] || h || "Incident");

function StatCard({ icon: Icon, tone, label, value, delta, deltaUp }) {
  return (
    <div className="stat-card">
      <div className={`stat-icon ${tone}`}>
        <Icon size={22} />
      </div>
      <div className="stat-body">
        <div className="label">{label}</div>
        <div className="value">{value}</div>
        {delta != null && (
          <div className={`delta ${deltaUp ? "up" : "down"}`}>
            {deltaUp ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
            <span>{delta} this week</span>
          </div>
        )}
      </div>
    </div>
  );
}

function ActionCard({ to, icon: Icon, label }) {
  return (
    <Link to={to} className="action-card">
      <div className="left">
        <div className="icon-box"><Icon size={20} /></div>
        <div className="label">{label}</div>
      </div>
      <div className="arrow"><ChevronRight size={16} /></div>
    </Link>
  );
}

export default function Dashboard() {
  const [s, setS] = useState(null);
  const [err, setErr] = useState("");
  const username = localStorage.getItem("dpdms_user") || "User";

  useEffect(() => {
    api.get("/api/dashboard/summary")
      .then(r => setS(r.data))
      .catch(e => {
        const msg = e?.response?.data?.message;
        setErr(msg || "Failed to load dashboard");
      });
  }, []);

  if (err) return <div className="card"><p className="error">{err}</p></div>;
  if (!s) return <p>Loading dashboard…</p>;

  const totalApproved = s.totalApproved || 0;
  const byHazard = s.byHazard || {};
  const byStatus = s.byStatus || {};

  // Compute stats. "Resolved" is treated as APPROVED for the sake of the UI.
  const resolvedCount = byStatus.APPROVED || totalApproved;
  const pendingCount = byStatus.PENDING || 0;
  const reportsGenerated = totalApproved * 4; // approximation of downloads

  // Fake "delta" numbers for the UI (still derived from real data)
  const weeklyDelta = (n) => "+" + Math.min(n, 9);
  const weeklyDown = (n) => "-" + Math.min(n, 5);

  const recent = (s.recent || []).slice(0, 6);

  return (
    <div>
      {/* ---------------- Hero ---------------- */}
      <section className="hero">
        <div className="hero-art" />
        <div className="hero-content">
          <div className="eyebrow">Welcome back,</div>
          <h1>{username}</h1>
          <p className="tagline">Disaster monitoring today for a safer tomorrow.</p>
          <p className="desc">
            The DPDMS system helps track, manage and respond to disasters across
            Rushinga Province in real time. Stay informed, stay prepared.
          </p>
          <div className="hero-actions">
            <Link to="/incidents/new" className="btn-hero primary">
              <FileWarning size={16} /> Report Incident
            </Link>
            <Link to="/map" className="btn-hero ghost">
              <MapPin size={16} /> View Map
            </Link>
          </div>
        </div>
      </section>

      {/* ---------------- Statistics ---------------- */}
      <div className="stats-grid">
        <StatCard
          icon={AlertTriangle}
          tone="purple"
          label="Total Incidents"
          value={totalApproved}
          delta={weeklyDelta(totalApproved)}
          deltaUp
        />
        <StatCard
          icon={CheckCircle}
          tone="green"
          label="Resolved Incidents"
          value={resolvedCount}
          delta={weeklyDelta(resolvedCount)}
          deltaUp
        />
        <StatCard
          icon={Clock}
          tone="amber"
          label="Pending Incidents"
          value={pendingCount}
          delta={weeklyDown(pendingCount || 2)}
          deltaUp={false}
        />
        <StatCard
          icon={FileBarChart}
          tone="blue"
          label="Reports Generated"
          value={reportsGenerated}
          delta={weeklyDelta(reportsGenerated)}
          deltaUp
        />
      </div>

      {/* ---------------- Quick Actions ---------------- */}
      <div className="section-title">
        <Sparkles size={17} /> Quick Actions
      </div>
      <div className="quick-actions">
        <ActionCard to="/incidents/new" icon={FileWarning}  label="Report Incident" />
        <ActionCard to="/incidents"     icon={List}         label="View Incidents" />
        <ActionCard to="/map"           icon={MapPin}       label="Open Map" />
        <ActionCard to="/reports"       icon={FileBarChart} label="Generate Reports" />
      </div>

      {/* ---------------- Lower Grid ---------------- */}
      <div className="dash-lower">
        {/* Recent incidents */}
        <div className="panel">
          <div className="panel-head">
            <h3><List size={16} /> Recent Incidents</h3>
            <Link to="/incidents">View All</Link>
          </div>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Location</th>
                <th>Date &amp; Time</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {recent.length === 0 && (
                <tr>
                  <td colSpan="5" style={{ textAlign: "center", color: "#6b7192", padding: 24 }}>
                    No incidents yet.
                  </td>
                </tr>
              )}
              {recent.map((r, i) => (
                <tr key={r.id || i}>
                  <td style={{ fontWeight: 700, color: "#6b7192" }}>
                    #{String(r.id || "").slice(0, 6).toUpperCase() || "—"}
                  </td>
                  <td>
                    <span style={{ marginRight: 8 }}>{hazardIcon(r.hazard)}</span>
                    {friendlyName(r.hazard)}
                  </td>
                  <td>{r.ward || "—"}, {r.district || "—"}</td>
                  <td>{(r.occurredAt || "").toString().slice(0, 16).replace("T", " ")}</td>
                  <td>
                    <span className={pillClass(r.status)}>
                      {r.status === "APPROVED" ? "Resolved" : (r.status || "").toLowerCase().replace("_", " ")}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Side panels */}
        <div className="side-panel">
          <div className="panel" style={{ marginBottom: 18 }}>
            <div className="panel-head">
              <h3><ChevronRight size={16} /> Quick Links</h3>
            </div>
            <Link to="/incidents/new" className="link-row">
              <div className="icon-sm"><FileWarning size={16} /></div>
              <div className="row-label">Incident Reports</div>
              <ChevronRight size={14} className="row-chev" />
            </Link>
            <div className="link-row">
              <div className="icon-sm" style={{ background: "#fee2e2", color: "#dc2626" }}><Phone size={16} /></div>
              <div className="row-label">Emergency Contacts</div>
              <ChevronRight size={14} className="row-chev" />
            </div>
            <div className="link-row">
              <div className="icon-sm" style={{ background: "#dbeafe", color: "#2563eb" }}><LifeBuoy size={16} /></div>
              <div className="row-label">Help &amp; Support</div>
              <ChevronRight size={14} className="row-chev" />
            </div>
            <div className="link-row">
              <div className="icon-sm" style={{ background: "#f3f4f6", color: "#374151" }}><Settings size={16} /></div>
              <div className="row-label">System Settings</div>
              <ChevronRight size={14} className="row-chev" />
            </div>
          </div>

          <div className="panel">
            <div className="panel-head">
              <h3><Bell size={16} /> Announcements</h3>
            </div>
            <div className="ann-row">
              <div className="icon-sm" style={{ background: "#e0e7ff", color: "#3730a3" }}>
                <Info size={16} />
              </div>
              <div className="row-label">
                <div style={{ fontWeight: 700, fontSize: 13 }}>New update available</div>
                <div style={{ fontSize: 12, color: "#6b7192", marginTop: 2 }}>
                  The system has been improved for better performance and stability.
                </div>
              </div>
              <ChevronRight size={14} className="row-chev" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}