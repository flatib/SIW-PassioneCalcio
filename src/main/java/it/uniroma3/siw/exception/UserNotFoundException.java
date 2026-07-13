package it.uniroma3.siw.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1145644583554575393L;

	public UserNotFoundException(Long id) {
        super("Utente con ID " + id + " non trovato.");
    }
}
