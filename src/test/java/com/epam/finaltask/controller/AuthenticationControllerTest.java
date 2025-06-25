package com.epam.finaltask.controller;

import com.epam.finaltask.dto.LoginRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /auth/sign-in → 200, is loginRequest, without loginError")
    void loginPageNoError() throws Exception {
        mockMvc.perform(get("/auth/sign-in"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-in"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("loginRequest", instanceOf(LoginRequestDTO.class)))
                .andExpect(model().attributeDoesNotExist("loginError"));
    }

    @Test
    @DisplayName("GET /auth/sign-in?error=… → 200, is loginRequest and loginError=true")
    void loginPageWithError() throws Exception {
        mockMvc.perform(get("/auth/sign-in").param("error", "bad"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-in"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("loginRequest", instanceOf(LoginRequestDTO.class)))
                .andExpect(model().attribute("loginError", true));
    }
}