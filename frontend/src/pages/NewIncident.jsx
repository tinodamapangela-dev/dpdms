import { useState } from "react";
import api from "../api/client";
import { useAuth } from "../auth/useAuth";

const templates = {
  floods: ["peakWaterLevelM", "riverBasin", "householdsDisplaced", "areaFloodedHectares", "inundationDays"],
  droughts: ["rainfallDeficitMm", "consecutiveDryDays", "cropFailurePct", "peopleWaterShortage", "livestockMortality"],
  fires: ["areaBurnedHa", "suspectedCause", "injuries", "fatalities", "structuresDestroyed", "active"],
  zoonotic: ["pathogen", "animalSpecies", "confirmedHumanCases", "confirmedAnimalCases", "classification"],
  mining: ["mineName", "mineType", "accidentType", "trappedMiners", "injuredMiners", "fatalities", "rescueOngoing"]
};

export default function NewIncident() {
  const { ward } = useAuth();
  const [hazard, setHazard] = useState("floods");
  const [form, setForm] = useState({
    ward: ward || "",
    district: "Rushinga",
    occurredAt: new Date().toISOString().slice(0, 16),
    severity: "HIGH",
    latitude: -16.6,
    longitude: 31.9
  });
  const [extra, setExtra] = useState({});
  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));
  const setX = (k, v) => setExtra(x => ({ ...x, [k]: v }));

  const submit = async (e) => {
    e.preventDefault();
    setErr(""); setMsg("");
    try {
      const body = { ...form, ...extra };
      await api.post(`/api/${hazard}`, body);
      setMsg("Incident created with status PENDING");
      setExtra({});
    } catch (x) {
      setErr(x?.response?.data?.message || "Failed");
    }
  };

  return (
    <form className="card" onSubmit={submit} style={{ maxWidth: 700 }}>
      <h1>Report Incident</h1>
      <label>Hazard</label>
      <select value={hazard} onChange={e => { setHazard(e.target.value); setExtra({}); }}>
        <option value="floods">Flood</option>
        <option value="droughts">Drought</option>
        <option value="fires">Fire</option>
        <option value="zoonotic">Zoonotic Disease</option>
        <option value="mining">Mining Accident</option>
      </select>

      <label>Ward</label>
      <input value={form.ward} onChange={e => set("ward", e.target.value)} />

      <label>District</label>
      <input value={form.district} onChange={e => set("district", e.target.value)} />

      <label>Occurred</label>
      <input type="datetime-local" value={form.occurredAt} onChange={e => set("occurredAt", e.target.value)} />

      <label>Severity</label>
      <select value={form.severity} onChange={e => set("severity", e.target.value)}>
        {["LOW", "MODERATE", "HIGH", "CRITICAL"].map(s => <option key={s}>{s}</option>)}
      </select>

      <label>Latitude</label>
      <input type="number" step="0.0001" value={form.latitude} onChange={e => set("latitude", parseFloat(e.target.value))} />

      <label>Longitude</label>
      <input type="number" step="0.0001" value={form.longitude} onChange={e => set("longitude", parseFloat(e.target.value))} />

      <h3>{hazard.toUpperCase()} specific</h3>
      {templates[hazard].map(k => (
        <div key={k}>
          <label>{k}</label>
          <input value={extra[k] ?? ""} onChange={e => setX(k, e.target.value)} />
        </div>
      ))}

      <button type="submit" style={{ marginTop: 16 }}>Submit</button>
      {msg && <p className="success">{msg}</p>}
      {err && <p className="error">{err}</p>}
    </form>
  );
}