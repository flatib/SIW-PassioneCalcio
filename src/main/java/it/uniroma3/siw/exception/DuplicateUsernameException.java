package it.uniroma3.siw.exception;

public class DuplicateUsernameException extends RuntimeException {

    private static final long serialVersionUID = -6277321434342099443L;

	public DuplicateUsernameException(String username) {
        super("Lo username " + username + " e' gia' in uso.");
    }
}
