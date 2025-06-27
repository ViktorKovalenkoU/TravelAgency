package com.epam.finaltask.controller;

import com.epam.finaltask.dto.LoginRequestDTO;
import com.epam.finaltask.dto.PasswordChangeDTO;
import com.epam.finaltask.dto.PasswordResetRequestDTO;
import com.epam.finaltask.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final PasswordResetService passwordResetService;

    @Value("${app.url.resetPassword}")
    private String resetUrl;

    @GetMapping("/auth/sign-in")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO());
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "auth/sign-in";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("request", new PasswordResetRequestDTO());
        return "auth/forgot-password";
    }

    @PostMapping("/auth/forgot-password")
    public String processForgotPassword(
            @ModelAttribute("request") @Valid PasswordResetRequestDTO request,
            BindingResult br,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            return "auth/forgot-password";
        }
        try {
            // створюємо та зберігаємо токен
            String token = passwordResetService.createPasswordResetToken(request.getEmail());
            // будуємо лінк та зберігаємо у flash
            String link = resetUrl + token;
            ra.addFlashAttribute("message", "Reset link generated:");
            ra.addFlashAttribute("resetLink", link);
            return "redirect:/auth/forgot-password";

        } catch (IllegalArgumentException ex) {
            br.rejectValue("email", "error.email", ex.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/auth/reset-password")
    public String resetPasswordForm(@RequestParam(value = "token", required = false) String token,
                                    Model model,
                                    RedirectAttributes ra) {
        if (token == null || token.isBlank()) {
            return "redirect:/auth/forgot-password";
        }
        try {
            passwordResetService.validatePasswordResetToken(token);
            PasswordChangeDTO dto = new PasswordChangeDTO();
            dto.setToken(token);
            model.addAttribute("dto", dto);
            return "auth/reset-password";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/auth/sign-in";
        }
    }

    @PostMapping("/auth/reset-password")
    public String processResetPassword(
            @ModelAttribute("dto") @Valid PasswordChangeDTO dto,
            BindingResult br,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            return "auth/reset-password";
        }
        try {
            passwordResetService.changePassword(dto);
            ra.addFlashAttribute("message", "Password changed successfully");
            return "redirect:/auth/sign-in";
        } catch (IllegalArgumentException ex) {
            br.reject("error.token", ex.getMessage());
            return "auth/reset-password";
        }
    }
}