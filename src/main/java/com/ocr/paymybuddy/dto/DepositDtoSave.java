package com.ocr.paymybuddy.dto;

import com.ocr.paymybuddy.model.BankAccount;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DepositDtoSave {

    @NotEmpty
    private BankAccount bankAccount;

    @NotEmpty
    private BigDecimal amount;

    private final String description = "Credit deposit";

    @NotNull
    private final LocalDateTime date = LocalDateTime.now();

}
