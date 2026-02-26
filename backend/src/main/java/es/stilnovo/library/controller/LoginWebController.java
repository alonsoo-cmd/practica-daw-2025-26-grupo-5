package es.stilnovo.library.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

/** Controller for login and authentication pages */
@Controller
public class LoginWebController {

    /** Show login form */
    @GetMapping("/login-page")
    public String login(Model model, HttpServletRequest request) {

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("token", token.getToken());
        }

        return "login-page";
    }

    /** Show login form with error message after failed login */
    @GetMapping("/login-error")
    public String loginError(Model model, HttpServletRequest request) {

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("token", token.getToken());
        }

        model.addAttribute("loginError", true);
        return "login-page";
    }

    /** Show banned user page */
    @GetMapping("/banned")
    public String bannedPage() {
        return "banned-page";
    }
}
