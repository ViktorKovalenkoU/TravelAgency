package com.epam.finaltask.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError() {
        // Тут можна додати логіку для аналізу помилки
        return "error"; // Перенаправлення на шаблон error.html
    }

    // (З Java 9 можна безпосередньо реалізувати метод getErrorPath, але він більше не обов'язковий.)
}