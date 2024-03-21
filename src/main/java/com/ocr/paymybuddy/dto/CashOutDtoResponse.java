package com.ocr.paymybuddy.dto;

import com.ocr.paymybuddy.model.BankAccount;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class CashOutDtoResponse {

    @NotEmpty
    private BankAccount bankAccount;

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotEmpty
    public BigDecimal fees;

    @NotEmpty
    private final String description = "Cash out to IBAN";

    @NotNull
    private final LocalDateTime date = LocalDateTime.now();
}
