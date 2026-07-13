import api from "./api";

function normalizeNumber(value) {
  if (value === "" || value === null || value === undefined) return null;
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
}

export async function searchAnnunci({ nome = "", categoria = null, prezzo = null } = {}) {
  const params = {};
  const nomePulito = nome.trim();
  const prezzoNormalizzato = normalizeNumber(prezzo);

  if (nomePulito) params.nome = nomePulito;
  if (categoria) params.categoria = categoria;
  if (prezzoNormalizzato !== null) params.prezzo = prezzoNormalizzato;

  const response = await api.get("/rest/annunci/search", { params });
  return response.data;
}
