package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherFilterRequest;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.specification.VoucherSpecification;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
            if (hotCompare != 0) {
                return hotCompare;
            }
            return v1.getArrivalDate().compareTo(v2.getArrivalDate());
        });

        List<VoucherDTO> dtos = vouchers.stream()
                .map(v -> {
                    VoucherDTO dto = new VoucherDTO();
                    dto.setId(v.getId().toString());

                    VoucherTranslation tr = v.getTranslations().stream()
                            .filter(t -> t.getLocale().equalsIgnoreCase(locale))
                            .findFirst()
                            .orElse(null);
                    if (tr != null) {
                        dto.setTitle(tr.getTitle());
                        dto.setDescription(tr.getDescription());
                    } else {
                        dto.setTitle(v.getTitle());
                        dto.setDescription(v.getDescription());
                    }

                    dto.setPrice(v.getPrice());
                    dto.setStatus(v.getStatus().name());
                    dto.setTourType(v.getTourType() != null ? v.getTourType().name() : null);
                    dto.setTransferType(v.getTransferType() != null ? v.getTransferType().name() : null);
                    dto.setHotelType(v.getHotelType() != null ? v.getHotelType().name() : null);
                    dto.setArrivalDate(v.getArrivalDate());
                    dto.setEvictionDate(v.getEvictionDate());
                    dto.setUserId(v.getUser() != null ? v.getUser().getId() : null);
                    dto.setHot(v.isHot());

                    boolean avail = v.isAvailableForPurchase();
                    dto.setAvailable(avail);
                    dto.setAvailableForPurchase(avail);

                    return dto;
                })
                .collect(Collectors.toList());

        logger.debug("Список ваучерів сформовано, розмір: {}", dtos.size());
        return dtos;
    }

    @Override
    public List<VoucherDTO> findAllByFilter(VoucherFilterRequest filter, String locale) {
        Specification<Voucher> spec = VoucherSpecification.byFilter(filter);

        Sort sort = Sort.by("isHot").descending()
                .and(Sort.by("arrivalDate").ascending());

        return voucherRepository.findAll(spec, sort)
                .stream()
                .map(v -> {
                    VoucherDTO dto = voucherMapper.toVoucherDTO(v, locale);
                    boolean avail = v.isAvailableForPurchase();
                    dto.setAvailable(avail);
                    dto.setAvailableForPurchase(avail);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

