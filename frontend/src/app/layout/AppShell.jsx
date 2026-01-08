import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";
import "../../styles/appShell.css";

function NavItem({ to, label }) {
  return (
    <NavLink to={to} className={({ isActive }) => (isActive ? "navItem active" : "navItem")}>
      {label}
    </NavLink>
  );
}

export default function AppShell() {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brandMark">EA</div>
          <div className="brandText">
            <div className="brandTitle">EduScrum</div>
            <div className="brandSubtitle">Awards</div>
          </div>
        </div>

        <nav className="nav">
          <NavItem to="/dashboard" label="Cursos" />
          <NavItem to="/badges" label="Distinções" />
          <NavItem to="/leaderboards" label="Rankings" />
          <NavItem to="/profile" label="Perfil" />
        </nav>

        <div className="sidebarFooter">
          <div className="userLine">
            <div className="userName">{user?.name || "Utilizador"}</div>
            <div className="userEmail">{user?.email || ""}</div>
          </div>
          <button
            className="btn danger"
            onClick={() => {
              signOut();
              navigate("/signin");
            }}
          >
            Sair
          </button>
        </div>
      </aside>

      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
