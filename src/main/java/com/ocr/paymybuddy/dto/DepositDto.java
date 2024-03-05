package com.ocr.paymybuddy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Data
public class DepositDto {

    @NotNull
    @Range(min = 0, max = 10000,  message = "Credit  must be between 0 and 10 000€")
    private Integer credit;

    private LocalDateTime date = LocalDateTime.now();

}
