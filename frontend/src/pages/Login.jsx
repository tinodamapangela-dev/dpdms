import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
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
      const role = data.role || "";
      if (role.endsWith("_RECORDER")) nav("/incidents");
      else if (role.endsWith("_SUPERVISOR")) nav("/pending");
      else nav("/dashboard");
    } catch (x) {
      setErr(x?.response?.data?.message || "Login failed");
    }
  };

  return (
    <div className="login-shell">
      <form className="login-card" onSubmit={submit}>
        <div className="login-badge">🔒</div>
        <h2>Login Now</h2>

        <label>Username *</label>
        <input
          value={username}
          onChange={e => setU(e.target.value)}
          placeholder="Enter your Username"
          required
        />

        <label>Password *</label>
        <input
          type="password"
          value={password}
          onChange={e => setP(e.target.value)}
          placeholder="Enter your Password"
          required
        />

        <button type="submit">Login</button>

        <div className="login-links">
          <a href="#">Don&apos;t have an account?</a>
          <a href="#">Forgot password?</a>
        </div>

        {err && <p className="error" style={{ marginTop: 16 }}>{err}</p>}

        <p className="login-hint">
          Demo users (password: <code>password</code>):<br />
          flood.recorder.wardA · drought.recorder.wardA<br />
          flood.supervisor · drought.supervisor<br />
          national.user · provincial.admin
        </p>
      </form>
    </div>
  );
}