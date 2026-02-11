package es.stilnovo.library.controller;

import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.UserRepository;
import es.stilnovo.library.service.ProductService;

@Controller
public class MainController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository; // Essential to find the userId

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

            // Get the user from the database to inject the ID into the templates
            userRepository.findByName(principal.getName()).ifPresent(user -> {
                model.addAttribute("userId", user.getId());
            });
            
        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productService.findAll());
        return "index";
    }
}