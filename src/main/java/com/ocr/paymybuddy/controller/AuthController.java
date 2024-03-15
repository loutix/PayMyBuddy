package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.dto.RegisterDto;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.UserRepository;
import com.ocr.paymybuddy.service.BankServiceImpl;
import com.ocr.paymybuddy.service.UserServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class AuthController {
    private final UserServiceImpl userServiceImpl;
    private final BankServiceImpl bankServiceImpl;

    private final UserRepository  userRepository;

    public AuthController(UserServiceImpl userServiceImpl, BankServiceImpl bankServiceImpl, UserRepository userRepository) {
        this.userServiceImpl = userServiceImpl;
        this.bankServiceImpl = bankServiceImpl;
        this.userRepository = userRepository;
    }


    @GetMapping("/login")
    public String login() {
        log.info("GET/login ");
        return "registration/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.info("GET/register ");

        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("registerDto", registerDto);
        return "registration/register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("registerDto") RegisterDto registerDto, BindingResult result, Model model) {
        log.info("POST/register/save: " + "  registerDto: " + registerDto);


        if (result.hasErrors()) {
            return "registration/register";
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            model.addAttribute("emailNotUnique", registerDto.getEmail());
            return "registration/register";
        }

        try {
            UserCustom newUser = userServiceImpl.saveUser(registerDto);
            bankServiceImpl.saveBankAccount(newUser);
            return "redirect:/login?success";
        } catch (ValidationException e) {
            return "redirect:/register?error";
        }

    }
}
