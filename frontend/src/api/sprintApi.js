import http from "./http";

export async function listSprints(projectId) {
  const { data } = await http.get(`/sprints/project/${projectId}`);
  return data;
}

export async function createSprint(projectId, sprint) {
  // sprint: { name, goal, startDate, endDate }
  const { data } = await http.post(`/sprints/project/${projectId}`, sprint);
  return data;
}

export async function deleteSprint(sprintId) {
  const { data } = await http.delete(`/sprints/${sprintId}`);
  return data;
}
