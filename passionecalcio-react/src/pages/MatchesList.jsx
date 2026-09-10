import { useState, useEffect, useMemo } from "react";

export default function MatchesList({ isAdmin = false }) {
  const [matches, setMatches] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    fetchMatches(0);
  }, []);

  const fetchMatches = async (pageIndex = 0) => {
    try {
      setLoading(true);
      setError("");

      const params = new URLSearchParams({
        page: pageIndex,
        size: 10,
        ...(searchTerm && { search: searchTerm }),
        ...(startDate && { startDate }),
        ...(endDate && { endDate })
      });

      const response = await fetch(`/rest/matches?${params.toString()}`);

      if (!response.ok) {
        throw new Error("Errore nel caricamento del calendario.");
      }

      const data = await response.json();

      if (pageIndex === 0) {
        setMatches(data.content || []);
      } else {
        setMatches((prev) => [...prev, ...(data.content || [])]); // Accoda all'array esistente
      }

      setPage(pageIndex);
      setHasMore(!data.last);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const normalizeDate = (value) => {
    if (!value) return "";
    return value.slice(0, 10);
  };

  const resetFilters = () => {
    setSearchTerm("");
    setStartDate("");
    setEndDate("");
    setTimeout(() => fetchMatches(0), 0); // Resetta i filtri e ricarica le partite
  };

  const formatDateTime = (value) => {
    if (!value) return "N/D";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) return value;

    return date.toLocaleString("it-IT", {
      dateStyle: "short",
      timeStyle: "short",
    });
  };

  const handleDelete = async (matchId) => {
    const confirmed = window.confirm("Sei sicuro di voler eliminare questa partita?");
    if (!confirmed) return;

    const response = await fetch(`/rest/matches/${matchId}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error("Errore durante l'eliminazione della partita.");
    }

    setMatches((prev) => prev.filter((match) => match.id !== matchId));
  };

  return (
    <div className="react-page-container">
      <div className="react-page-header">
        <div>
          <h1 className="react-page-title">Calendario Partite</h1>
          <p className="react-page-subtitle">
            Cerca per squadre o torneo e filtra le partite per intervallo di date.
          </p>
        </div>

        {isAdmin && (
          <a href="/admin/matches/new" className="react-button-primary">
            + Crea Nuova Partita
          </a>
        )}
      </div>

      <div className="react-toolbar">
        <h3 className="react-toolbar-title">Ricerca e filtri</h3>

        <div className="react-toolbar-grid react-toolbar-grid--matches">
          <div className="react-field">
            <label htmlFor="matchSearch">Squadre o torneo</label>
            <input
              id="matchSearch"
              type="text"
              className="react-input"
              placeholder="Cerca per squadre o torneo..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="react-field">
            <label htmlFor="startDate">Da data</label>
            <input
              id="startDate"
              type="date"
              className="react-input"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
          </div>

          <div className="react-field">
            <label htmlFor="endDate">A data</label>
            <input
              id="endDate"
              type="date"
              className="react-input"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>

          <button
            type="button"
            className="react-button-primary"
            onClick={() => fetchMatches(0)}
            disabled={loading}
          >
            Cerca
          </button>

          <button
            type="button"
            className="react-button-secondary"
            onClick={resetFilters}
          >
            Reset
          </button>

        </div>
      </div>

      {loading && <p>Caricamento calendario partite...</p>}

      {error && <p className="error-text">{error}</p>}

      {!loading && !error && matches.length === 0 && (
        <p>Nessuna partita trovata con i filtri selezionati.</p>
      )}

      {!loading && !error && matches.length > 0 && (
        <div className="react-list-section">
          <div className="react-list-header">
            <p>Partite trovate: <strong>{matches.length}</strong></p>
          </div>

          <div className="react-table-wrapper">
            <table className="react-table">
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Torneo</th>
                  <th>Casa</th>
                  <th>Trasferta</th>
                  <th>Stato</th>
                  <th>Risultato</th>
                  {isAdmin && <th>Azioni</th>}
                </tr>
              </thead>
              <tbody>
                {matches.map((match) => (
                  <tr key={match.id}
                  onClick={() => window.location.href = `/matches/${match.id}`}
                  style={{ cursor: 'pointer' }}
                  >
                    <td>{formatDateTime(match.dateAndTime || match.date)}</td>
                    <td>{match.tournament?.name || "N/D"}</td>
                    <td>{match.homeTeam?.name || "N/D"}</td>
                    <td>{match.awayTeam?.name || "N/D"}</td>
                    <td>{match.status || "N/D"}</td>
                    <td>
                      {match.goalsHome != null && match.goalsAway != null
                        ? `${match.goalsHome} - ${match.goalsAway}`
                        : "—"}
                    </td>
                    {isAdmin && (
                      <td>
                        <button
                          type="button"
                          className="react-button-danger"
                          onClick={() => handleDelete(match.id)}
                        >
                          Elimina
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
                onClick={() => fetchMatches(page + 1)}
                disabled={loading}
              >
                {loading ? "Caricamento..." : "Carica altre partite"}
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}