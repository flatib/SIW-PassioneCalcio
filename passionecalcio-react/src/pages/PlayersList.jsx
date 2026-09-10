import { useState, useEffect, useMemo } from "react";

export default function PlayersList({ isAdmin = false }) {
  const [players, setPlayers] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedRole, setSelectedRole] = useState("");
  const [onlyFreeAgents, setOnlyFreeAgents] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    fetchPlayers();
  }, []);

  const fetchPlayers = async (pageIndex = 0) => {
    try {
      setLoading(true);
      setError("");

      const params = new URLSearchParams({
        page: pageIndex,
        size: 10,
        ...(searchTerm && { search: searchTerm }),
        ...(selectedRole && { role: selectedRole }),
        ...(onlyFreeAgents && { onlyFreeAgents: true })
      });

      const response = await fetch(`/rest/players?${params.toString()}`);

      if (!response.ok) {
        throw new Error(`Errore server: ${response.status}`);
      }

      const data = await response.json();

      if (pageIndex === 0) {
        setPlayers(data.content || []);
      } else {
        setPlayers((prev) => [...prev, ...(data.content || [])]);
      }

      setPage(pageIndex);
      setHasMore(!data.last);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const availableRoles = ["Portiere", "Difensore", "Centrocampista", "Attaccante"];

  const resetFilters = () => {
    setSearchTerm("");
    setSelectedRole("");
  };

  return (
    <div className="react-page-container">
      <div className="react-page-header">
        <div>
          <h1 className="react-page-title">Tesserati</h1>
          <p className="react-page-subtitle">
            Cerca i giocatori per nome, cognome, squadra o ruolo.
          </p>
        </div>

        {isAdmin && (
          <a href="/admin/players/new" className="react-button-primary">
            Aggiungi giocatore
          </a>
        )}
      </div>

      <div className="react-toolbar">
        <h3 className="react-toolbar-title">Ricerca e filtri</h3>

        <div className="react-toolbar-grid react-toolbar-grid--players">
          <div className="react-field">
            <label htmlFor="searchPlayer">
              Nome, cognome o squadra
            </label>
            <input
              id="searchPlayer"
              type="text"
              className="react-input"
              placeholder="Cerca giocatore o squadra..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="react-field">
            <label htmlFor="playerRole">Ruolo</label>
            <select
              id="playerRole"
              className="react-select"
              value={selectedRole}
              onChange={(e) => setSelectedRole(e.target.value)}
            >
              <option value="">Tutti i ruoli</option>
              {availableRoles.map((role) => (
                <option key={role} value={role}>
                  {role}
                </option>
              ))}
            </select>
          </div>

          <div className="react-checkbox-field">
            <input
              type="checkbox"
              id="onlyFreeAgents"
              checked={onlyFreeAgents}
              onChange={(e) => setOnlyFreeAgents(e.target.checked)}
            />
            <label htmlFor="onlyFreeAgents">Svincolati</label>
          </div>

          <button
            type="button"
            className="react-button-primary"
            onClick={() => fetchPlayers(0)}
            disabled={loading}
          >
            Cerca
          </button>

          <button
            type="button"
            className="react-button-secondary"
            onClick={() => {
              setSearchTerm("");
              setSelectedRole("");
              setTimeout(() => fetchPlayers(0), 0);
            }}
          >
            Reset
          </button>
        </div>
      </div>

      {loading && <p>Caricamento Giocatori...</p>}

      {error && <p className="error-text">{error}</p>}

      {!loading && !error && players.length === 0 && (
        <p>Nessun giocatore trovato con i filtri selezionati.</p>
      )}

      {!loading && !error && players.length > 0 && (
        <div className="react-list-section">
          <div className="react-list-header">
            <p>
              Giocatori trovati: <strong>{players.length}</strong>
            </p>
          </div>

          <div className="react-table-wrapper">
            <table className="react-table">
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>Cognome</th>
                  <th>Data di nascita</th>
                  <th>Ruolo</th>
                  <th>Squadra</th>
                  {isAdmin && <th>Azioni</th>}
                </tr>
              </thead>
              <tbody>
                {players.map((player) => (
                  <tr key={player.id}>
                    <td>{player.name}</td>
                    <td>{player.surname}</td>
                    <td>{player.dateOfBirth || "N/D"}</td>
                    <td>{player.role || "N/D"}</td>
                    <td>{player.team ? player.team.name : "Svincolato"}</td>
                    {isAdmin && (
                      <td>
                        <button
                          type="button"
                          className="react-button-secondary"
                          onClick={() => {
                            window.location.href = `/admin/players/${player.id}/edit`;
                          }}
                        >
                          Modifica
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {hasMore && (
            <div style={{ textAlign: "center", marginTop: "24px" }}>
              <button
                type="button"
                className="react-button-secondary"
                onClick={() => fetchPlayers(page + 1)}
                disabled={loading}
              >
                {loading ? "Caricamento..." : "Carica altri giocatori"}
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}