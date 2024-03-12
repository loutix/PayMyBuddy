package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.service.BankService;
import com.ocr.paymybuddy.service.TransactionService;
import com.ocr.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
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
    public String getMyBankAccount(Model model, Principal principal) {

        BigDecimal balance = bankService.getBankAccount(principal).getBalance();

        DepositResponseDto depositResponseDto = new DepositResponseDto(balance);
        model.addAttribute("bankAmount", depositResponseDto.getBalance());

        DepositRequestDto depositRequestDto = new DepositRequestDto();
        model.addAttribute("depositRequestDto", depositRequestDto);

        return "bank/accountStatement";
    }

    @PostMapping("/my-account-statement/save")
    public String registration(@Valid @ModelAttribute("depositRequestDto") DepositRequestDto depositRequestDto,
                               BindingResult result,
                               Model model,
                               Principal principal) {

        if (result.hasErrors()) {
            return "bank/accountStatement";
        }

        BankAccount bankAccount = bankService.creditDeposit(principal, depositRequestDto);
        DepositResponseDto depositResponseDto = new DepositResponseDto(bankAccount.getBalance());
        model.addAttribute("bankAccount", depositResponseDto);

        //register credit
        DepositDtoSave depositDtoSave = new DepositDtoSave(bankAccount, depositRequestDto.getCredit(), depositRequestDto.getDate());
        transactionService.saveDeposit(depositDtoSave);


        return "redirect:/my-account-statement?success";
    }


    /*  Cash out routes    */
    @GetMapping("/cash-out")
    public String getCashOut(Model model, Principal principal) {

        CashOutRequestDto cashOutRequestDto = bankService.getCashOut(principal);

        // balance + Iban
        model.addAttribute("cashOutRequestDto", cashOutRequestDto);

        // a changer
        CashOutTransferRequestDto cashOutTransferRequestDto = new CashOutTransferRequestDto();
        model.addAttribute("cashOutTransferRequestDto", cashOutTransferRequestDto);

        //form pour compléter le IBAN
        IbanRequestDto ibanRequestDto = new IbanRequestDto();
        model.addAttribute("ibanRequestDto", ibanRequestDto);

        return "bank/cashOut";

    }


    @PostMapping("/cash-out/save")
    public String saveCashOut(@Valid @ModelAttribute("cashOutTransferRequestDto") CashOutTransferRequestDto cashOutTransferRequestDto,
                              BindingResult result,
                              Principal principal) {


        if (result.hasErrors()) {
            return "bank/cashOut";
        }

        BigDecimal balance = bankService.getBankAccount(principal).getBalance();

        if (!bankService.controlBalance(cashOutTransferRequestDto.getDebit(), balance)) {
            return "redirect:/cash-out?error";
        }

        CashOutDtoResponse cashOutDtoResponse = bankService.transferToIban(principal, cashOutTransferRequestDto);
        transactionService.saveCashOut(cashOutDtoResponse);


        return "redirect:/cash-out?success";

    }

    @PostMapping("/cash-out/iban/save")
    public String saveCashOutIban(@Valid @ModelAttribute("ibanRequestDto") IbanRequestDto ibanRequestDto,
                                  BindingResult result,
                                  Model model,
                                  Principal principal) {

        CashOutRequestDto cashOutRequestDto = bankService.getCashOut(principal);


        if (result.hasErrors()) {

            if (result.hasFieldErrors("iban")) {

                model.addAttribute("cashOutRequestDto", cashOutRequestDto);

                CashOutTransferRequestDto cashOutTransferRequestDto = new CashOutTransferRequestDto();
                model.addAttribute("cashOutTransferRequestDto", cashOutTransferRequestDto);


                String ibanError = result.getFieldError("iban").getDefaultMessage();
                model.addAttribute("ibanError", ibanError);
            }
            return "bank/cashOut";
        }

        bankService.saveIban(principal, ibanRequestDto);

        return "redirect:/cash-out?successIban";
    }


    @GetMapping("/cash-out/iban/delete")
    public String DeleteIban(Principal principal, Model model) {
        bankService.deleteIban(principal);
        return this.getCashOut(model, principal);
    }


    @GetMapping("/transfer")
    public String transfer(Model model, Principal principal, @RequestParam(defaultValue = "0") int page) {

        TransferDto transferDto = new TransferDto();
        model.addAttribute("transferDto", transferDto);

        List<UserCustom> friendList = userService.getAuthFriendShip(principal);
        model.addAttribute("friendList", friendList);

        BankAccount bankAccount = bankService.getBankAccount(principal);
        Page<Transaction> transactionList = transactionService.getTransactionsByBankAccount(bankAccount, page);
        model.addAttribute("transactionList", transactionList);
        model.addAttribute("currentPage", page);

        return "transfer";
    }

    @PostMapping("/transfer/save")
    public String transferSave(@Valid @ModelAttribute("transferDto") TransferDto transferDto, Principal principal) {

        BigDecimal balance = bankService.getBankAccount(principal).getBalanceRounded();

        if (!bankService.controlBalance(transferDto.getAmount(), balance)) {
            return "redirect:/transfer?error";
        }

        TransferDtoSave transferDtoSave = bankService.transferToFriend(principal, transferDto);

        transactionService.saveTransaction(transferDtoSave);

        return "redirect:/transfer";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

}
