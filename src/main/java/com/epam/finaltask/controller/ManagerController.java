package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerController {

    private final VoucherService voucherService;

    @GetMapping
    public String panel(
            @RequestParam(value = "lang", defaultValue = "en") String lang,
            Model model) {

        // 1) додаємо ваучери
        model.addAttribute("vouchers", voucherService.findAll(lang));
        // 2) додаємо масив статусів для <select>
        model.addAttribute("allStatuses", VoucherStatus.values());
        return "manager";
    }

    @PostMapping("/vouchers/{id}/hot")
    public String toggleHot(
            @PathVariable String id,
            @RequestParam boolean hot,
            @RequestParam(value = "lang", defaultValue = "en") String lang) {

        voucherService.changeHotStatus(id, hot, lang);
        return "redirect:/manager?lang=" + lang;
    }

    @PostMapping("/vouchers/{id}/status")
    public String changeStatus(
            @PathVariable String id,
            @RequestParam("status") VoucherStatus status,
            @RequestParam(value = "lang", defaultValue = "en") String lang) {

        voucherService.changeStatus(id, status, lang);
        return "redirect:/manager?lang=" + lang;
    }
}
