package com.epam.finaltask.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/auth/sign-in")
    public String signIn(Model model) {
        return "auth/sign-in";
    }

    @GetMapping("/auth/sign-up")
    public String signUp(Model model) {
        model.addAttribute("signUpRequest", new com.epam.finaltask.dto.UserDTO());
        return "auth/sign-up";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }
}