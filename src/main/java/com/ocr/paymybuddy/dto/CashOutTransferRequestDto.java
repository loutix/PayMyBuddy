package com.ocr.paymybuddy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CashOutTransferRequestDto {

    @NotNull
    @Range(min = 0, max = 10000, message = "Debit  must be between 0 and 10 000€")
    private BigDecimal debit;

    @NotNull
    private final LocalDateTime date = LocalDateTime.now();

}
