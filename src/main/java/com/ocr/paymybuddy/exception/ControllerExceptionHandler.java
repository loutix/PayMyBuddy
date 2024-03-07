package com.ocr.paymybuddy.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(Exception.class)
    public String handleException(Model model, Exception exception){
        model.addAttribute("errorMsg", exception.getMessage());
        return  "error";
    }
}
