package com.ocr.paymybuddy.interfaces;

import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.UserCustom;
import jakarta.validation.Valid;

import java.math.BigDecimal;

public interface BankService {

    /**
     * Registers a bank account for a given user.
     *
     * @param userCustom the user for whom the bank account is to be registered
     */
    void saveBankAccount(UserCustom userCustom);

    /**
     * Credits a deposit to the bank account.
     *
     * @param depositResponseDto the details of the deposit to be credited
     * @return the bank account after being credited
     */
    BankAccount creditDeposit(@Valid DepositRequestDto depositResponseDto);

    /**
     * Checks if the balance is sufficient for a given transaction.
     *
     * @param amount the amount of the transaction
     * @param balance the current balance of the account
     * @return true if the balance is sufficient, false otherwise
     */
    Boolean controlBalance(BigDecimal amount, BigDecimal balance);

    /**
     * Performs a transfer to a friend.
     *
     * @param transferDto the details of the transfer to be made
     * @return the details of the completed transfer
     */
    TransferDtoSave transferToFriend(TransferDto transferDto);

    /**
     * Performs a transfer to an IBAN.
     *
     * @param cashOutTransferRequestDto the details of the transfer to the IBAN
     * @return the details of the transfer response
     */
    CashOutDtoResponse transferToIban(@Valid CashOutTransferRequestDto cashOutTransferRequestDto);

    /**
     * Debits an amount from the bank account.
     *
     * @param bankAccount the bank account to be debited
     * @param amount the amount to be debited
     */
    void debitAccount(BankAccount bankAccount, BigDecimal amount);

    /**
     * Credits an amount to the bank account.
     *
     * @param bankAccount the bank account to be credited
     * @param amount the amount to be credited
     */
    void creditAccount(BankAccount bankAccount, BigDecimal amount);

    /**
     * Retrieves the current user's bank account.
     *
     * @return the current user's bank account
     */
    BankAccount getBankAccount();

    /**
     * Retrieves the details of the cash-out request.
     *
     * @return the details of the cash-out request
     */
    CashOutRequestDto getCashOut();

    /**
     * Registers an IBAN for the current user.
     *
     * @param ibanRequestDto the details of the IBAN to be registered
     */
    void saveIban(IbanRequestDto ibanRequestDto);


    /**
     * Deletes the current user's IBAN.
     */
    void deleteIban();

}