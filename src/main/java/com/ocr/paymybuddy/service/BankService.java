package com.ocr.paymybuddy.service;


import com.ocr.paymybuddy.constants.Fare;
import com.ocr.paymybuddy.dto.DepositDto;
import com.ocr.paymybuddy.dto.TransferDto;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.BankAccountRepository;
import com.ocr.paymybuddy.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service

public class BankService {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
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
     * Get the balance bank account
     *
     * @param userDetails userDetails
     * @return integer
     */
    public BigDecimal getBankAmount(UserDetails userDetails) {
        UserCustom userCustom = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userCustom.getBankAccount().getBalanceRounded();
    }

    /**
     * User credits their own bank account
     *
     * @param userDetails userDetails
     * @param depositDto  depositDto
     * @return BankAccount
     */
    public BankAccount creditDeposit(UserDetails userDetails, DepositDto depositDto) {
        BankAccount bankAccount = bankAccountRepository.findByUserCustomEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));
        bankAccount.setBalance(bankAccount.getBalance().add(depositDto.getCredit()));
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
    public Boolean controlBalance(Double amount, BigDecimal balance) {

        //Convert Dto to BigDecimal
        BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);

        //Calculate fees
        BigDecimal fees = calculateTransactionFees(amountBigDecimal);

        //Total
        BigDecimal amountWithFees = amountBigDecimal.add(fees);

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
     * @param userDetails userDetails
     * @param transferDto transferDto
     * @return TransferDtoSave
     */
    public TransferDtoSave transferToFriend(UserDetails userDetails, TransferDto transferDto) {
        BankAccount bankAccountDebit = getBankAccount(userDetails);
        BankAccount bankAccountCredit = bankAccountRepository.findByUserCustomEmail(transferDto.getUserCustom().getEmail()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));

        BigDecimal amount = transferDto.getAmountRounded();
        BigDecimal fees = calculateTransactionFees(amount);

        // debit the debtor
        debitAccount(bankAccountDebit, amount);
        // credit the creditor
        creditAccount(bankAccountCredit, amount);

        return new TransferDtoSave(bankAccountDebit, bankAccountCredit,amount, fees, transferDto.getDescription(), transferDto.getDate());
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

    public BankAccount getBankAccount(UserDetails userDetails) {
        return bankAccountRepository.findByUserCustomEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));
    }


}
