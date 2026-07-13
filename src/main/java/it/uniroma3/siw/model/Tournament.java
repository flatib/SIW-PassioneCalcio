package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Tournament {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@NotNull
	@Column(nullable = false)
	private Integer year;

	@NotBlank
	@Column(nullable = false, length = 2000)
	private String description;

	@ManyToMany
	@JoinTable(
		name = "tournament_teams",
		joinColumns = @JoinColumn(name = "tournament_id"),
		inverseJoinColumns = @JoinColumn(name = "team_id")
	)
	@JsonIgnore
	private List<Team> teams;

	@OneToMany(mappedBy = "tournament")
	@JsonIgnore
	private List<Match> matches;
	
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Team> getTeams() {
		return teams;
	}
	
	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}
	
	public List<Match> getMatches() {
		return matches;
	}
	
	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Tournament other = (Tournament) obj;
		return Objects.equals(name, other.name) && Objects.equals(year, other.year);
	}
}
