package es.stilnovo.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.User;
import es.stilnovo.library.service.ProductService;
import es.stilnovo.library.service.UserService;

/** Controller for buyer-to-seller communication/inquiry page */
@Controller
public class ContactSellerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/contact-seller-page/{id}")
    public String showContactSeller(@PathVariable long id, Model model, Principal principal,
                                    @RequestParam(required = false) String sent,
                                    @RequestParam(required = false) String error,
                                    @RequestParam(required = false) String cooldown) {
        
        if (principal == null) {
            return "redirect:/login-page";
        }

        Product product = productService.findById(id).orElseThrow();
        User seller = product.getSeller();
        User buyer = userService.findByName(principal.getName()).orElseThrow();

        if (seller.getUserId().equals(buyer.getUserId())) {
            return "redirect:/info-product-page/" + id + "?error=self_purchase";
        }

        model.addAttribute("product", product);
        model.addAttribute("seller", seller);

        if (principal != null) {
            // Use service layer instead of direct repository access
            userService.findByName(principal.getName()).ifPresent(user -> {
                model.addAttribute("buyerName", user.getName());
                model.addAttribute("buyerEmail", user.getEmail());
            });
        }

        model.addAttribute("sent", "true".equalsIgnoreCase(sent));
        model.addAttribute("error", error);
        model.addAttribute("cooldownMinutes", cooldown);

        return "contact-seller-page";
    }
}
