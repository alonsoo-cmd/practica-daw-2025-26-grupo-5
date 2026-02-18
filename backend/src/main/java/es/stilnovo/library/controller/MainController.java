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
import es.stilnovo.library.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;
    
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

        Principal principal = request.getUserPrincipal();
        User currentUser = null;
        boolean isAdmin = false; 

        // 1. User Identification
        if (principal != null) {
            String username = principal.getName();
            // Look for the user
            currentUser = userService.findByName(username).orElse(null);
            
            if (currentUser != null) {
                model.addAttribute("logged", true);
                model.addAttribute("username", currentUser.getName());
                model.addAttribute("userId", currentUser.getUserId());
                
                isAdmin = request.isUserInRole("ADMIN");
                model.addAttribute("admin", isAdmin);
                
                // Add full user object
                model.addAttribute("user", currentUser); 
            } else {
                model.addAttribute("logged", false);
            }
        } else {
            model.addAttribute("logged", false);
        }
        
        model.addAttribute("isAdmin", isAdmin);

        // 2. Product Search Logic
        List<Product> products;
        boolean isSearching = (query != null && !query.isEmpty()) || (category != null && !category.isEmpty());

        if (isSearching) {
            if (category != null && !category.isEmpty()) {
                products = productService.findByQueryCategory(category);
                model.addAttribute("query", category);
            } else {
                products = productService.findByQuery(query);
                model.addAttribute("query", query);
            }

            // Redirect if unique result
            if (products.size() == 1) {
                return "redirect:/info-product-page/" + products.get(0).getId();
            }

        } else {
            // Recommendation Algorithm
            products = productService.getRecommendations(currentUser);
        }

        // 3. Favorites Logic
        if (currentUser != null && products != null) {
             List<Product> userFavs = currentUser.getFavoriteProducts(); 
             for (Product p : products) {
                 boolean isFav = userFavs.stream().anyMatch(fav -> fav.getId().equals(p.getId())); 
                 p.setFavorite(isFav); 
             }
        }

        model.addAttribute("searching", isSearching);
        model.addAttribute("products", products);

        return "index";
    }
}