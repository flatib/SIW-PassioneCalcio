package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@GetMapping("/login")
    public String showLoginForm(HttpServletRequest request, HttpSession session) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
            session.setAttribute("SPRING_SECURITY_SAVED_REQUEST_REFERER", referer);
        }
        return "login";
    }
}
