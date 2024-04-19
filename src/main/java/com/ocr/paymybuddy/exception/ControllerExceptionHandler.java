package com.ocr.paymybuddy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(Exception.class)
    public String handleException(Model model, Exception exception){
        model.addAttribute("errorMsg", exception.getMessage());
        log.info("*****ERROR***");
        log.error(exception.getMessage(), exception);
        log.info("*****ERROR***");
        return  "error";
    }
}
