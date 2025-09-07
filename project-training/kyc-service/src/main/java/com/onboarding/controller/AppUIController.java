package com.onboarding.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppUIController {

    /**
     * Shows the custom login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // Renders login.html
    }

    /**
     * A smart dashboard that redirects users based on their role after successful login.
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                    return "redirect:/admin/dashboard";
                }
                if ("ROLE_CUSTOMER".equals(auth.getAuthority())) {
                    return "redirect:/customer/dashboard";
                }
            }
        }
        return "redirect:/login"; // Fallback
    }
}