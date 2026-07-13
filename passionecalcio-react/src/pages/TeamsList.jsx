import { useState, useEffect, useMemo } from "react";

export default function TeamsList({ isAdmin = false }) {
  const [teams, setTeams] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedYear, setSelectedYear] = useState("");
  const [selectedCity, setSelectedCity] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchTeams();
  }, []);

  const fetchTeams = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await fetch("/rest/teams");

      if (!response.ok) {
        throw new Error("Errore nel caricamento delle squadre.");
      }

      const data = await response.json();
      setTeams(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getTeamImage = (team) => {
    return team.imageUrl || "https://placehold.co/600x350?text=Squadra";
  };

  const availableYears = useMemo(() => {
    return [...new Set(teams.map((team) => team.foundationYear).filter(Boolean))].sort(
      (a, b) => b - a
    );
  }, [teams]);

  const availableCities = useMemo(() => {
    return [...new Set(teams.map((team) => team.city).filter(Boolean))].sort(
      (a, b) => a.localeCompare(b)
    );
  }, [teams]);

  const filteredTeams = useMemo(() => {
    return teams.filter((team) => {
      const matchesName = team.name
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase());

      const matchesYear = selectedYear
        ? String(team.foundationYear) === selectedYear
        : true;

      const matchesCity = selectedCity
        ? team.city === selectedCity
        : true;

      return matchesName && matchesYear && matchesCity;
    });
  }, [teams, searchTerm, selectedYear, selectedCity]);

  const resetFilters = () => {
    setSearchTerm("");
    setSelectedYear("");
    setSelectedCity("");
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
          style={{ gridTemplateColumns: "2fr 1fr 1fr auto" }}
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
            className="react-button-secondary"
            onClick={resetFilters}
          >
            Reset
          </button>
        </div>
      </div>

      {loading && <p>Caricamento squadre...</p>}

      {error && <p className="error-text">{error}</p>}

      {!loading && !error && filteredTeams.length === 0 && (
        <p>Nessuna squadra trovata con i filtri selezionati.</p>
      )}

      {!loading && !error && filteredTeams.length > 0 && (
        <div className="react-results-grid">
          {filteredTeams.map((team) => (
            <a
              key={team.id}
              href={`/teams/${team.id}`}
              className="react-card-link"
            >
              <div className="react-card">
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
      )}
    </div>
  );
}