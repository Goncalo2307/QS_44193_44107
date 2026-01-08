import http from "./http";

export async function listCourses() {
  const { data } = await http.get("/courses");
  return data;
}

export async function getCourse(id) {
  const { data } = await http.get(`/courses/${id}`);
  return data;
}

export async function createCourse({ name, description }) {
  const { data } = await http.post("/courses", { name, description });
  return data;
}
