import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Shield } from "lucide-react";
import api from "../api/client";

export default function Login() {
  const [username, setU] = useState("");
  const [password, setP] = useState("");
  const [err, setErr] = useState("");
  const nav = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setErr("");
    try {
      const { data } = await api.post("/auth/login", { username, password });
      localStorage.setItem("dpdms_token", data.token);
      localStorage.setItem("dpdms_role", data.role);
      localStorage.setItem("dpdms_ward", data.ward || "");
      localStorage.setItem("dpdms_user", username);
      nav("/dashboard");
    } catch (x) {
      setErr(x?.response?.data?.message || "Login failed");
    }
  };

  return (
    <div className="login-shell">
      <form className="login-card" onSubmit={submit}>
        <div className="login-logo">
          <div className="badge"><Shield size={28} /></div>
          <h1>DPDMS</h1>
          <p>Rushinga Provincial Disaster Monitoring</p>
        </div>

        <label>Username</label>
        <input value={username} onChange={e => setU(e.target.value)}
               placeholder="Enter your username" required />

        <label>Password</label>
        <input type="password" value={password} onChange={e => setP(e.target.value)}
               placeholder="Enter your password" required />

        <button type="submit">Sign In</button>

        {err && <p className="error" style={{ marginTop: 16 }}>{err}</p>}

        <p className="login-hint">
          Demo users (password: <code>password</code>):<br />
          flood.recorder.wardA · drought.recorder.wardA<br />
          flood.supervisor · national.user
        </p>
      </form>
    </div>
  );
}