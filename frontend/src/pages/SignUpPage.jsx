import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import "../styles/auth.css";

const ROLE_OPTIONS = [
  { value: "ROLE_STUDENT", label: "Aluno" },
  { value: "ROLE_TEACHER", label: "Professor" },
  { value: "ROLE_ADMIN", label: "Administrador" },
];

export default function SignUpPage() {
  const { signUp } = useAuth();
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("ROLE_STUDENT");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [ok, setOk] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setError("");
    setOk("");
    setBusy(true);
    try {
      const res = await signUp({ name, email, password, role });
      setOk(res?.message || "Conta criada com sucesso.");
      setTimeout(() => navigate("/signin"), 700);
    } catch (err) {
      setError(err?.response?.data?.message || "Não foi possível criar conta.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="authWrap">
      <div className="authCard">
        <h1>Criar conta</h1>
        <p className="muted">Registo rápido para entrares no teu curso e equipa Scrum.</p>

        <form onSubmit={onSubmit} className="form">
          <label>
            Nome
            <input value={name} onChange={(e) => setName(e.target.value)} required />
          </label>

          <label>
            Email
            <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
          </label>

          <label>
            Palavra-passe
            <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required />
          </label>

          <label>
            Perfil
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              {ROLE_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
          </label>

          {error ? <div className="errorBox">{error}</div> : null}
          {ok ? <div className="okBox">{ok}</div> : null}

          <button className="btn primary" disabled={busy}>
            {busy ? "A criar..." : "Criar"}
          </button>

          <button type="button" className="btn ghost" onClick={() => navigate("/signin")}>
            Já tenho conta
          </button>
        </form>
      </div>
    </div>
  );
}
