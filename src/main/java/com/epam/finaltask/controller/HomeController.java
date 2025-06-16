package com.epam.finaltask.controller;

import com.epam.finaltask.dto.LoginRequestDTO;
import com.epam.finaltask.dto.SignUpRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
}
