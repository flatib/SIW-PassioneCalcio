package it.uniroma3.siw.exception;

public class DuplicateTeamException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateTeamException(String message) {
		super(message);
	}
}