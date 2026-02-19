package es.stilnovo.library.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        String username = request.getParameter("username");

        if (username != null) {
            User user = userRepository.findByName(username).orElse(null);

            if (user != null && user.isBanned()) {
                response.sendRedirect("/banned");
                return;
            }
        }

        // Default: wrong password / user not found
        response.sendRedirect("/login-error");
    }
}

