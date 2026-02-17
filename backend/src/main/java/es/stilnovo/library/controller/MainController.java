package es.stilnovo.library.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.UserRepository;
import es.stilnovo.library.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Main landing page handler.
     * Manages product search, category filtering, and UI states.
     */
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String category,
                        HttpServletRequest request) {

        // 1. Fetch products based on search or category
        List<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productService.findByQueryCategory(category);
            model.addAttribute("query", category);
        } else {
            products = productService.findByQuery(query);
            model.addAttribute("query", (query != null) ? query : "");
        }

        // 2. Security Check & User Data
        Principal principal = request.getUserPrincipal();
        boolean isAdmin = false;

        if (principal != null) {
            // FIX: Using .orElse(null) to prevent "No value present" 500 error 
            // if the user exists in cookies but was deleted from the Database.
            User user = userRepository.findByName(principal.getName()).orElse(null);

            if (user != null) {
                isAdmin = user.getRoles().contains("ROLE_ADMIN");
                
                // Add specific attributes and the full object for Template harmony
                model.addAttribute("user", user); 
                model.addAttribute("userId", user.getUserId());
                model.addAttribute("username", user.getName());
                model.addAttribute("logged", true);
            } else {
                // Principal exists but user doesn't (Ghost session)
                model.addAttribute("logged", false);
            }
        } else {
            model.addAttribute("logged", false);
        }

        model.addAttribute("isAdmin", isAdmin);

        // 3. UI Logic: Auto-redirect if only one match is found
        if (products.size() == 1 && (query != null || category != null)) {
            return "redirect:/info-product-page/" + products.get(0).getId();
        }

        // 4. Final View State
        boolean isSearching = (query != null && !query.isEmpty()) || 
                            (category != null && !category.isEmpty());

        model.addAttribute("searching", isSearching);
        model.addAttribute("products", products);

        return "index";
    }
}