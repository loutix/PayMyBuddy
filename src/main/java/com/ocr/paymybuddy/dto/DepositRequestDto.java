package com.ocr.paymybuddy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DepositRequestDto {

    @NotNull
    @Range(min = 0, max = 10000, message = "Credit  must be between 0 and 10 000€")
    private BigDecimal credit;

    @NotNull
    private final LocalDateTime date = LocalDateTime.now();

}
