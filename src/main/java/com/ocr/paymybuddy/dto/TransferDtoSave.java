package com.ocr.paymybuddy.dto;

import com.ocr.paymybuddy.model.BankAccount;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private BigDecimal amount;

    @NotEmpty
    private BigDecimal fees;

    @NotEmpty
    private String description;

    private LocalDateTime date;

    public String getCreditor(){
        return bankTarget.getUserCustom().getFirstName() + " " + bankTarget.getUserCustom().getLastName();
    }

    public Integer getCreditorId(){
        return bankTarget.getUserCustom().getId();
    }

}
