import api from "./api";

export async function getCategorie() {
  const response = await api.get("/rest/categorie");
  return response.data;
}
