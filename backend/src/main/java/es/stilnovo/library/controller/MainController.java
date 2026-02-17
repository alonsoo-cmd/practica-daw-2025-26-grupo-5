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

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String category,
                        HttpServletRequest request) {

        List<Product> products;

        if (category != null && !category.isEmpty()) {
            products = productService.findByQueryCategory(category);
            model.addAttribute("query", category);
        } else {
            products = productService.findByQuery(query);
            model.addAttribute("query", (query != null) ? query : "");
        }

        Principal principal = request.getUserPrincipal();

        boolean isAdmin = false;

        if (principal != null) {

            User user = userRepository.findByName(principal.getName()).orElseThrow();

            
            List<Product> userFavs = user.getFavoriteProducts();
            for (Product p : products) {
                p.setFavorite(userFavs.contains(p));
            }

            // -------- ADMIN CHECK --------
            isAdmin = user.getRoles().contains("ROLE_ADMIN");

            model.addAttribute("userId", user.getUserId());
            model.addAttribute("username", user.getName());
            model.addAttribute("logged", true);
        }

        model.addAttribute("isAdmin", isAdmin);

        if (products.size() == 1 && (query != null || category != null)) {
            return "redirect:/info-product-page/" + products.get(0).getId();
        }

        boolean isSearching =
                (query != null && !query.isEmpty()) ||
                (category != null && !category.isEmpty());

        model.addAttribute("searching", isSearching);
        model.addAttribute("products", products);

        return "index";
    }
}
