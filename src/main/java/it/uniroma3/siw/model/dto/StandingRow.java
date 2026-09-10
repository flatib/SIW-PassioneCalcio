package it.uniroma3.siw.model.dto;

import it.uniroma3.siw.model.Team;

public class StandingRow {

	private Team team;
	private int points = 0;
	private int played = 0;
	private int won = 0;
	private int drawn = 0;
	private int lost = 0;
	private int goalsFor = 0;
	private int goalsAgainst = 0;

	public StandingRow(Team team) {
		this.team = team;
	}

	public void updateStats(int gFor, int gAgainst) {
		this.played++;
		this.goalsFor += gFor;
		this.goalsAgainst += gAgainst;

		if (gFor > gAgainst) {
			this.won++;
			this.points += 3;
		} else if (gFor == gAgainst) {
			this.drawn++;
			this.points += 1;
		} else {
			this.lost++;
		}
	}

	public Team getTeam() {
		return team;
	}

	public int getPoints() {
		return points;
	}

	public int getPlayed() {
		return played;
	}

	public int getWon() {
		return won;
	}

	public int getDrawn() {
		return drawn;
	}

	public int getLost() {
		return lost;
	}

	public int getGoalsFor() {
		return goalsFor;
	}

	public int getGoalsAgainst() {
		return goalsAgainst;
	}

	public int getGoalDifference() {
		return goalsFor - goalsAgainst;
	}
}
