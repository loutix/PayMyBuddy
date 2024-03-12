package com.ocr.paymybuddy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DepositResponseDto {
    @NotNull
    @PositiveOrZero
    private BigDecimal balance;
}
