import axios from "axios";

/**
 * Centralized HTTP client.
 * - Base URL configurable via REACT_APP_API_URL (defaults to localhost backend)
 * - JWT injected automatically (if present)
 */
const http = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem("eduscrum.token");
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default http;
