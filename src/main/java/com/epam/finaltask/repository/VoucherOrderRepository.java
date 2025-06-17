package com.epam.finaltask.repository;

import com.epam.finaltask.model.VoucherOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, UUID> {
}