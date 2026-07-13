package it.uniroma3.siw.exception;

public class UnauthorizedOperationException extends RuntimeException {

    private static final long serialVersionUID = -1472388559775760659L;

	public UnauthorizedOperationException(String message) {
        super(message);
    }
}
