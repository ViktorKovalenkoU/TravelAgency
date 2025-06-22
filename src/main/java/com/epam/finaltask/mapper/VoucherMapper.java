package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Context;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    VoucherDTO toVoucherDTO(Voucher voucher);

    Voucher toVoucher(VoucherDTO dto);

    default VoucherDTO toVoucherDTO(Voucher voucher, @Context String locale) {
        VoucherDTO dto = toVoucherDTO(voucher);

        if (voucher.getTranslations() != null && !voucher.getTranslations().isEmpty()) {
            Optional<VoucherTranslation> trOpt = voucher.getTranslations().stream()
                    .filter(t -> t.getLocale().equalsIgnoreCase(locale))
                    .findFirst();
            if (trOpt.isEmpty()) {
                trOpt = voucher.getTranslations().stream()
                        .filter(t -> t.getLocale().equalsIgnoreCase("en"))
                        .findFirst();
            }
            trOpt.ifPresent(tr -> {
                dto.setTitle(tr.getTitle());
                dto.setDescription(tr.getDescription());
            });
        }
        if (dto.getTitle() == null) {
            dto.setTitle(voucher.getTitle());
        }
        if (dto.getDescription() == null) {
            dto.setDescription(voucher.getDescription());
        }

        boolean avail = voucher.isAvailableForPurchase();
        dto.setAvailable(avail);
        dto.setAvailableForPurchase(avail);

        return dto;
    }
}

