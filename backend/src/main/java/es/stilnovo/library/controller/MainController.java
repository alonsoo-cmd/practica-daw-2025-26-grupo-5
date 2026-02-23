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
    private ProductService productService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String category,
                        Principal principal) {

        // 1. Get typed data from Service
        User user = mainService.getUserContext(principal != null ? principal.getName() : null);

        List<Product> products;
        List<Product> recommendedProducts = null; // Nuova lista per le raccomandazioni
        
        // Check if the user is performing a search
        boolean isSearching = (query != null && !query.isEmpty()) || (category != null && !category.isEmpty());

        if (isSearching) {
            // Se cerca, usiamo solo il motore di ricerca normale
            products = mainService.searchProducts(query, category);
        } else {
            // SE NON CERCA: Carichiamo ENTRAMBE le liste
            // 1. Tutti i prodotti (il catalogo normale)
            products = mainService.searchProducts(query, category); 
            
            // 2. I prodotti raccomandati dal TUO algoritmo
            recommendedProducts = productService.getRecommendations(user);
        }

        // 2. Populate Model
        boolean logged = (user != null);
        boolean isAdmin = mainService.isUserAdmin(user);
                
        model.addAttribute("products", products);
        
        // Passiamo la nuova lista e una variabile per dire all'HTML se ci sono raccomandazioni
        model.addAttribute("recommendedProducts", recommendedProducts);
        model.addAttribute("hasRecommendations", recommendedProducts != null && !recommendedProducts.isEmpty());

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