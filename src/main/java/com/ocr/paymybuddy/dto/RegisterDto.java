package com.ocr.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {

    @NotEmpty
    @Size(min = 5, max = 15 , message = "First name must be between 5 and 15 characters")
    private String firstName;

    @NotEmpty
    @Size(min = 5, max = 15 , message = "Lastname name must be between 5 and 15 characters")
    private String lastName;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 5, max = 15 , message = "Password name must be between 5 and 15 characters")
    private String password;
}
