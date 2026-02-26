package es.stilnovo.library.controller;

import java.security.Principal;
import java.util.ArrayList;
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

/**
 * MainController: Displays the homepage and handles product browsing
 * 
 * This controller manages:
 * - Homepage display with product listings
 * - Product search by query text
 * - Product filtering by category
 * - Personalized recommendations (for logged-in users)
 * - Pagination/infinite scroll for product loading
 * - Auto-redirect when search returns single product
 * 
 * Uses: MainService, ProductService
 */
@Controller
public class MainController {

    @Autowired
    private MainService mainService;

    @Autowired
    private ProductService productService;

    /** Display homepage with product listings and recommendations */
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String category,
                        Principal principal) {

        // 1. Get typed data from Service
        User user = mainService.getUserContext(principal != null ? principal.getName() : null);

        List<Product> products;
        List<Product> recommendedProducts = null; 
        
        // Check if the user is performing a search
        boolean isSearching = (query != null && !query.isEmpty()) || (category != null && !category.isEmpty());

        if (isSearching) {
            products = new ArrayList<>(mainService.searchProducts(query, category));
        } else {
            // if not searching, show all products and recommendations
            // 1. all products (or filtered by category if category is selected)
            products = new ArrayList<>(mainService.searchProducts(query, category)); 
            
            // 2. your recommendations based on user preferences and history
            recommendedProducts = productService.getRecommendations(user);

            // 3. Filter: removing recommended products from the main list to avoid duplicates
            if (recommendedProducts != null && !recommendedProducts.isEmpty()) {
                // extracting IDs of recommended products for efficient lookup
                List<Long> recommendedIds = recommendedProducts.stream().map(Product::getId).toList();
                // removing products that are in the recommended list
                products.removeIf(p -> recommendedIds.contains(p.getId()));
            }
        }


        // 2. Populate Model
        boolean logged = (user != null);
        boolean isAdmin = mainService.isUserAdmin(user);
        
        int recSize = (recommendedProducts != null) ? recommendedProducts.size() : 0;
        int maxItems = 10;
        
        // Calculate how many regular products we can show in the first view
        int regularLimit = Math.max(0, maxItems - recSize);
        
        // It is the last page if the sum of recommended and regular products is <= 10
        boolean isLast = (recSize + products.size()) <= maxItems;
        
        int nextOffset = products.size(); 

        if (products.size() > regularLimit) {
            products = products.subList(0, regularLimit);
            nextOffset = regularLimit; // Save the index where we left off
        }
                
        model.addAttribute("products", products);
        model.addAttribute("recommendedProducts", recommendedProducts);
        model.addAttribute("user", user);
        model.addAttribute("logged", logged);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("query", (query != null) ? query : (category != null ? category : ""));
        model.addAttribute("searching", isSearching);
        model.addAttribute("isLast", isLast);
        model.addAttribute("nextOffset", nextOffset); // Added for the JS

        // 3. Navigation Logic
        if (products.size() == 1 && isSearching) {
            return "redirect:/info-product-page/" + products.get(0).getId();
        }

        return "index";
    }
}