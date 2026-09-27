import { useEffect, useState } from "react";
import api from "../api/client";

const hazards = [
  { path: "floods", label: "Flood" },
  { path: "droughts", label: "Drought" },
  { path: "fires", label: "Fire" },
  { path: "zoonotic", label: "Zoonotic Disease" },
  { path: "mining", label: "Mining Accident" }
];

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

export default function Incidents() {
  const [hazard, setHazard] = useState("floods");
  const [rows, setRows] = useState([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    api.get(`/api/${hazard}`)
      .then(r => { setRows(r.data); setErr(""); })
      .catch(e => setErr(e?.response?.data?.message || "Failed to load"));
  }, [hazard]);

  return (
    <div>
      <h1>Incidents</h1>
      <div className="card">
        <label>Hazard</label>
        <select value={hazard} onChange={e => setHazard(e.target.value)}>
          {hazards.map(h => <option key={h.path} value={h.path}>{h.label}</option>)}
        </select>
      </div>
      {err && <p className="error">{err}</p>}
      <table>
        <thead>
          <tr><th>Ward</th><th>Status</th><th>Severity</th><th>Occurred</th></tr>
        </thead>
        <tbody>
          {rows.map(r => (
            <tr key={r.id}>
              <td>{r.ward}</td>
              <td><span className={pillClass(r.status)}>{r.status}</span></td>
              <td><span className={pillClass(r.severity)}>{r.severity}</span></td>
              <td>{r.occurredAt}</td>
            </tr>
          ))}
          {rows.length === 0 && (
            <tr><td colSpan="4" style={{ textAlign: "center", color: "#6b7280" }}>No incidents</td></tr>
          )}
        </tbody>
      </table>
    </div>
  );
}