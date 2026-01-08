import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import RequireAuth from "./app/routing/RequireAuth";
import AppShell from "./app/layout/AppShell";

import SignInPage from "./pages/SignInPage";
import SignUpPage from "./pages/SignUpPage";
import Dashboard from "./pages/Dashboard";
import CoursePage from "./pages/CoursePage";
import ProjectPage from "./pages/ProjectPage";
import BadgesPage from "./pages/BadgesPage";
import LeaderboardsPage from "./pages/LeaderboardsPage";
import ProfilePage from "./pages/ProfilePage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/signin" element={<SignInPage />} />
          <Route path="/signup" element={<SignUpPage />} />

          <Route
            element={
              <RequireAuth>
                <AppShell />
              </RequireAuth>
            }
          >
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/courses/:courseId" element={<CoursePage />} />
            <Route path="/projects/:projectId" element={<ProjectPage />} />
            <Route path="/badges" element={<BadgesPage />} />
            <Route path="/leaderboards" element={<LeaderboardsPage />} />
            <Route path="/profile" element={<ProfilePage />} />
          </Route>

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
