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
import es.stilnovo.library.repository.UserRepository;
import es.stilnovo.library.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminService adminService;  

    @GetMapping("/panel")
    public String showAdminPanel() {
        return "admin-panel-page";
    }

    // List users: añadimos el objeto _csrf al modelo para que Mustache lo use
    @GetMapping("/users")
    public String listUsers(Model model, HttpServletRequest request) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        // Add CSRF token object to model so Mustache section {{#_csrf}} works
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "admin-user-managment-page";
    }

    @GetMapping("/global-inventory")
    public String showGlobalInventory() {
        return "admin-global-invent-page";
    }

    @GetMapping("/transactions")
    public String showTransactions() {
        return "admin-global-transac-page";
    }

    //safe delete
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        adminService.deleteUser(id);

        return "redirect:/admin/users";
    }

    // Access administration page for a specific admin user
    @GetMapping("/{id}")
    public String showAdministrationpage(Model model, @PathVariable Long id, HttpServletRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);

        // Also expose CSRF token in case the admin panel has forms
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "admin-panel-page";
    }

    // Ban / Unban user (toggle)
    @PostMapping("/users/ban/{id}")
    public String toggleBanUser(@PathVariable Long id) {

        User user = userRepository.findById(id).orElseThrow();

        user.setBanned(!user.isBanned()); // TOGGLE

        userRepository.save(user);

        return "redirect:/admin/users";
    }
}
