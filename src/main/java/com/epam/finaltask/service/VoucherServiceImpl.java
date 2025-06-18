package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private static final Logger logger = LoggerFactory.getLogger(VoucherServiceImpl.class);

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        Voucher saved = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
        voucher.setStatus(com.epam.finaltask.model.VoucherStatus.PAID);
        Voucher updated = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(updated);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher existing = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
        existing.setTitle(voucherDTO.getTitle());
        existing.setDescription(voucherDTO.getDescription());
        existing.setPrice(voucherDTO.getPrice());
        Voucher updated = voucherRepository.save(existing);
        return voucherMapper.toVoucherDTO(updated);
    }

    @Override
    public void delete(String voucherId) {
        voucherRepository.deleteById(UUID.fromString(voucherId));
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher existing = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
        existing.setHot(voucherDTO.isHot());
        Voucher updated = voucherRepository.save(existing);
        return voucherMapper.toVoucherDTO(updated);
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        List<Voucher> vouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getUser().getUsername().equals(userId))
                .collect(Collectors.toList());
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        List<Voucher> vouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getTourType() == tourType)
                .collect(Collectors.toList());
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        List<Voucher> vouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getTransferType().name().equalsIgnoreCase(transferType))
                .collect(Collectors.toList());
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        List<Voucher> vouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getHotelType() == hotelType)
                .collect(Collectors.toList());
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        List<Voucher> vouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getPrice() <= price)
                .collect(Collectors.toList());
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll(String locale) {
        logger.debug("Отримання списку ваучерів для локалі: {}", locale);

        List<Voucher> vouchers = voucherRepository.findAll();

        vouchers.sort((v1, v2) -> {
            int hotCompare = Boolean.compare(v2.isHot(), v1.isHot());
            return (hotCompare != 0) ? hotCompare : v1.getArrivalDate().compareTo(v2.getArrivalDate());
        });

        List<VoucherDTO> dtos = vouchers.stream().map(voucher -> {
            VoucherDTO dto = new VoucherDTO();
            dto.setId(voucher.getId().toString());
            VoucherTranslation translation = voucher.getTranslations().stream()
                    .filter(t -> t.getLocale().equalsIgnoreCase(locale))
                    .findFirst()
                    .orElse(null);
            if (translation != null) {
                dto.setTitle(translation.getTitle());
                dto.setDescription(translation.getDescription());
            } else {
                dto.setTitle(voucher.getTitle());
                dto.setDescription(voucher.getDescription());
            }
            dto.setPrice(voucher.getPrice());
            dto.setStatus(voucher.getStatus().name());
            dto.setAvailable(voucher.isAvailableForPurchase());
            dto.setAvailableForPurchase(voucher.isAvailableForPurchase());
            dto.setHotelType(voucher.getHotelType() != null ? voucher.getHotelType().name() : null);
            dto.setTransferType(voucher.getTransferType() != null ? voucher.getTransferType().name() : null);
            dto.setTourType(voucher.getTourType() != null ? voucher.getTourType().name() : null);
            dto.setArrivalDate(voucher.getArrivalDate());
            dto.setEvictionDate(voucher.getEvictionDate());
            dto.setUserId(voucher.getUser() != null ? voucher.getUser().getId() : null);
            dto.setHot(voucher.isHot());
            return dto;
        }).collect(Collectors.toList());

        logger.debug("Список ваучерів сформовано, розмір: {}", dtos.size());
        return dtos;
    }
}