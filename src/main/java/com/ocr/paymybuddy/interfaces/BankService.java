package com.ocr.paymybuddy.interfaces;

import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.UserCustom;
import jakarta.validation.Valid;

import java.math.BigDecimal;

public interface BankService {

    void saveBankAccount(UserCustom userCustom);

    BankAccount creditDeposit(@Valid DepositRequestDto depositResponseDto);

    Boolean controlBalance(BigDecimal amount, BigDecimal balance);

    TransferDtoSave transferToFriend(TransferDto transferDto);

    CashOutDtoResponse transferToIban(@Valid CashOutTransferRequestDto cashOutTransferRequestDto);

    void debitAccount(BankAccount bankAccount, BigDecimal amount);

    void creditAccount(BankAccount bankAccount, BigDecimal amount);

    BankAccount getBankAccount();

    CashOutRequestDto getCashOut();

    void saveIban(IbanRequestDto ibanRequestDto);

    void deleteIban();

}