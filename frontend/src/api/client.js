import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080" });

api.interceptors.request.use(cfg => {
  const t = localStorage.getItem("dpdms_token");
  if (t) cfg.headers.Authorization = `Bearer ${t}`;
  return cfg;
});

api.interceptors.response.use(
  r => r,
  err => {
    if (err?.response?.status === 401) {
      localStorage.clear();
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(err);
  }
);

export default api;