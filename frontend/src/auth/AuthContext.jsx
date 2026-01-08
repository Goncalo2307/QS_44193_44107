import React, { createContext, useContext, useMemo, useState } from "react";
import * as authApi from "../api/authApi";

const AuthContext = createContext(null);

function normalizeRoles(roles) {
  if (!roles) return [];
  if (Array.isArray(roles)) return roles;
  return [roles];
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem("eduscrum.user");
    return raw ? JSON.parse(raw) : null;
  });

  const isAuthenticated = !!localStorage.getItem("eduscrum.token");

  const value = useMemo(() => ({
    user,
    isAuthenticated,
    async signIn(email, password) {
      const data = await authApi.signIn(email, password);
      // expected: { token, id, name, email, roles }
      localStorage.setItem("eduscrum.token", data.token);
      const u = { id: data.id, name: data.name, email: data.email, roles: normalizeRoles(data.roles) };
      localStorage.setItem("eduscrum.user", JSON.stringify(u));
      setUser(u);
      return u;
    },
    async signUp({ name, email, password, role }) {
      // role should be ROLE_STUDENT/ROLE_TEACHER/ROLE_ADMIN
      return authApi.signUp({ name, email, password, role });
    },
    signOut() {
      localStorage.removeItem("eduscrum.token");
      localStorage.removeItem("eduscrum.user");
      setUser(null);
    },
    hasRole(role) {
      const roles = (user?.roles || []).map(String);
      return roles.includes(role);
    }
  }), [user, isAuthenticated]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
