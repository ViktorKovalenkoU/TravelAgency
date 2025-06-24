package com.epam.finaltask.controller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.OrderStatus;
import com.epam.finaltask.model.VoucherOrder;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherOrderRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final VoucherOrderRepository voucherOrderRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(value = "lang", required = false) String lang,
            Model model) {

        // Якщо lang у запиті відсутній, беремо його із вже встановленої локалі
        if (lang == null) {
            // #locale в Thymeleaf – це Locale, але тут ми можемо не опускатися на JavaSide
            // Бо CookieLocaleResolver уже встановив правильну локаль.
            // Просто передамо ту ж мову, що і в #locale:
            lang = LocaleContextHolder.getLocale().getLanguage();
        }

        UserDTO user = userService.getUserByUsername(principal.getUsername());
        List<VoucherOrder> orders =
                voucherOrderRepository.findByUserUsername(principal.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);

        // прокинути lang у сторінку
        model.addAttribute("lang", lang);

        return "profile";
    }


    @PostMapping("/profile/topup")
    public String topUpBalance(Principal principal,
                               @RequestParam("amount") double amount,
                               RedirectAttributes redirectAttributes) {

        if (amount < 0 || amount > 10000) {
            redirectAttributes.addFlashAttribute("error", "Сума має бути від 0 до 10 000 доларів.");
            return "redirect:/profile";
        }

        try {
            userService.topUpBalance(principal.getName(), amount);
            redirectAttributes.addFlashAttribute("success", "Баланс успішно поповнено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Сталася помилка при поповненні балансу.");
        }

        return "redirect:/profile";
    }

    @PostMapping("/orders/pay")
    public String payOrder(@RequestParam("orderId") UUID orderId,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        VoucherOrder order = voucherOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        String username = principal.getName();
        if (!order.getUser().getUsername().equals(username)) {
            redirectAttributes.addFlashAttribute("error", "Ви не маєте прав для оплати цього замовлення.");
            return "redirect:/profile";
        }
        if (order.getOrderStatus().toString().toLowerCase().equals("confirmed")) {
            redirectAttributes.addFlashAttribute("info", "Замовлення вже оплачено.");
            return "redirect:/profile";
        }

        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        BigDecimal totalPrice = BigDecimal.valueOf(order.getTotalPrice());
        if (user.getBalance() == null || user.getBalance().compareTo(totalPrice) < 0) {
            redirectAttributes.addFlashAttribute("error", "Замало коштів, поповніть баланс.");
            return "redirect:/profile";
        }

        user.setBalance(user.getBalance().subtract(totalPrice));
        order.setOrderStatus(OrderStatus.CONFIRMED);
        voucherOrderRepository.save(order);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Оплата пройшла успішно!");
        return "redirect:/profile";
    }

    @PostMapping("/orders/cancel")
    public String cancelOrder(@RequestParam("orderId") UUID orderId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        VoucherOrder order = voucherOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        String username = principal.getName();
        if (!order.getUser().getUsername().equals(username)) {
            redirectAttributes.addFlashAttribute("error", "Ви не маєте прав для скасування цього замовлення.");
            return "redirect:/profile";
        }
        if (order.getOrderStatus().toString().toLowerCase().equals("confirmed")) {
            redirectAttributes.addFlashAttribute("error", "Замовлення вже оплачено, скасувати неможливо.");
            return "redirect:/profile";
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        voucherOrderRepository.save(order);

        order.getVoucher().setStatus(VoucherStatus.REGISTERED);
        voucherRepository.save(order.getVoucher());

        redirectAttributes.addFlashAttribute("success", "Замовлення скасовано, ваучер знову доступний для придбання.");
        return "redirect:/profile";
    }
}