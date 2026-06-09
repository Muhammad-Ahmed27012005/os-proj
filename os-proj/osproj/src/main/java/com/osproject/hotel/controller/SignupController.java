package com.osproject.hotel.controller;

import com.osproject.hotel.model.User;
import com.osproject.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(User user, Model model) {
        boolean success = userService.registerUser(user);
        if (success) {
            return "redirect:/login?registered";
        } else {
            model.addAttribute("error", "Username or email already exists.");
            return "signup";
        }
    }
}