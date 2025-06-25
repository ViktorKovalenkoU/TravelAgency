package com.epam.finaltask.controller;

import com.epam.finaltask.dto.SignUpRequestDTO;
import com.epam.finaltask.exception.ResourceAlreadyExistsException;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /auth/sign-up → 200, signup form in model")
    void shouldShowSignUpForm() throws Exception {
        mockMvc.perform(get("/auth/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeExists("signUpRequest"))
                .andExpect(model().attribute("signUpRequest",
                        instanceOf(SignUpRequestDTO.class)));
    }

    @Test
    @DisplayName("POST /auth/sign-up → valid data → redirect to sign-in")
    void shouldRegisterAndRedirect() throws Exception {
        mockMvc.perform(post("/auth/sign-up")
                        .with(csrf())
                        .param("username", "viktor")
                        .param("firstName", "Viktor")
                        .param("lastName", "Kovalenko")
                        .param("email", "viktor@example.com")
                        .param("phoneNumber", "+380123456789")
                        .param("password", "pass123")
                        .param("confirmPassword", "pass123")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/sign-in"));

        then(userService).should()
                .register(any(SignUpRequestDTO.class));
    }

    @Test
    @DisplayName("POST /auth/sign-up → binding errors → stay on form")
    void shouldStayOnFormWhenValidationFails() throws Exception {
        mockMvc.perform(post("/auth/sign-up")
                        .with(csrf())
                        .param("username", "")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("email", "not-an-email")
                        .param("phoneNumber", "")
                        .param("password", "p")
                        .param("confirmPassword", "")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeHasFieldErrors("signUpRequest",
                        "username", "firstName", "lastName",
                        "email", "phoneNumber", "password", "confirmPassword"));
    }

    @Test
    @DisplayName("POST /auth/sign-up → password mismatch → error on confirmPassword")
    void shouldHandlePasswordMismatch() throws Exception {
        willThrow(new IllegalArgumentException("Passwords do not match"))
                .given(userService).register(any(SignUpRequestDTO.class));

        mockMvc.perform(post("/auth/sign-up")
                        .with(csrf())
                        .param("username", "john")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@example.com")
                        .param("phoneNumber", "+380123456789")
                        .param("password", "pass123")
                        .param("confirmPassword", "wrong")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequest", "confirmPassword", "password.mismatch"));
    }

    @Test
    @DisplayName("POST /auth/sign-up → username exists → error on username")
    void shouldHandleUsernameExists() throws Exception {
        willThrow(new ResourceAlreadyExistsException("Username already exists"))
                .given(userService).register(any());

        mockMvc.perform(post("/auth/sign-up")
                        .with(csrf())
                        .param("username", "viktor")
                        .param("firstName", "Viktor")
                        .param("lastName", "Kovalenko")
                        .param("email", "viktor@example.com")
                        .param("phoneNumber", "+380123456789")
                        .param("password", "pass123")
                        .param("confirmPassword", "pass123")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequest", "username", "username.exists"));
    }

    @Test
    @DisplayName("POST /auth/sign-up → email exists → error on email")
    void shouldHandleEmailExists() throws Exception {
        willThrow(new ResourceAlreadyExistsException("Email already exists"))
                .given(userService).register(any());

        mockMvc.perform(post("/auth/sign-up")
                        .with(csrf())
                        .param("username", "viktor")
                        .param("firstName", "Viktor")
                        .param("lastName", "Kovalenko")
                        .param("email", "viktor@example.com")
                        .param("phoneNumber", "+380123456789")
                        .param("password", "pass123")
                        .param("confirmPassword", "pass123")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequest", "email", "email.exists"));
    }

    @Test
    @DisplayName("POST /auth/sign-up → phone exists → error on phoneNumber")
    void shouldHandlePhoneExists() throws Exception {
        willThrow(new ResourceAlreadyExistsException("Phone already exists"))
                .given(userService).register(any());

        mockMvc.perform(post("/auth/sign-up")
                        .with(csrf())
                        .param("username", "viktor")
                        .param("firstName", "Viktor")
                        .param("lastName", "Kovalenko")
                        .param("email", "viktor@example.com")
                        .param("phoneNumber", "+380123456789")
                        .param("password", "pass123")
                        .param("confirmPassword", "pass123")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequest", "phoneNumber", "phone.exists"));
    }

    @Test
    @DisplayName("POST /auth/sign-up → other resource exists → global error")
    void shouldHandleUnknownResourceExists() throws Exception {
        String msg = "Something went wrong";
        willThrow(new ResourceAlreadyExistsException(msg))
                .given(userService).register(any());

        mockMvc.perform(post("/auth/sign-up")
                        .with(csrf())
                        .param("username", "viktor")
                        .param("firstName", "Viktor")
                        .param("lastName", "Kovalenko")
                        .param("email", "viktor@example.com")
                        .param("phoneNumber", "+380123456789")
                        .param("password", "pass123")
                        .param("confirmPassword", "pass123")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attribute("error", msg));
    }
}