package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.dto.DepositDto;
import com.ocr.paymybuddy.dto.TransferDto;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.service.BankService;
import com.ocr.paymybuddy.service.TransactionService;
import com.ocr.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BankController {

    private final BankService bankService;
    private final UserService userService;
    private final TransactionService transactionService;


    public BankController(BankService bankService, UserService userService, TransactionService transactionService) {
        this.bankService = bankService;
        this.userService = userService;
        this.transactionService = transactionService;

    }

    @GetMapping("/my-account-statement")
    public String getMyBankAccount(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Integer balance = bankService.getBankAmount(userDetails);
        model.addAttribute("bankAmount", balance);

        DepositDto depositDto = new DepositDto();
        model.addAttribute("depositDto", depositDto);

        return "accountStatement";
    }

    @PostMapping("/my-account-statement/save")
    public String registration(@Valid @ModelAttribute("depositDto") DepositDto depositDto, BindingResult result, Model model, @AuthenticationPrincipal UserDetails userDetails) {

//        if (result.hasErrors()) {
//            return "redirect:/my-account-statement?error";
//        }

        if (result.hasErrors()) {
            return "accountStatement";
        }

        BankAccount bankAccount = bankService.creditDeposit(userDetails, depositDto);

        //register credit
        transactionService.saveTransaction(new TransferDtoSave(bankAccount, bankAccount, depositDto.getCredit(), "Credit from bankFlow", depositDto.getDate()));


        model.addAttribute("bankAmount", bankAccount.getBalance());
        return "redirect:/my-account-statement?success";

    }

    @GetMapping("/transfer")
    public String transfer(Model model, @AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "0") int page) {

        TransferDto transferDto = new TransferDto();
        model.addAttribute("transferDto", transferDto);

        List<UserCustom> friendList = userService.getAuthFriendShip(userDetails);
        model.addAttribute("friendList", friendList);


        BankAccount bankAccount = bankService.getBankAccount(userDetails);
        Page<Transaction> transactionList = transactionService.getTransactionsByBankAccount(bankAccount, page);
        model.addAttribute("transactionList", transactionList);
        model.addAttribute("currentPage", page);

        return "transfer";
    }

    @PostMapping("/transfer/save")
    public String transferSave(@Valid @ModelAttribute("transferDto") TransferDto transferDto,
                               @AuthenticationPrincipal UserDetails userDetails) {

        Integer balance = bankService.getBankAccount(userDetails).getBalance();

        if (balance < transferDto.getAmount()) {
            return "redirect:/transfer?error";
        }

        TransferDtoSave transferDtoSave = bankService.transferToFriend(userDetails, transferDto);

        transactionService.saveTransaction(transferDtoSave);

        return "redirect:/transfer";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
