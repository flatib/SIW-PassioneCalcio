package it.uniroma3.siw.exception;

public class DuplicateTournamentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateTournamentException(String message) {
		super(message);
	}

}
