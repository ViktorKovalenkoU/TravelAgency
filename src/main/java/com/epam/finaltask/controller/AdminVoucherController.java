package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final VoucherService voucherService;

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("voucher", new VoucherDTO());
        populateEnums(model);
        return "admin/vouchers/edit";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute VoucherDTO voucher,
                         @RequestParam String lang) {
        voucherService.create(voucher);
        return "redirect:/admin?lang=" + lang;
    }
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        model.addAttribute("voucher", voucherService.findById(id));
        populateEnums(model);
        return "admin/vouchers/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable String id,
                         @ModelAttribute("voucher") VoucherDTO voucher,
                         BindingResult bindingResult,
                         @RequestParam(name = "lang", defaultValue = "en") String lang,
                         Model model) {
        log.info("📥 [ADMIN] Received update request: ID={}, lang={}, DTO={}", id, lang, voucher);

        if (bindingResult.hasErrors()) {
            log.warn("[ADMIN] Binding errors while updating voucher ID={}: {}", id, bindingResult.getAllErrors());
            populateEnums(model);
            return "admin/vouchers/edit";
        }

        try {
            VoucherDTO updated = voucherService.update(id, voucher);
            log.info("[ADMIN] Successfully updated voucher ID={} -> {}", id, updated);
            return "redirect:/admin?lang=" + lang; // редірект на загальну адмін панель
        } catch (IllegalArgumentException e) {
            log.error("[ADMIN] IllegalArgumentException during update of voucher ID={}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("[ADMIN] Unexpected error during update of voucher ID={}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Unexpected error occurred. Please try again.");
        }

        populateEnums(model);
        return "admin/vouchers/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteVoucher(@PathVariable String id, @RequestParam(defaultValue = "en") String lang) {
        voucherService.delete(id);
        return "redirect:/admin?lang=" + lang;
    }

    private void populateEnums(Model model) {
        model.addAttribute("allStatuses",      VoucherStatus.values());
        model.addAttribute("allTourTypes",     TourType.values());
        model.addAttribute("allTransferTypes", TransferType.values());
        model.addAttribute("allHotelTypes",    HotelType.values());
    }
}