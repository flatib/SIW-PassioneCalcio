package it.uniroma3.siw.model;

import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;

@Entity
@Table(name = "immagini")
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JdbcTypeCode(Types.BINARY)
	@Column(columnDefinition = "BYTEA", nullable = false)
	private byte[] dati;

	private String contentType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getDati() {
		return dati;
	}

	public void setDati(byte[] dati) {
		this.dati = dati;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
