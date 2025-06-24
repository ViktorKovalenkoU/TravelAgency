package com.epam.finaltask.controller;

import com.epam.finaltask.dto.SignUpRequestDTO;
import com.epam.finaltask.exception.ResourceAlreadyExistsException;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@Slf4j
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequestDTO());
        return "auth/sign-up";
    }

    @PostMapping("/sign-up")
    public String register(
            @Valid @ModelAttribute("signUpRequest") SignUpRequestDTO dto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/sign-up";
        }

        try {
            userService.register(dto);
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch");
            return "auth/sign-up";
        } catch (ResourceAlreadyExistsException ex) {
            String msg = ex.getMessage().toLowerCase();
            if (msg.contains("username")) {
                bindingResult.rejectValue("username", "username.exists");
            } else if (msg.contains("email")) {
                bindingResult.rejectValue("email", "email.exists");
            } else if (msg.contains("phone")) {
                bindingResult.rejectValue("phoneNumber", "phone.exists");
            } else {
                model.addAttribute("error", ex.getMessage());
            }
            return "auth/sign-up";
        }

        return "redirect:/auth/sign-in";
    }
}
