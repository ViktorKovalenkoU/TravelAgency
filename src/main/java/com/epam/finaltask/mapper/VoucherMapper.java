package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Context;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    Voucher toVoucher(VoucherDTO voucherDTO);
    VoucherDTO toVoucherDTO(Voucher voucher);

    default VoucherDTO toVoucherDTO(Voucher voucher, @Context String locale) {
        VoucherDTO dto = toVoucherDTO(voucher);

        if (voucher.getTranslations() != null && !voucher.getTranslations().isEmpty()) {
            Optional<VoucherTranslation> optTrans = voucher.getTranslations().stream()
                    .filter(t -> t.getLocale().equalsIgnoreCase(locale))
                    .findFirst();
            if(optTrans.isPresent()){
                dto.setTitle(optTrans.get().getTitle());
                dto.setDescription(optTrans.get().getDescription());
            } else {
                Optional<VoucherTranslation> defaultTrans = voucher.getTranslations().stream()
                        .filter(t -> t.getLocale().equalsIgnoreCase("en"))
                        .findFirst();
                if(defaultTrans.isPresent()){
                    dto.setTitle(defaultTrans.get().getTitle());
                    dto.setDescription(defaultTrans.get().getDescription());
                }
            }
        }
        return dto;
    }
}