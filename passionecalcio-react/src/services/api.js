import axios from "axios";

const API_BASE_URL = import.meta.env?.VITE_API_BASE_URL || "";

const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const message = status
      ? `Richiesta API fallita con stato ${status}`
      : "Richiesta API non riuscita";

    return Promise.reject(new Error(message, { cause: error }));
  }
);

export default api;
