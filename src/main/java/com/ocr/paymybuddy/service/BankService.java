package com.ocr.paymybuddy.service;


import com.ocr.paymybuddy.constants.Fare;
import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.BankAccountRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;

@Service

public class BankService {
    private final BankAccountRepository bankAccountRepository;

    public BankService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Allocation of a new bank account for each new user
     *
     * @param userCustom userCustom
     */
    public void saveBankAccount(UserCustom userCustom) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setUserCustom(userCustom);
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccountRepository.save(bankAccount);
    }


    /**
     * User credits their own bank account
     *
     * @param principal          principal
     * @param depositResponseDto depositResponseDto
     * @return BankAccount
     */
    public BankAccount creditDeposit(Principal principal, @Valid DepositRequestDto depositResponseDto) {
        BankAccount bankAccount = this.getBankAccount(principal);
        bankAccount.setBalance(bankAccount.getBalance().add(depositResponseDto.getCredit()));
        bankAccountRepository.save(bankAccount);
        return bankAccount;
    }


    /**
     * Control if the user has enough credit to make the transfer
     *
     * @param amount  amount
     * @param balance balance
     * @return boolean
     */
    public Boolean controlBalance(BigDecimal amount, BigDecimal balance) {

        //Calculate fees
        BigDecimal fees = calculateTransactionFees(amount);
        //Total
        BigDecimal amountWithFees = amount.add(fees);

        return balance.compareTo(amountWithFees) > 0;
    }

    /**
     * Calculate fees amount
     *
     * @param amount amount
     * @return BigDecimal
     */
    private BigDecimal calculateTransactionFees(BigDecimal amount) {
        return amount.multiply(Fare.TRANSACTION_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * User make a money transfer to a friend
     *
     * @param principal   principal
     * @param transferDto transferDto
     * @return TransferDtoSave
     */
    public TransferDtoSave transferToFriend(Principal principal, TransferDto transferDto) {
        BankAccount bankAccountDebit = getBankAccount(principal);
        BankAccount bankAccountCredit = bankAccountRepository.findByUserCustomEmail(transferDto.getUserCustom().getEmail()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));

        BigDecimal amount = transferDto.getAmount();
        BigDecimal fees = calculateTransactionFees(amount);

        // debit the debtor
        debitAccount(bankAccountDebit, amount);
        // credit the creditor
        creditAccount(bankAccountCredit, amount);

        return new TransferDtoSave(bankAccountDebit, bankAccountCredit, amount, fees, transferDto.getDescription(), transferDto.getDate());
    }


    public CashOutDtoResponse transferToIban(Principal principal, @Valid CashOutTransferRequestDto cashOutTransferRequestDto) {

        BankAccount bankAccountDebit = getBankAccount(principal);

        BigDecimal amount = cashOutTransferRequestDto.getDebit();
        BigDecimal fees = calculateTransactionFees(amount);

        // debit the debtor
        debitAccount(bankAccountDebit, amount);

        return new CashOutDtoResponse(bankAccountDebit, amount, fees, cashOutTransferRequestDto.getDate());
    }


    /**
     * Debit the debtor bank account including fees
     *
     * @param bankAccount bankAccount
     * @param amount      amount
     */
    public void debitAccount(BankAccount bankAccount, BigDecimal amount) {
        BigDecimal fees = calculateTransactionFees(amount);
        BigDecimal totalAmount = amount.add(fees);
        bankAccount.setBalance(bankAccount.getBalance().subtract(totalAmount));
        bankAccountRepository.save(bankAccount);
    }

    /**
     * Credit the creditor bank account
     *
     * @param bankAccount bankAccount
     * @param amount      amount
     */
    public void creditAccount(BankAccount bankAccount, BigDecimal amount) {
        bankAccount.setBalance(bankAccount.getBalance().add(amount));
        bankAccountRepository.save(bankAccount);
    }

    public BankAccount getBankAccount(Principal principal) {
        return bankAccountRepository.findByUserCustomEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));
    }


    public CashOutRequestDto getCashOut(Principal principal) {

        BankAccount bankAccount = this.getBankAccount(principal);

        return new CashOutRequestDto(
                bankAccount.getBalance(),
                bankAccount.getIban()
        );
    }

    /**
     * Save user Iban
     *
     * @param principal      principal
     * @param ibanRequestDto ibanRequestDto
     */
    public void saveIban(Principal principal, IbanRequestDto ibanRequestDto) {
        BankAccount bankAccount = this.getBankAccount(principal);
        bankAccount.setIban(ibanRequestDto.getIban());
        bankAccountRepository.save(bankAccount);
    }

    /**
     * Delete the user Iban
     *
     * @param principal principal
     */

    public void deleteIban(Principal principal) {
        BankAccount bankAccount = this.getBankAccount(principal);
        bankAccount.setIban(null);
        bankAccountRepository.save(bankAccount);

    }
}
