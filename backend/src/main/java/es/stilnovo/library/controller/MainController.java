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

import es.stilnovo.library.service.MainService;
import es.stilnovo.library.service.ProductService; 

@Controller
public class MainController {

    @Autowired
    private MainService mainService;

    @Autowired
    private ProductService productService; // Injected service for the recommendation algorithm

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String category,
                        Principal principal) {

        // 1. Get typed data from Service
        User user = mainService.getUserContext(principal != null ? principal.getName() : null);

        List<Product> products;
        
        // Check if the user is performing a search
        boolean isSearching = (query != null && !query.isEmpty()) || (category != null && !category.isEmpty());

        if (isSearching) {
            // If searching, use the main search functionality
            products = mainService.searchProducts(query, category);
        } else {
            // If not searching, show the products RECOMMENDED by the algorithm
            products = productService.getRecommendations(user);
        }

        // 2. Populate Model
        boolean logged = (user != null);
        boolean isAdmin = mainService.isUserAdmin(user);
                
        model.addAttribute("products", products);
        model.addAttribute("user", user);
        model.addAttribute("logged", logged);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("query", (query != null) ? query : (category != null ? category : ""));
        model.addAttribute("searching", isSearching);

        // 3. Navigation Logic
        if (products.size() == 1 && isSearching) {
            return "redirect:/info-product-page/" + products.get(0).getId();
        }

        return "index";
    }
}