package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

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
        List<Voucher> vouchers = voucherRepository.findAll()
                .stream()
                .filter(v -> v.getUser().getUsername().equals(userId))
                .collect(Collectors.toList());
        return vouchers.stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        List<Voucher> vouchers = voucherRepository.findAll()
                .stream()
                .filter(v -> v.getTourType() == tourType)
                .collect(Collectors.toList());
        return vouchers.stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        List<Voucher> vouchers = voucherRepository.findAll()
                .stream()
                .filter(v -> v.getTransferType().name().equalsIgnoreCase(transferType))
                .collect(Collectors.toList());
        return vouchers.stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        List<Voucher> vouchers = voucherRepository.findAll()
                .stream()
                .filter(v -> v.getHotelType() == hotelType)
                .collect(Collectors.toList());
        return vouchers.stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        List<Voucher> vouchers = voucherRepository.findAll()
                .stream()
                .filter(v -> v.getPrice().equals(price))
                .collect(Collectors.toList());
        return vouchers.stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }
}