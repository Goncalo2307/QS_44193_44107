import http from "./http";

export async function listProjectsByCourse(courseId) {
  const { data } = await http.get(`/projects/course/${courseId}`);
  return data;
}

export async function getProject(projectId) {
  const { data } = await http.get(`/projects/${projectId}`);
  return data;
}

export async function createProject({ name, description, courseId }) {
  const { data } = await http.post("/projects", { name, description, courseId });
  return data;
}
