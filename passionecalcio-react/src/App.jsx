// src/App.jsx
import TournamentsList from "./pages/TournamentsList";
import TeamsList from "./pages/TeamsList";
import PlayersList from "./pages/PlayersList";
import MatchesList from "./pages/MatchesList";

function App({ view, isAdmin }) {
  switch (view) {
    case "tournaments":
      return <TournamentsList isAdmin={isAdmin} />;
    case "teams":
      return <TeamsList isAdmin={isAdmin} />;
    case "players":
      return <PlayersList isAdmin={isAdmin} />;
    case "matches":
      return <MatchesList isAdmin={isAdmin} />;
    default:
      return <div className="error-text">Errore: Nessuna vista React specificata.</div>;
  }
}

export default App;