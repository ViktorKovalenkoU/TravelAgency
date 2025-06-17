package com.epam.finaltask.service;

import com.epam.finaltask.exception.ResourceNotFoundException;
import com.epam.finaltask.model.OrderStatus;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherOrder;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherOrderRepository;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherOrderService {

    private final VoucherRepository voucherRepository;
    private final VoucherOrderRepository voucherOrderRepository;
    private final UserRepository userRepository;

    @Transactional
    public VoucherOrder orderVoucher(String voucherId, String userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + voucherId));

        if (!VoucherStatus.REGISTERED.name().equals(voucher.getStatus())) {
            throw new IllegalArgumentException("Voucher is not available for order");
        }

        if (voucher.getEvictionDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new IllegalArgumentException("Voucher has expired");
        }

        User user = userRepository.findUserByUsername(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        voucher.setStatus(VoucherStatus.valueOf(VoucherStatus.PAID.name()));
        voucherRepository.save(voucher);

        VoucherOrder order = new VoucherOrder();
        order.setUser(user);
        order.setVoucher(voucher);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setTotalPrice(voucher.getPrice());

        return voucherOrderRepository.save(order);
    }
}