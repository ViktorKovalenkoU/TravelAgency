package com.epam.finaltask.controller;

import com.epam.finaltask.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    @GetMapping("/auth/sign-in")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        model.addAttribute("loginRequest", new LoginRequestDTO());
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "auth/sign-in";
    }
}