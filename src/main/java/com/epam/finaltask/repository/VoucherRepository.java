package com.epam.finaltask.repository;

import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.HotelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface VoucherRepository
        extends JpaRepository<Voucher, UUID>,
        JpaSpecificationExecutor<Voucher> {

    List<Voucher> findAllByUserId(UUID userId);

    List<Voucher> findAllByTourType(TourType tourType);

    List<Voucher> findAllByTransferType(TransferType transferType);

    List<Voucher> findAllByPrice(Double price);

    List<Voucher> findAllByHotelType(HotelType hotelType);
}