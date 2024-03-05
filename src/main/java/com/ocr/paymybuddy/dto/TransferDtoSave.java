package com.ocr.paymybuddy.dto;

import com.ocr.paymybuddy.model.BankAccount;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransferDtoSave {
    @NotEmpty
    private BankAccount bankOrigin;

    @NotEmpty
    private BankAccount bankTarget;

    @NotEmpty
    private Integer amount;

    @NotEmpty
    private String description;

    private LocalDateTime date;

}
