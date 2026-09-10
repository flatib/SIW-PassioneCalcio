package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Referee {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@NotBlank
	@Column(nullable = false)
	private String surname;

	@NotBlank
	@Column(nullable = false, unique = true)
	private String refereeCode;

	@OneToMany(mappedBy = "referee")
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
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getRefereeCode() {
		return refereeCode;
	}
	
	public void setRefereeCode(String refereeCode) {
		this.refereeCode = refereeCode;
	}
	
	public List<Match> getMatches() {
		return matches;
	}
	
	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	@Override
	public int hashCode() {
		return Objects.hash(refereeCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Referee other = (Referee) obj;
		return Objects.equals(refereeCode, other.refereeCode);
	}
}