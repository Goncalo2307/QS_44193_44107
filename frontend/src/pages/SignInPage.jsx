import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import "../styles/auth.css";

export default function SignInPage() {
  const { signIn } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      await signIn(email, password);
      navigate("/dashboard");
    } catch (err) {
      setError(err?.response?.data?.message || "Não foi possível autenticar. Verifica as credenciais.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="authWrap">
      <div className="authCard">
        <h1>Entrar</h1>
        <p className="muted">Acede à plataforma para gerir cursos, sprints e distinções.</p>

        <form onSubmit={onSubmit} className="form">
          <label>
            Email
            <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
          </label>

          <label>
            Palavra-passe
            <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required />
          </label>

          {error ? <div className="errorBox">{error}</div> : null}

          <button className="btn primary" disabled={busy}>
            {busy ? "A autenticar..." : "Entrar"}
          </button>

          <button type="button" className="btn ghost" onClick={() => navigate("/signup")}>
            Criar conta
          </button>
        </form>
      </div>
    </div>
  );
}
