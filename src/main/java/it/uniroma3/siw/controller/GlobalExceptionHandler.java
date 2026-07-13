package it.uniroma3.siw.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import it.uniroma3.siw.exception.DuplicateUsernameException;
import it.uniroma3.siw.exception.UnauthorizedOperationException;
import it.uniroma3.siw.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(UserNotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public String handleDuplicateUsername(DuplicateUsernameException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "register";
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorized(UnauthorizedOperationException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception e, Model model) {
        model.addAttribute("errorMessage", "Si è verificato un errore interno. Riprovare più tardi.");
        return "error/500";
    }
}