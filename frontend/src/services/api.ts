import axios, { type AxiosInstance } from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add request interceptor to include userId
api.interceptors.request.use((config) => {
  const userId = localStorage.getItem("userId") || "user01";
  config.headers.userId = userId;
  return config;
});

export default api;
