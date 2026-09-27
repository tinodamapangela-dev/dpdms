import { useEffect, useState } from "react";
import api from "../api/client";
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, Legend, CartesianGrid, ResponsiveContainer,
  PieChart, Pie, LineChart, Line
} from "recharts";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png"
});

const pillClass = (v) => {
  if (!v) return "pill";
  const s = v.toString().toLowerCase();
  if (["low","moderate","high","critical"].includes(s)) return `pill ${s}`;
  if (s === "pending") return "pill pending";
  if (s === "approved") return "pill approved";
  if (s === "rejected") return "pill rejected";
  if (s === "correction_required") return "pill correction";
  return "pill";
};

export default function Dashboard() {
  const [s, setS] = useState(null);
  const [err, setErr] = useState("");
  const role = localStorage.getItem("dpdms_role") || "";
  const isRecorder = role.endsWith("_RECORDER");

  useEffect(() => {
    if (isRecorder) return;
    api.get("/api/dashboard/summary")
      .then(r => setS(r.data))
      .catch(e => {
        const status = e?.response?.status;
        const msg = e?.response?.data?.message;
        if (status === 403) setErr("Your role does not have dashboard access.");
        else setErr(msg || "Failed to load dashboard");
      });
  }, [isRecorder]);

  if (isRecorder) {
    return (
      <div className="card">
        <h2>Dashboard restricted</h2>
        <p>Aggregate dashboards are available to supervisors, national users, and provincial administrators.</p>
        <p>As a <b>{role}</b>, please use the <a href="/incidents">Incidents</a> page.</p>
      </div>
    );
  }
  if (err) return <div className="card"><p className="error">{err}</p></div>;
  if (!s) return <p>Loading dashboard…</p>;

  const hazardData = Object.entries(s.byHazard || {}).map(([k, v]) => ({ name: k, value: v }));
  const severityData = Object.entries(s.bySeverity || {}).map(([k, v]) => ({ name: k, value: v }));
  const trendData = Object.entries(s.overTime || {}).map(([k, v]) => ({ date: k, count: v }));

  return (
    <div>
      <h1>Approved Incident Dashboard</h1>
      <p>Total approved incidents: <b>{s.totalApproved}</b></p>

      <div className="grid">
        <div className="card">
          <h3>By Hazard</h3>
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={hazardData}>
              <CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="name" /><YAxis />
              <Tooltip /><Legend /><Bar dataKey="value" fill="#7c3aed" radius={[8,8,0,0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="card">
          <h3>By Severity</h3>
          <ResponsiveContainer width="100%" height={240}>
            <PieChart>
              <Pie data={severityData} dataKey="value" nameKey="name" outerRadius={90}
                fill="#06b6d4" label />
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <div className="card">
          <h3>Over Time</h3>
          <ResponsiveContainer width="100%" height={240}>
            <LineChart data={trendData}>
              <CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="date" /><YAxis /><Tooltip />
              <Line type="monotone" dataKey="count" stroke="#ec4899" strokeWidth={3}
                dot={{ r: 5, fill: "#ec4899" }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <h3 style={{ marginTop: 24 }}>Approved Incidents — Map</h3>
      <div className="card map">
        <MapContainer center={[-16.6, 31.9]} zoom={9} style={{ height: "100%", width: "100%" }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
          {(s.incidents || []).map(i => (
            <Marker key={i.id} position={[i.latitude, i.longitude]}>
              <Popup>
                <b>{i.hazard}</b><br />{i.ward} — {i.district}<br />
                Severity: {i.severity}<br />{i.occurredAt}
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>

      <h3 style={{ marginTop: 24 }}>Recent Approved Incidents</h3>
      <table>
        <thead>
          <tr><th>Hazard</th><th>Ward</th><th>Severity</th><th>Occurred</th></tr>
        </thead>
        <tbody>
          {(s.recent || []).map(r => (
            <tr key={r.id}>
              <td>{r.hazard}</td>
              <td>{r.ward}</td>
              <td><span className={pillClass(r.severity)}>{r.severity}</span></td>
              <td>{r.occurredAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}