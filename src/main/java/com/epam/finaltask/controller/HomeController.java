package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherFilterRequest;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VoucherService voucherService;

    @GetMapping({"/", "/home"})
    public String homePage(
            @ModelAttribute VoucherFilterRequest filter,
            @RequestParam(value = "lang", defaultValue = "en") String lang,
            Model model) {

        List<VoucherDTO> vouchers = voucherService.findAllByFilter(filter, lang);

        model.addAttribute("filter", filter);
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("locale", lang);
        return "home";
    }
}
