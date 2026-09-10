package it.uniroma3.siw.exception;

public class DuplicatePlayerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicatePlayerException(String name, String surname) {
        super("Il giocatore " + name + " " + surname + " esiste già.");
    }
}