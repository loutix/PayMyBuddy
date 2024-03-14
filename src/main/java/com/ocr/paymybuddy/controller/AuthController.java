package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.dto.RegisterDto;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.UserRepository;
import com.ocr.paymybuddy.service.BankServiceImpl;
import com.ocr.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UserService userService;
    private final BankServiceImpl bankServiceImpl;

    private final UserRepository  userRepository;

    public AuthController(UserService userService, BankServiceImpl bankServiceImpl, UserRepository userRepository) {
        this.userService = userService;
        this.bankServiceImpl = bankServiceImpl;
        this.userRepository = userRepository;
    }


    @GetMapping("/login")
    public String login() {
        return "registration/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("registerDto", registerDto);
        return "registration/register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("registerDto") RegisterDto registerDto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "registration/register";
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            model.addAttribute("emailNotUnique", registerDto.getEmail());
            return "registration/register";
        }


        try {
            UserCustom newUser = userService.saveUser(registerDto);
            bankServiceImpl.saveBankAccount(newUser);
            return "redirect:/login?success";
        } catch (ValidationException e) {
//            result.rejectValue("registerDto", "error.email.duplicate", "Email already exists!");
            return "redirect:/register?error";
        }

    }
}
