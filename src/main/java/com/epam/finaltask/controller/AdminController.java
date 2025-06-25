package com.epam.finaltask.controller;

import com.epam.finaltask.model.*;
import com.epam.finaltask.service.AdminService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final VoucherService voucherService;
    private final AdminService adminService;
    private final UserService userService;

    @ModelAttribute("roles")
    public Role[] allRoles() {
        return Role.values();
    }

    @GetMapping
    public String panelAdmin(
            @RequestParam(value="lang", defaultValue="en") String lang,
            Model model
    ) {
        model.addAttribute("vouchers", voucherService.findAll(lang));
        model.addAttribute("allStatuses", VoucherStatus.values());
        model.addAttribute("allTourTypes", TourType.values());
        model.addAttribute("allTransferTypes", TransferType.values());
        model.addAttribute("allHotelTypes", HotelType.values());
        model.addAttribute("users", adminService.findAllUsers());
        return "admin";
    }

    @PostMapping("/users/{id}/block")
    public String block(
            @PathVariable("id") UUID id,
            @RequestParam(value="lang", defaultValue="en") String lang,
            RedirectAttributes ra
    ) {
        adminService.blockUser(id);
        ra.addFlashAttribute("success", "User blocked");
        return "redirect:/admin?lang=" + lang + "#users";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblock(
            @PathVariable("id") UUID id,
            @RequestParam(value="lang", defaultValue="en") String lang,
            RedirectAttributes ra
    ) {
        adminService.unblockUser(id);
        ra.addFlashAttribute("success", "User unblocked");
        return "redirect:/admin?lang=" + lang + "#users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(
            @PathVariable("id") UUID userId,
            @RequestParam("role") String roleName,
            @RequestParam(value = "lang", defaultValue = "en") String lang,
            RedirectAttributes ra
    ) {
        try {
            Role newRole = Role.valueOf(roleName);
            userService.changeUserRole(userId, newRole);
            ra.addFlashAttribute("success", "Role updated");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", "Unknown role: " + roleName);
        } catch (Exception ex) {
            log.error("Error changing role for user {}: ", userId, ex);
            ra.addFlashAttribute("error", "Cannot change role: " + ex.getMessage());
        }
        return "redirect:/admin?lang=" + lang + "#users";
    }

    @PostMapping("/vouchers/{id}/hot")
    public String toggleHot(
            @PathVariable String id,
            @RequestParam boolean hot,
            @RequestParam(value = "lang", defaultValue = "en") String lang
    ) {
        voucherService.changeHotStatus(id, hot, lang);
        return "redirect:/admin?lang=" + lang;
    }

    @PostMapping("/vouchers/{id}/status")
    public String changeStatus(
            @PathVariable String id,
            @RequestParam("status") VoucherStatus status,
            @RequestParam(value = "lang", defaultValue = "en") String lang
    ) {
        voucherService.changeStatus(id, status, lang);
        return "redirect:/admin?lang=" + lang;
    }
}