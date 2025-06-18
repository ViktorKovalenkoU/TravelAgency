package com.epam.finaltask.service;

import com.epam.finaltask.exception.ResourceNotFoundException;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherOrderRepository;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherOrderService {

    private static final Logger logger = LoggerFactory.getLogger(VoucherOrderService.class);

    private final VoucherRepository voucherRepository;
    private final VoucherOrderRepository voucherOrderRepository;
    private final UserRepository userRepository;

    @Transactional
    public VoucherOrder orderVoucher(String voucherId, String userId) {
        logger.info("Початок оформлення замовлення для ваучера {} користувачем {}", voucherId, userId);

        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> {
                    logger.error("Ваучер з id {} не знайдено", voucherId);
                    return new ResourceNotFoundException("Voucher not found with id: " + voucherId);
                });

        // Перевірка доступності за датою прибуття:
        if (!voucher.isAvailableForPurchase()) {
            logger.error("Ваучер {} більше не доступний для покупки: термін реєстрації завершився", voucherId);
            throw new IllegalArgumentException("Registration period for this voucher has ended");
        }

        User user = userRepository.findUserByUsername(userId)
                .orElseThrow(() -> {
                    logger.error("Користувача {} не знайдено", userId);
                    return new ResourceNotFoundException("User not found: " + userId);
                });

        voucher.setStatus(VoucherStatus.PAID);
        voucherRepository.save(voucher);
        logger.info("Оновлено статус ваучера {} на PAID", voucherId);

        VoucherOrder order = new VoucherOrder();
        order.setUser(user);
        order.setVoucher(voucher);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setTotalPrice(voucher.getPrice());

        VoucherOrder savedOrder = voucherOrderRepository.save(order);
        logger.info("Замовлення створено: id {}", savedOrder.getId());
        return savedOrder;
    }
}