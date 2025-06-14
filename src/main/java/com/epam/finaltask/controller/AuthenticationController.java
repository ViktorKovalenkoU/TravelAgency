package com.epam.finaltask.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/auth/sign-in-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "sign-in";
    }
}