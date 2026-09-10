import { useState, useEffect, useMemo } from "react";

export default function TournamentsList({ isAdmin = false }) {
  const [tournaments, setTournaments] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedYear, setSelectedYear] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [availableYears, setAvailableYears] = useState([]);

  useEffect(() => {
    fetchFilterOptions();
    fetchTournaments(0);
  }, []);

  const fetchFilterOptions = async () => {
    try {
      const [yearsRes] = await Promise.all([
        fetch("/rest/tournaments/years")
      ]);
      if (yearsRes.ok) setAvailableYears(await yearsRes.json());
    } catch (err) {
      console.error("Errore nel caricamento delle opzioni di filtro:", err);
    }
  };

  const fetchTournaments = async (pageIndex = 0) => {
    try {
      setLoading(true);
      setError("");

      const params = new URLSearchParams({
        page: pageIndex,
        size: 12,
        ...(searchTerm && { search: searchTerm }),
        ...(selectedYear && { year: selectedYear })
      });

      const response = await fetch(`/rest/tournaments?${params.toString()}`);

      if (!response.ok) {
        throw new Error("Errore nel caricamento dei tornei.");
      }

      const data = await response.json();

      if (pageIndex === 0) {
        setTournaments(data.content || []);
      } else {
        setTournaments((prev) => [...prev, ...(data.content || [])]);
      }

      setPage(pageIndex);
      setHasMore(!data.last);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getTournamentImage = (tournament) => {
    return tournament.imageUrl || "https://placehold.co/600x350?text=Torneo";
  };

  const resetFilters = () => {
    setSearchTerm("");
    setSelectedYear("");
  };

  return (
    <div className="react-page-container">
      <div className="react-page-header">
        <div>
          <h1 className="react-page-title">Tornei Disponibili</h1>
          <p className="react-page-subtitle">
            Esplora i tornei e apri il dettaglio di ciascuna stagione.
          </p>
        </div>

        {isAdmin && (
          <a href="/admin/tournaments/new" className="react-button-primary">
            Nuovo Torneo
          </a>
        )}
      </div>

      <div className="react-toolbar">
        <h3 className="react-toolbar-title">Ricerca e filtri</h3>

        <div className="react-toolbar-grid">
          <div className="react-field">
            <label htmlFor="searchName">Nome torneo</label>
            <input
              id="searchName"
              type="text"
              className="react-input"
              placeholder="Cerca per nome..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="react-field">
            <label htmlFor="seasonYear">Anno stagione</label>
            <select
              id="seasonYear"
              className="react-select"
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value)}
            >
              <option value="">Tutte le stagioni</option>
              {availableYears.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </select>
          </div>

          <button
            type="button"
            className="react-button-primary"
            onClick={() => fetchTournaments(0)}
            disabled={loading}
          >
            Cerca
          </button>
          <button
            type="button"
            className="react-button-secondary"
            onClick={() => {
              resetFilters();
              setTimeout(() => fetchTournaments(0), 0);
            }}
          >
            Reset
            </button>
        </div>
      </div>

      {loading && <p>Caricamento tornei in corso...</p>}
      {error && <p className="error-text">{error}</p>}

      {!loading && !error && tournaments.length === 0 && (
        <p>Nessun torneo trovato con i filtri selezionati.</p>
      )}

      {!loading && !error && tournaments.length > 0 && (
        <>
          <div className="react-results-grid">
            {tournaments.map((tournament) => (
              <a
                key={tournament.id}
                href={`/tournaments/${tournament.id}`}
                className="react-card-link"
              >
                <div className="react-card">
                  <img
                    src={getTournamentImage(tournament)}
                    alt={tournament.name}
                    className="react-card-image"
                  />
                  <div className="react-card-body">
                    <h3 className="react-card-title">{tournament.name}</h3>
                    <p className="react-card-text">
                      Stagione {tournament.year || "N/D"}
                    </p>
                  </div>
                </div>
              </a>
            ))}
          </div>
          {hasMore && (
            <div style={{ textAlign: "center", marginTop: "24px" }}>
              <button
                type="button"
                className="react-button-secondary"
                onClick={() => fetchTournaments(page + 1)}
                disabled={loading}
              >
                {loading ? "Caricamento..." : "Carica altri tornei"}
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}