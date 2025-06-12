package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.ResourceNotFoundException;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(VoucherRepository voucherRepository,
                              UserRepository userRepository,
                              VoucherMapper voucherMapper) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
        this.voucherMapper = voucherMapper;
    }

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        voucher.setId(UUID.randomUUID());
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDTO order(String voucherId, String userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        voucher.setUser(user);
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher existingVoucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        Voucher updatedVoucher = voucherMapper.toVoucher(voucherDTO);
        updatedVoucher.setId(existingVoucher.getId());
        updatedVoucher.setUser(existingVoucher.getUser());

        return voucherMapper.toVoucherDTO(voucherRepository.save(updatedVoucher));
    }

    @Override
    public void delete(String voucherId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));
        voucherRepository.delete(voucher);
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        voucher.setHot(voucherDTO.getIsHot());
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        return voucherRepository.findAllByUserId(UUID.fromString(userId)).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        return voucherRepository.findAllByTourType(tourType).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        TransferType type = safeEnumValueOf(TransferType.class, transferType);
        return voucherRepository.findAllByTransferType(type).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        return voucherRepository.findAllByPrice(price).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        return voucherRepository.findAllByHotelType(hotelType).stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll() {
        return voucherRepository.findAll().stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }


    private <T extends Enum<T>> T safeEnumValueOf(Class<T> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value for " + enumClass.getSimpleName() + ": " + value);
        }
    }
}
