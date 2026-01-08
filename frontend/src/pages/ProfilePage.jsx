import React from "react";
import { useAuth } from "../auth/AuthContext";
import "../styles/pages.css";

export default function ProfilePage() {
  const { user } = useAuth();

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <h2>Perfil</h2>
          <div className="muted">Informação básica da sessão atual.</div>
        </div>
      </div>

      <div className="card">
        <div className="kv">
          <div className="k">Nome</div>
          <div className="v">{user?.name || "-"}</div>
        </div>
        <div className="kv">
          <div className="k">Email</div>
          <div className="v">{user?.email || "-"}</div>
        </div>
        <div className="kv">
          <div className="k">Roles</div>
          <div className="v">{(user?.roles || []).join(", ") || "-"}</div>
        </div>
        <div className="kv">
          <div className="k">User ID</div>
          <div className="v">{user?.id ?? "-"}</div>
        </div>
      </div>
    </div>
  );
}
