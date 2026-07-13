package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(nullable = false, unique = true)
	private String name;

	@NotNull
	@Column(nullable = false)
	private Integer foundationYear;

	@NotBlank
	@Column(nullable = false)
	private String city;

	@ManyToMany(mappedBy = "teams")
	@JsonIgnore
	private List<Tournament> tournaments;

	@OneToMany(mappedBy = "team")
	@JsonIgnore
	private List<Player> players;
	
	
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
	
	public Integer getFoundationYear() {
		return foundationYear;
	}
	
	public void setFoundationYear(Integer foundationYear) {
		this.foundationYear = foundationYear;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public List<Tournament> getTournaments() {
		return tournaments;
	}
	
	public void setTournaments(List<Tournament> tournaments) {
		this.tournaments = tournaments;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Team other = (Team) obj;
		return Objects.equals(name, other.name);
	}
}
