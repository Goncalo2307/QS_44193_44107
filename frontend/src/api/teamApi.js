import http from "./http";

export async function getTeamByProject(projectId) {
  const { data } = await http.get(`/teams/project/${projectId}`);
  return data; // list or object depending on backend; we normalize in UI
}

export async function createTeam(projectId, teamName) {
  const { data } = await http.post(`/teams/project/${projectId}`, null, { params: { teamName } });
  return data;
}

export async function listMembers(teamId) {
  const { data } = await http.get(`/teams/${teamId}/members`);
  return data;
}

export async function addMember(teamId, userId, role) {
  const { data } = await http.post(`/teams/${teamId}/member`, null, { params: { userId, role } });
  return data;
}

export async function removeMember(teamMemberId) {
  const { data } = await http.delete(`/teams/member/${teamMemberId}`);
  return data;
}

export async function getTeamLeaderboard() {
  const { data } = await http.get("/teams/leaderboard");
  return data;
}
