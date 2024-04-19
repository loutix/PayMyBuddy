package com.ocr.paymybuddy.dto;

import com.ocr.paymybuddy.model.UserCustom;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class TransferDto {

    @NotNull
    @Range(min = 1, max = 10000)
    private BigDecimal amount;

    @NotNull
    @Size(min = 3, max = 50)
    private String description;

    @NotNull
    private UserCustom userCustom;

}
