package com.epam.finaltask.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model, Locale locale) {
        System.out.println("CURRENT LOCALE: " + locale);
        return "index";
    }

    @GetMapping("/auth/sign-in")
    public String signIn(Model model) {
        return "sign-in";
    }

    @GetMapping("/auth/sign-up")
    public String signUp(Model model) {
        model.addAttribute("signUpRequest", new com.epam.finaltask.dto.UserDTO());
        return "sign-up";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }
}