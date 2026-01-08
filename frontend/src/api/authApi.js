import http from "./http";

export async function signIn(email, password) {
  const { data } = await http.post("/auth/signin", { email, password });
  return data;
}

export async function signUp(payload) {
  // payload: { name, email, password, role }
  const { data } = await http.post("/auth/signup", payload);
  return data;
}
