import { useState } from "react";
import api from "../api/client";

export default function Reports() {
  const [form, setForm] = useState({
    hazard: "", ward: "", district: "", fromDate: "", toDate: "", severity: ""
  });
  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setMsg(""); setErr("");
    const format = e.nativeEvent.submitter.value;
    try {
      const params = new URLSearchParams();
      Object.entries(form).forEach(([k, v]) => { if (v) params.append(k, v); });
      const res = await api.get(`/api/reports/${format}?${params}`, { responseType: "blob" });
      const url = URL.createObjectURL(res.data);
      const a = document.createElement("a");
      a.href = url; a.download = `dpdms-report.${format.toLowerCase()}`;
      document.body.appendChild(a); a.click(); a.remove();
      URL.revokeObjectURL(url);
      setMsg(`Downloaded (${res.data.size} bytes)`);
    } catch (x) {
      const status = x?.response?.status || "network error";
      let detail = "";
      try {
        const blob = x.response.data;
        if (blob instanceof Blob) {
          detail = await blob.text();
        }
      } catch {}
      setErr(`Failed: HTTP ${status}${detail ? " — " + detail.slice(0, 200) : ""}`);
    }
  };

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  return (
    <form className="card" onSubmit={submit} style={{ maxWidth: 720 }}>
      <h1>Reports</h1>
      <label>Hazard</label>
      <select value={form.hazard} onChange={e => set("hazard", e.target.value)}>
        <option value="">All</option>
        {["FLOOD","DROUGHT","FIRE","ZOONOTIC_DISEASE","MINING_ACCIDENT"].map(h =>
          <option key={h}>{h}</option>)}
      </select>

      <label>Ward (leave blank for all)</label>
      <input value={form.ward} onChange={e => set("ward", e.target.value)} placeholder="e.g. Ward A" />

      <label>District (leave blank for all)</label>
      <input value={form.district} onChange={e => set("district", e.target.value)} placeholder="e.g. Rushinga" />

      <label>From (leave blank for all)</label>
      <input type="date" value={form.fromDate} onChange={e => set("fromDate", e.target.value)} />

      <label>To (leave blank for all)</label>
      <input type="date" value={form.toDate} onChange={e => set("toDate", e.target.value)} />

      <label>Severity</label>
      <select value={form.severity} onChange={e => set("severity", e.target.value)}>
        <option value="">All</option>
        {["LOW","MODERATE","HIGH","CRITICAL"].map(s => <option key={s}>{s}</option>)}
      </select>

      <div style={{ marginTop: 16, display: "flex", gap: 8, flexWrap: "wrap" }}>
        <button type="submit" value="pdf">Download PDF</button>
        <button type="submit" value="docx">Download DOCX</button>
        <button type="submit" value="xlsx">Download XLSX</button>
        <button type="submit" value="csv">Download CSV</button>
      </div>
      {msg && <p className="success">{msg}</p>}
      {err && <p className="error">{err}</p>}
    </form>
  );
}