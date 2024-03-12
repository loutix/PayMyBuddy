package com.ocr.paymybuddy.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class IbanRequestDto {

    @NotEmpty
    @Pattern(
            regexp = "^FR[0-9A-Z]{26,32}$",
            message = "The IBAN format is invalid"
    )
    private String iban;

}




