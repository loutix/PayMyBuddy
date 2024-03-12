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

    private LocalDateTime date;


}
