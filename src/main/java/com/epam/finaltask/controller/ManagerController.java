package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.paged.PagedResponse;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
@Slf4j
public class ManagerController {

    private final VoucherService voucherService;

    @GetMapping
    public String panel(
            @RequestParam(value = "lang", defaultValue = "en") String lang,
            @RequestParam(value = "page", defaultValue = "0")   int page,
            @RequestParam(value = "size", defaultValue = "10")  int size,
            Locale locale,
            Model model) {

        String language = (lang != null) ? lang : locale.getLanguage();

        // сортування по isHot (поле в ентіті) та arrivalDate
        Sort sort = Sort.by("isHot").descending()
                .and(Sort.by("arrivalDate").ascending());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VoucherDTO> pageResult = voucherService.findAll(pageable, language);
        PagedResponse<VoucherDTO> paged     = new PagedResponse<>(pageResult);
        List<VoucherDTO> vouchers           = pageResult.getContent();

        model.addAttribute("pagedVouchers", paged);
        model.addAttribute("vouchers",      vouchers);
        model.addAttribute("lang",          language);
        model.addAttribute("allStatuses",   VoucherStatus.values());

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