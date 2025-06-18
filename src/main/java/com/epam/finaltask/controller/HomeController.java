package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.VoucherService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {

    private final VoucherService voucherService;

    public HomeController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       @RequestParam(required = false) TourType tourType,
                       @RequestParam(required = false) TransferType transferType,
                       @RequestParam(required = false) Double price,
                       @RequestParam(required = false) HotelType hotelType) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String locale = currentLocale.getLanguage();
        List<VoucherDTO> vouchers;

        if (tourType != null) {
            vouchers = voucherService.findAllByTourType(tourType);
        } else if (transferType != null) {
            vouchers = voucherService.findAllByTransferType(transferType.name());
        } else if (price != null) {
            vouchers = voucherService.findAllByPrice(price);
        } else if (hotelType != null) {
            vouchers = voucherService.findAllByHotelType(hotelType);
        } else {
            vouchers = voucherService.findAll(locale);
        }

        model.addAttribute("vouchers", vouchers);
        return "home";
    }
}