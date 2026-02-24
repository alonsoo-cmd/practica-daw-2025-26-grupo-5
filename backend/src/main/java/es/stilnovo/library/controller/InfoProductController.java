package es.stilnovo.library.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.User;
import es.stilnovo.library.service.MainService;
import es.stilnovo.library.service.ProductService;


@Controller
public class InfoProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MainService mainService;

    @GetMapping("/info-product-page/{id}")
    public String infoProduct(Model model, @PathVariable long id, Principal principal) {
        
        // 1. Get the current authenticated user context
        User user = (principal != null) ? mainService.getUserContext(principal.getName()) : null;

        // 2. Fetch the product by ID or throw a 404 exception if it doesn't exist
        // This removes the need for extra null checks later
        Product product = productService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // 3. Business Logic: Record a 'VIEW' interaction if the user is logged in
        // We delegate the implementation details to the Service layer
        if (user != null) {
            productService.recordView(user, product);
        }

        // 4. Fetch personalized recommendations for the user
        List<Product> recommendations = productService.getRecommendations(user);
        
        // 5. UX Improvement: Remove the current product from the recommendations list
        if (recommendations != null) {
            recommendations.removeIf(p -> p.getId().equals(id));
        }

        // To render or not the you may also like secction
        boolean showSection = (recommendations != null && !recommendations.isEmpty());
        model.addAttribute("haveRecoProds", showSection);

        // 6. Populate the model for the Mustache template
        model.addAttribute("product", product);
        model.addAttribute("recommendedProducts", recommendations);
        model.addAttribute("logged", user != null);
        
        return "info-product-page";
    }
}