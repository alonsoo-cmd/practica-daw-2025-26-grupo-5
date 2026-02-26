package es.stilnovo.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.stilnovo.library.model.User;
import es.stilnovo.library.service.AdminService;
import es.stilnovo.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AdminController: Handles all administrative panel operations
 * 
 * This controller manages:
 * - Admin dashboard display (statistics, user count, banned users)
 * - User list management and filtering
 * - User deletion from the system
 * - User banning/unbanning functionality
 * - System inventory view
 * - Transaction history view
 * 
 * All endpoints are protected with ADMIN role requirement
 * Uses: AdminService, UserService
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;
    
    /**
     * Displays the main admin dashboard with system statistics
     * Shows: total users, banned users count, recent users list, memory usage
     */
    @GetMapping({ "", "/", "/panel" })
    public String showAdminPanel(Model model) {

        int numUsers = adminService.getNumTotalUsers();
        int numBanneds = adminService.getNumBanneds();

        model.addAttribute("numUsers", numUsers);
        model.addAttribute("numBanneds", numBanneds);
        
        List<User> allUsers = userService.findAll();
        List<User> dashboardUsers = allUsers.stream()
                                            .limit(3)
                                            .toList();
        
        model.addAttribute("users", dashboardUsers);

        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        model.addAttribute("memoryUsage", usedMemory + " MB");

        return "admin-panel-page";
    }

    /**
     * Lists all users in the system with sorting/filtering
     * Includes CSRF token for delete/ban operations
     */
    @GetMapping("/users")
    public String listUsers(Model model, HttpServletRequest request) {
        // Use service layer instead of direct repository access
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        // Add CSRF token object to model so Mustache section {{#_csrf}} works
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "admin-user-managment-page";
    }

    /**
     * Displays global product inventory view
     * Shows all products in the system with status
     */
    @GetMapping("/global-inventory")
    public String showGlobalInventory() {
        return "admin-global-invent-page";
    }

    /**
     * Displays all transactions in the system
     * Shows: buyer, seller, product, price, status, timestamp
     */
    @GetMapping("/transactions")
    public String showTransactions() {
        return "admin-global-transac-page";
    }

    /**
     * Permanently deletes a user account from the system
     * Also deletes all associated products, transactions, and reviews
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        adminService.deleteUser(id);

        return "redirect:/admin/users";
    }

    /**
     * Toggles user banned status (ban/unban)
     * Banned users cannot log in or perform any actions
     */
    @PostMapping("/users/ban/{id}")
    public String toggleBanUser(@PathVariable Long id) {

        // Use service layer instead of direct repository access
        User user = userService.findById(id).orElseThrow();

        user.setBanned(!user.isBanned()); // TOGGLE

        userService.save(user);

        return "redirect:/admin/users";
    }
}
