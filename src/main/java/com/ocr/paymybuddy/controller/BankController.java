package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.service.BankServiceImpl;
import com.ocr.paymybuddy.service.TransactionServiceImpl;
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
import java.util.List;

@Controller
public class BankController {

    private final BankServiceImpl bankServiceImpl;
    private final UserService userService;
    private final TransactionServiceImpl transactionServiceImpl;


    public BankController(BankServiceImpl bankServiceImpl, UserService userService, TransactionServiceImpl transactionServiceImpl) {
        this.bankServiceImpl = bankServiceImpl;
        this.userService = userService;
        this.transactionServiceImpl = transactionServiceImpl;

    }

    @GetMapping("/deposit")
    public String getMyBankAccount(Model model) {

        BigDecimal balance = bankServiceImpl.getBankAccount().getBalance();

        DepositResponseDto depositResponseDto = new DepositResponseDto(balance);
        model.addAttribute("bankAmount", depositResponseDto.getBalance());

        DepositRequestDto depositRequestDto = new DepositRequestDto();
        model.addAttribute("depositRequestDto", depositRequestDto);

        return "bank/deposit";
    }

    @PostMapping("/deposit/save")
    public String registration(@Valid @ModelAttribute("depositRequestDto") DepositRequestDto depositRequestDto,
                               BindingResult result,
                               Model model
    ) {

        if (result.hasErrors()) {
            return "bank/deposit";
        }

        BankAccount bankAccount = bankServiceImpl.creditDeposit(depositRequestDto);
        DepositResponseDto depositResponseDto = new DepositResponseDto(bankAccount.getBalance());
        model.addAttribute("bankAccount", depositResponseDto);

        //register credit
        DepositDtoSave depositDtoSave = new DepositDtoSave(bankAccount, depositRequestDto.getCredit(), depositRequestDto.getDate());
        transactionServiceImpl.saveDeposit(depositDtoSave);


        return "redirect:/deposit?success";
    }


    /*  Cash out routes    */
    @GetMapping("/cash-out")
    public String getCashOut(Model model) {

        CashOutRequestDto cashOutRequestDto = bankServiceImpl.getCashOut();

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
                              BindingResult result) {

        if (result.hasErrors()) {
            return "bank/cashOut";
        }

        BigDecimal balance = bankServiceImpl.getBankAccount().getBalance();

        if (!bankServiceImpl.controlBalance(cashOutTransferRequestDto.getDebit(), balance)) {
            return "redirect:/cash-out?error";
        }

        CashOutDtoResponse cashOutDtoResponse = bankServiceImpl.transferToIban(cashOutTransferRequestDto);
        transactionServiceImpl.saveCashOut(cashOutDtoResponse);


        return "redirect:/cash-out?success";

    }

    @PostMapping("/cash-out/iban/save")
    public String saveCashOutIban(@Valid @ModelAttribute("ibanRequestDto") IbanRequestDto ibanRequestDto,
                                  BindingResult result,
                                  Model model) {

        CashOutRequestDto cashOutRequestDto = bankServiceImpl.getCashOut();


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

        bankServiceImpl.saveIban(ibanRequestDto);

        return "redirect:/cash-out?successIban";
    }


    @GetMapping("/cash-out/iban/delete")
    public String DeleteIban(Model model) {
        bankServiceImpl.deleteIban();
        return this.getCashOut(model);
    }


    @GetMapping("/transfer")
    public String transfer(Model model, @RequestParam(defaultValue = "0") int page) {

        TransferDto transferDto = new TransferDto();
        model.addAttribute("transferDto", transferDto);

        List<UserCustom> friendList = userService.getAuthFriendShip();
        model.addAttribute("friendList", friendList);

        BankAccount bankAccount = bankServiceImpl.getBankAccount();
        Page<Transaction> transactionList = transactionServiceImpl.getTransactionsByBankAccount(bankAccount, page);
        model.addAttribute("transactionList", transactionList);
        model.addAttribute("currentPage", page);

        return "transfer";
    }

    @PostMapping("/transfer/save")
    public String transferSave(@Valid @ModelAttribute("transferDto") TransferDto transferDto) {

        BigDecimal balance = bankServiceImpl.getBankAccount().getBalanceRounded();

        if (!bankServiceImpl.controlBalance(transferDto.getAmount(), balance)) {
            return "redirect:/transfer?error";
        }

        TransferDtoSave transferDtoSave = bankServiceImpl.transferToFriend(transferDto);

        transactionServiceImpl.saveTransaction(transferDtoSave);

        return "redirect:/transfer";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

}
