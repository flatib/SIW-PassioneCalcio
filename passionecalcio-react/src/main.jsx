// src/main.jsx
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import "./index.css";

const container = document.getElementById("react-root");

if (container) {
  const viewToRender = container.dataset.view; 
  const isAdmin = container.dataset.isAdmin === "true";

  ReactDOM.createRoot(container).render(
    <React.StrictMode>
      <App view={viewToRender} isAdmin={isAdmin} />
    </React.StrictMode>
  );
}