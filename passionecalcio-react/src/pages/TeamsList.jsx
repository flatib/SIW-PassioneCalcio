import { useState, useEffect, useMemo } from "react";

export default function TeamsList({ isAdmin = false }) {
  const [teams, setTeams] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedYear, setSelectedYear] = useState("");
  const [selectedCity, setSelectedCity] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [availableCities, setAvailableCities] = useState([]);
  const [availableYears, setAvailableYears] = useState([]);
  const [selectedTeams, setSelectedTeams] = useState([]);

  useEffect(() => {
    fetchFilterOptions();
    fetchTeams(0);
  }, []);

  const fetchFilterOptions = async () => {
    try {
      const [citiesRes, yearsRes] = await Promise.all([
        fetch("/rest/teams/cities"),
        fetch("/rest/teams/years")
      ]);
      if (citiesRes.ok) setAvailableCities(await citiesRes.json());
      if (yearsRes.ok) setAvailableYears(await yearsRes.json());
    } catch (err) {
      console.error("Errore nel caricamento dei filtri", err);
    }
  };

  const fetchTeams = async (pageIndex = 0) => {
    try {
      setLoading(true);
      setError("");

      const params = new URLSearchParams({
        page: pageIndex,
        size: 12,
        ...(searchTerm && { search: searchTerm }),
        ...(selectedYear && { year: selectedYear }),
        ...(selectedCity && { city: selectedCity })
      });

      const response = await fetch(`/rest/teams?${params.toString()}`);

      if (!response.ok) {
        throw new Error("Errore nel caricamento delle squadre.");
      }

      const data = await response.json();

      if (pageIndex === 0) {
        setTeams(data.content || []);
      } else {
        setTeams((prev) => [...prev, ...(data.content || [])]);
      }

      setPage(pageIndex);
      setHasMore(!data.last);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getTeamImage = (team) => {
    return team.imageUrl || "https://placehold.co/600x350?text=Squadra";
  };

  const resetFilters = () => {
    setSearchTerm("");
    setSelectedYear("");
    setSelectedCity("");
  };

  const handleToggleSelect = (teamId) => {
    setSelectedTeams((prev) => {
      if (prev.includes(teamId)) {
        return prev.filter((id) => id !== teamId); // Rimuove se già presente
      } else {
        return [...prev, teamId]; // Aggiunge se non presente
      }
    });
  };

  const handleDeleteSelected = async () => {
    if (!window.confirm(`Sei sicuro di voler eliminare ${selectedTeams.length} squadre?`)) return;

    try {
        const response = await fetch(`/rest/admin/teams/bulk`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(selectedTeams) 
        });

        if (response.ok) {
            setSelectedTeams([]);
            fetchTeams(0);
        } else {
            alert("Impossibile eliminare. Assicurati che le squadre non abbiano giocatori o tornei associati.");
            console.error("Errore dal server durante l'eliminazione");
        }
    } catch (error) {
        console.error("Errore di rete durante l'eliminazione", error);
    }
  };

  return (
    <div className="react-page-container">
      <div className="react-page-header">
        <div>
          <h1 className="react-page-title">Squadre Iscritte</h1>
          <p className="react-page-subtitle">
            Consulta le squadre partecipanti e apri il dettaglio di ciascuna.
          </p>
        </div>
          {isAdmin && (
            <a href="/admin/teams/new" className="react-button-primary">
              Nuova Squadra
            </a>
          )}

      </div>

      <div className="react-toolbar">
        <h3 className="react-toolbar-title">Ricerca e filtri</h3>

        <div
          className="react-toolbar-grid react-toolbar-grid--teams"
        >
          <div className="react-field">
            <label htmlFor="searchTeamName">Nome squadra</label>
            <input
              id="searchTeamName"
              type="text"
              className="react-input"
              placeholder="Cerca per nome..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="react-field">
            <label htmlFor="foundationYear">Anno fondazione</label>
            <select
              id="foundationYear"
              className="react-select"
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value)}
            >
              <option value="">Tutti gli anni</option>
              {availableYears.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </select>
          </div>

          <div className="react-field">
            <label htmlFor="teamCity">Città</label>
            <select
              id="teamCity"
              className="react-select"
              value={selectedCity}
              onChange={(e) => setSelectedCity(e.target.value)}
            >
              <option value="">Tutte le città</option>
              {availableCities.map((city) => (
                <option key={city} value={city}>
                  {city}
                </option>
              ))}
            </select>
          </div>

          <button
            type="button"
            className="react-button-primary"
            onClick={() => fetchTeams(0)}
            disabled={loading}
          >
            Cerca
          </button>

          <button
            type="button"
            className="react-button-secondary"
            onClick={() => {
              setSearchTerm("");
              setSelectedYear("");
              setSelectedCity("");
              setTimeout(() => fetchTeams(0), 0);
            }}
          >
            Reset
          </button>
        </div>
      </div>

      {isAdmin && selectedTeams.length > 0 && (
            <button
              className="react-button-danger"
              onClick={handleDeleteSelected}
            >
              Elimina selezionati ({selectedTeams.length})
            </button>
          )}

      {loading && <p>Caricamento squadre...</p>}

      {error && <p className="error-text">{error}</p>}

      {!loading && !error && teams.length === 0 && (
        <p>Nessuna squadra trovata con i filtri selezionati.</p>
      )}

      {!loading && !error && teams.length > 0 && (
        <>
          <div className="react-results-grid">
            {teams.map((team) => (
              <a
                key={team.id}
                href={`/teams/${team.id}`}
                className="react-card-link"
              >
                <div className="react-card" style={{ position: 'relative' }}>
                  {isAdmin && (
                    <input
                      type="checkbox"
                      style={{
                        position: 'absolute',
                        top: '10px',
                        left: '10px',
                        zIndex: 10,
                        transform: 'scale(1.5)'
                      }}
                      checked={selectedTeams.includes(team.id)}
                      onChange={() => handleToggleSelect(team.id)}
                      onClick={(e) => e.stopPropagation()}
                    />
                  )}
                  <img
                    src={getTeamImage(team)}
                    alt={team.name}
                    className="react-card-image"
                  />

                  <div className="react-card-body">
                    <h3 className="react-card-title">{team.name}</h3>
                    <p className="react-card-text">
                      Fondata nel: {team.foundationYear || "N/D"}
                    </p>
                    <p className="react-card-text">
                      Città: {team.city || "N/D"}
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
                onClick={() => fetchTeams(page + 1)}
                disabled={loading}
              >
                {loading ? "Caricamento..." : "Carica altre squadre"}
              </button>
            </div>
          )}
        </>
      )}
    </div >
  );
}