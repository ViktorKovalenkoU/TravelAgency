package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherFilterRequest;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.VoucherStatus;

import java.util.List;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO order(String id, String userId);
    VoucherDTO update(String id, VoucherDTO voucherDTO);
    void delete(String voucherId);
    VoucherDTO changeHotStatus(String id, boolean hot, String locale);
    VoucherDTO changeStatus(String id, VoucherStatus status, String locale);
    List<VoucherDTO> findAllByUserId(String userId);
    List<VoucherDTO> findAllByTourType(TourType tourType);
    List<VoucherDTO> findAllByTransferType(String transferType);
    List<VoucherDTO> findAllByHotelType(HotelType hotelType);
    List<VoucherDTO> findAllByPrice(Double price);
    List<VoucherDTO> findAll(String locale);
    List<VoucherDTO> findAllByFilter(VoucherFilterRequest filter, String locale);
}