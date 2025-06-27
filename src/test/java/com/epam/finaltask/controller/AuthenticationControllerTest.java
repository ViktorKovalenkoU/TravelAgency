package com.epam.finaltask.controller;

import com.epam.finaltask.dto.LoginRequestDTO;
import com.epam.finaltask.dto.PasswordChangeDTO;
import com.epam.finaltask.dto.PasswordResetRequestDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.service.PasswordResetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.url.resetPassword=http://localhost:8080/auth/reset-password?token="
})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @Test
    @DisplayName("POST /auth/forgot-password → valid email redirects with demo link")
    void processForgotPassword_Success() throws Exception {
        // підготовка: сервіс повертає токен
        String token = "abc123";
        given(passwordResetService.createPasswordResetToken("foo@example.com"))
                .willReturn(token);

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "foo@example.com"))
                .andExpect(status().is3xxRedirection())
                // перевірка flash-повідомлень
                .andExpect(flash().attribute("message", "Reset link generated:"))
                .andExpect(flash().attribute("resetLink",
                        "http://localhost:8080/auth/reset-password?token=" + token))
                .andExpect(redirectedUrl("/auth/forgot-password"));
    }

    @Test
    @DisplayName("POST /auth/forgot-password → invalid email shows field error")
    void processForgotPassword_InvalidEmail() throws Exception {
        // підготовка: сервіс кидає ексепшн
        given(passwordResetService.createPasswordResetToken("bad@example.com"))
                .willThrow(new IllegalArgumentException("No user with email bad@example.com"));

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "bad@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"))
                // перевірка, що в моделі є помилка в полі email
                .andExpect(model().attributeHasFieldErrorCode(
                        "request", "email", "error.email"));
    }

    //
    // === Тестування reset-password та входу залишаються без змін ===
    //

    @Test
    @DisplayName("GET /auth/sign-in → form without error")
    void loginPageNoError() throws Exception {
        mockMvc.perform(get("/auth/sign-in"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-in"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("loginRequest",
                        instanceOf(LoginRequestDTO.class)))
                .andExpect(model().attributeDoesNotExist("loginError"));
    }

    @Test
    @DisplayName("GET /auth/sign-in?error=… → form with loginError")
    void loginPageWithError() throws Exception {
        mockMvc.perform(get("/auth/sign-in").param("error", "bad"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-in"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("loginRequest",
                        instanceOf(LoginRequestDTO.class)))
                .andExpect(model().attribute("loginError", true));
    }

    @Test
    @DisplayName("GET /auth/reset-password?token=good → shows reset form")
    void resetPasswordForm_ValidToken() throws Exception {
        User dummy = new User();
        given(passwordResetService.validatePasswordResetToken("good-token"))
                .willReturn(dummy);

        mockMvc.perform(get("/auth/reset-password")
                        .param("token", "good-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attributeExists("dto"))
                .andExpect(model().attribute("dto",
                        instanceOf(PasswordChangeDTO.class)));
    }

    @Test
    @DisplayName("GET /auth/reset-password (no token) → redirect to forgot-password")
    void resetPasswordForm_NoToken() throws Exception {
        mockMvc.perform(get("/auth/reset-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/forgot-password"));
    }

    @Test
    @DisplayName("POST /auth/reset-password → mismatch shows global error")
    void processResetPassword_PasswordsDontMatch() throws Exception {
        willThrow(new IllegalArgumentException("Passwords do not match"))
                .given(passwordResetService).changePassword(any(PasswordChangeDTO.class));

        mockMvc.perform(post("/auth/reset-password")
                        .param("token", "t")
                        .param("newPassword", "A")
                        .param("confirmPassword", "B"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attributeHasErrors("dto"))
                .andExpect(model().attributeErrorCount("dto", 1));
    }

    @Test
    @DisplayName("POST /auth/reset-password → valid → redirect to login")
    void processResetPassword_Success() throws Exception {
        willDoNothing().given(passwordResetService).changePassword(any(PasswordChangeDTO.class));

        mockMvc.perform(post("/auth/reset-password")
                        .param("token", "t")
                        .param("newPassword", "X123")
                        .param("confirmPassword", "X123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "Password changed successfully"))
                .andExpect(redirectedUrl("/auth/sign-in"));
    }
}