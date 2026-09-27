export function useAuth() {
  const token = localStorage.getItem("dpdms_token");
  return {
    isAuthed: !!token,
    role: localStorage.getItem("dpdms_role") || "",
    ward: localStorage.getItem("dpdms_ward") || "",
    logout() {
      localStorage.clear();
      window.location.href = "/login";
    }
  };
}