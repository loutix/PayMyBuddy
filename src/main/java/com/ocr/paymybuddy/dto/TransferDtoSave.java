package com.ocr.paymybuddy.dto;

import com.ocr.paymybuddy.model.BankAccount;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDtoSave {
    @NotEmpty
    private BankAccount bankOrigin;

    @NotEmpty
    private BankAccount bankTarget;

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotEmpty
    private BigDecimal fees;

    @NotEmpty
    private String description;

    @NotNull
    private LocalDateTime date = LocalDateTime.now();


}
