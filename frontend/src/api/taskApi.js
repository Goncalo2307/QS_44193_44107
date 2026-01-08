import http from "./http";

export async function listTasksBySprint(sprintId) {
  const { data } = await http.get(`/tasks/sprint/${sprintId}`);
  return data;
}

export async function createTask(sprintId, task) {
  // task: { title, description, status, estimatedPoints }
  const { data } = await http.post(`/tasks/sprint/${sprintId}`, task);
  return data;
}

export async function updateTaskStatus(taskId, newStatus) {
  const { data } = await http.put(`/tasks/status/${taskId}`, null, { params: { newStatus } });
  return data;
}

export async function assignTask(taskId, userId) {
  const { data } = await http.put(`/tasks/assign/${taskId}/user/${userId}`);
  return data;
}
