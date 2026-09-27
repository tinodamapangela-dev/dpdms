import { useEffect, useState } from "react";
import api from "../api/client";
import { useAuth } from "../auth/useAuth";

const supervisorMap = {
  FLOOD_SUPERVISOR: "floods",
  DROUGHT_SUPERVISOR: "droughts",
  FIRE_SUPERVISOR: "fires",
  ZOONOTIC_SUPERVISOR: "zoonotic",
  MINING_SUPERVISOR: "mining"
};

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

export default function PendingApprovals() {
  const { role } = useAuth();
  const path = supervisorMap[role];
  const [rows, setRows] = useState([]);
  const [reason, setReason] = useState("");
  const [msg, setMsg] = useState("");

  const load = () => {
    if (!path) return;
    api.get(`/api/${path}`).then(r =>
      setRows(r.data.filter(x => x.status === "PENDING")));
  };

  useEffect(load, []);

  if (!path) {
    return (
      <div className="card">
        <h2>Pending Approvals</h2>
        <p>Only hazard supervisors may approve incidents.</p>
      </div>
    );
  }

  const act = async (id, action) => {
    setMsg("");
    try {
      if (action === "approve") await api.post(`/api/${path}/${id}/approve`);
      else if (action === "reject") await api.post(`/api/${path}/${id}/reject`, { reason });
      else await api.post(`/api/${path}/${id}/request-correction`, { reason });
      setMsg("Done");
      load();
    } catch (e) {
      setMsg(e?.response?.data?.message || "Action failed");
    }
  };

  return (
    <div>
      <h1>Pending Approvals</h1>
      <div className="card">
        <label>Reason (for reject / request correction)</label>
        <input
          value={reason}
          onChange={e => setReason(e.target.value)}
          placeholder="Reason (optional)"
        />
      </div>
      <table>
        <thead>
          <tr><th>Ward</th><th>Severity</th><th>Occurred</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {rows.map(r => (
            <tr key={r.id}>
              <td>{r.ward}</td>
              <td><span className={pillClass(r.severity)}>{r.severity}</span></td>
              <td>{r.occurredAt}</td>
              <td>
                <button onClick={() => act(r.id, "approve")}>Approve</button>{" "}
                <button className="danger" onClick={() => act(r.id, "reject")}>Reject</button>{" "}
                <button onClick={() => act(r.id, "correction")}>Request Correction</button>
              </td>
            </tr>
          ))}
          {rows.length === 0 && (
            <tr><td colSpan="4" style={{ textAlign: "center", color: "#6b7280" }}>No pending incidents</td></tr>
          )}
        </tbody>
      </table>
      {msg && <p className="success">{msg}</p>}
    </div>
  );
}