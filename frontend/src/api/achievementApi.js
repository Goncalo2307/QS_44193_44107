import http from "./http";

export async function listBadges() {
  const { data } = await http.get("/badges");
  return data;
}

export async function createBadge(badge) {
  // badge: { name, description, points }
  const { data } = await http.post("/badges", badge);
  return data;
}

export async function assignBadge(studentEmail, achievementId) {
  const { data } = await http.post("/badges/assign", { studentEmail, achievementId });
  return data;
}

export async function listStudentBadges(studentId) {
  const { data } = await http.get(`/badges/student/${studentId}`);
  return data;
}

export async function getLeaderboard() {
  const { data } = await http.get("/badges/leaderboard");
  return data;
}

export async function getAverage() {
  const { data } = await http.get("/badges/stats/average");
  return data;
}

export async function exportLeaderboardCsv() {
  const res = await http.get("/badges/leaderboard/export", { responseType: "blob" });
  return res.data;
}
