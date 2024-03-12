package com.ocr.paymybuddy.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CashOutRequestDto {

    @NotEmpty
    private BigDecimal balance;

    @NotEmpty
    private final String iban;

    @NotNull
    private final LocalDateTime date = LocalDateTime.now();

}
