package com.ocr.paymybuddy.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CashOutRequestDto {

    @NotEmpty
    private BigDecimal balance;

    @NotEmpty
    private final String iban;

}
