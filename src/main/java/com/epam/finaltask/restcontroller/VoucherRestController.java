package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.ApiResponse;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestController {

    private final VoucherService voucherService;

    public VoucherRestController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> getAllVouchers() {
        List<VoucherDTO> vouchers = voucherService.findAll();
        ApiResponse<List<VoucherDTO>> response = new ApiResponse<>(
                "OK",
                "Vouchers retrieved successfully",
                vouchers
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> getVouchersByUserId(@PathVariable("userId") String userId) {
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        ApiResponse<List<VoucherDTO>> response = new ApiResponse<>(
                "OK",
                "Vouchers retrieved successfully",
                vouchers
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherDTO>> createVoucher(@RequestBody VoucherDTO voucherDTO) {
        VoucherDTO createdVoucher = voucherService.create(voucherDTO);
        ApiResponse<VoucherDTO> response = new ApiResponse<>(
                "OK",
                "Voucher is successfully created",
                createdVoucher
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherDTO>> updateVoucher(@PathVariable("id") String id,
                                                                 @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updatedVoucher = voucherService.update(id, voucherDTO);
        ApiResponse<VoucherDTO> response = new ApiResponse<>(
                "OK",
                "Voucher is successfully updated",
                updatedVoucher
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable("id") String id) {
        voucherService.delete(id);
        ApiResponse<Void> response = new ApiResponse<>(
                "OK",
                String.format("Voucher with Id %s has been deleted", id),
                null
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VoucherDTO>> changeVoucherStatus(@PathVariable("id") String id,
                                                                       @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updatedVoucher = voucherService.changeHotStatus(id, voucherDTO);
        ApiResponse<VoucherDTO> response = new ApiResponse<>(
                "OK",
                "Voucher status is successfully changed",
                updatedVoucher
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/order")
    public ResponseEntity<ApiResponse<VoucherDTO>> orderVoucher(@PathVariable("id") String id,
                                                                @RequestParam String userId) {
        VoucherDTO orderedVoucher = voucherService.order(id, userId);
        ApiResponse<VoucherDTO> response = new ApiResponse<>(
                "OK",
                "Voucher ordered successfully",
                orderedVoucher
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tour-type")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> getByTourType(
            @RequestParam TourType tourType) {
        List<VoucherDTO> vouchers = voucherService.findAllByTourType(tourType);
        ApiResponse<List<VoucherDTO>> response = new ApiResponse<>(
                "OK",
                "Filtered by tour type",
                vouchers
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transfer-type")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> getByTransferType(
            @RequestParam TransferType transferType) {
        List<VoucherDTO> vouchers = voucherService.findAllByTransferType(transferType.name());
        ApiResponse<List<VoucherDTO>> response = new ApiResponse<>(
                "OK",
                "Filtered by transfer type",
                vouchers
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel-type")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> getByHotelType(
            @RequestParam HotelType hotelType) {
        List<VoucherDTO> vouchers = voucherService.findAllByHotelType(hotelType);
        ApiResponse<List<VoucherDTO>> response = new ApiResponse<>(
                "OK",
                "Filtered by hotel type",
                vouchers
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/price")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> getByPrice(
            @RequestParam Double price) {
        List<VoucherDTO> vouchers = voucherService.findAllByPrice(price);
        ApiResponse<List<VoucherDTO>> response = new ApiResponse<>(
                "OK",
                "Filtered by price",
                vouchers
        );
        return ResponseEntity.ok(response);
    }
}