import { useState, useEffect, useMemo } from "react";

export default function PlayersList({ isAdmin = false }) {
  const [players, setPlayers] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedRole, setSelectedRole] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchPlayers();
  }, []);

  const fetchPlayers = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await fetch("/rest/players");
      const text = await response.text();

      if (!response.ok) {
        throw new Error(`Errore server: ${response.status}`);
      }

      const data = JSON.parse(text);
      setPlayers(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const availableRoles = useMemo(() => {
    return [...new Set(players.map((player) => player.role).filter(Boolean))].sort(
      (a, b) => a.localeCompare(b)
    );
  }, [players]);

  const filteredPlayers = useMemo(() => {
    const normalizedSearch = searchTerm.toLowerCase().trim();

    return players.filter((player) => {
      const fullText = [
        player.name || "",
        player.surname || "",
        player.team?.name || "",
      ]
        .join(" ")
        .toLowerCase();

      const matchesSearch = normalizedSearch
        ? fullText.includes(normalizedSearch)
        : true;

      const matchesRole = selectedRole
        ? player.role === selectedRole
        : true;

      return matchesSearch && matchesRole;
    });
  }, [players, searchTerm, selectedRole]);

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

          <button
            type="button"
            className="react-button-secondary"
            onClick={resetFilters}
          >
            Reset
          </button>
        </div>
      </div>

      {loading && <p>Caricamento Giocatori...</p>}

      {error && <p className="error-text">{error}</p>}

      {!loading && !error && filteredPlayers.length === 0 && (
        <p>Nessun giocatore trovato con i filtri selezionati.</p>
      )}

      {!loading && !error && filteredPlayers.length > 0 && (
        <div className="react-list-section">
          <div className="react-list-header">
            <p>
              Giocatori trovati: <strong>{filteredPlayers.length}</strong>
            </p>
          </div>

          <div className="react-table-wrapper">
            <table className="react-table">
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>Cognome</th>
                  <th>Ruolo</th>
                  <th>Squadra</th>
                </tr>
              </thead>
              <tbody>
                {filteredPlayers.map((player) => (
                  <tr key={player.id}>
                    <td>{player.name}</td>
                    <td>{player.surname}</td>
                    <td>{player.role || "N/D"}</td>
                    <td>{player.team ? player.team.name : "Svincolato"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}