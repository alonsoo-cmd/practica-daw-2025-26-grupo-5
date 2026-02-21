package es.stilnovo.library.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.stilnovo.library.model.User;
import es.stilnovo.library.model.Transaction;
import es.stilnovo.library.service.ValorationService;
import es.stilnovo.library.service.UserService;

/**
 * Handles user interactions regarding reviews and feedback.
 * All private routes use the Principal to ensure secure access.
 */
@Controller
public class ValorationController {

    @Autowired
    private ValorationService valorationService;

    @Autowired
    private UserService userService;

    /**
     * Displays the central dashboard for user reviews.
     * Shows pending transactions and the history of submitted ratings.
     */
    @GetMapping("/user-valorations-page")
    public String showValorationDashboard(Model model, Principal principal) {
        
        // 1. Identity Verification via Session
        User user = userService.getFullUserProfile(principal.getName());
        model.addAttribute("user", user);

        // 2. Fetch processed data from Service
        List<Transaction> pending = valorationService.getPendingTransactions(user);
        
        // 3. Populate Model for UI badges and lists
        model.addAttribute("pendingValorations", pending);
        model.addAttribute("pendingCount", pending.size());
        model.addAttribute("myValorations", valorationService.getBuyerHistory(user));

        return "user-valorations-page"; 
    }

    /**
     * Processes the submission of a new product review.
     * Redirects back to the dashboard upon successful persistence.
     */
    @PostMapping("/submit-valoration")
    public String submitValoration(Principal principal,
                                    @RequestParam long transactionId,
                                    @RequestParam int stars,
                                    @RequestParam String comment) {

        // 1. Identify current buyer
        User buyer = userService.getFullUserProfile(principal.getName());

        // 2. Delegate secure storage and rating update to the Service
        valorationService.saveAndUpdateSellerRating(transactionId, stars, comment, buyer);

        // 3. Secure Redirect: No ID leak in URL
        return "redirect:/user-valorations-page";
    }

    /**
     * Handles the deletion of a specific review.
     * Uses @PathVariable to identify the resource, following REST conventions.
     */
    @PostMapping("/valoration/delete/{id}")
    public String deleteValoration(@PathVariable long id, Principal principal) {
        
        // 1. Identify the authenticated user
        User user = userService.getFullUserProfile(principal.getName());

        // 2. Execute deletion via Service
        valorationService.deleteValoration(id, user);

        // 3. Redirect back to the dashboard
        return "redirect:/user-valorations-page";
    }

    /**
     * Processes the update request for a specific valoration.
     * Uses @PathVariable for the ID and @RequestParam for the form data.
     */
    @PostMapping("/valoration/edit/{id}")
    public String editValoration(@PathVariable long id, 
                                @RequestParam int stars, 
                                @RequestParam String comment, 
                                Principal principal) {
        
        // 1. Identify the user through the Security Context
        User user = userService.getFullUserProfile(principal.getName());

        // 2. Delegate the update logic to the Service Layer
        valorationService.updateValoration(id, stars, comment, user);
        
        // 3. Success redirect to the dashboard
        return "redirect:/user-valorations-page";
    }
}