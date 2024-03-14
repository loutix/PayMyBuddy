package com.ocr.paymybuddy.service;


import com.ocr.paymybuddy.constants.Fare;
import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.interfaces.BankService;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.BankAccountRepository;
import com.ocr.paymybuddy.utilities.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BankServiceImpl implements BankService {
    private final BankAccountRepository bankAccountRepository;
    private final AuthUtils authUtils;

    public BankServiceImpl(BankAccountRepository bankAccountRepository, AuthUtils authUtils) {
        this.bankAccountRepository = bankAccountRepository;
        this.authUtils = authUtils;
    }

    /**
     * Allocation of a new bank account for each new user
     *
     * @param userCustom userCustom
     */
    @Override
    public void saveBankAccount(UserCustom userCustom) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setUserCustom(userCustom);
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccountRepository.save(bankAccount);
    }


    /**
     * User credits their own bank account
     *
     * @param depositResponseDto depositResponseDto
     * @return BankAccount
     */
    @Override
    public BankAccount creditDeposit(@Valid DepositRequestDto depositResponseDto) {
        BankAccount bankAccount = this.getBankAccount();
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
    @Override
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
     * @param transferDto transferDto
     * @return TransferDtoSave
     */
    @Override
    public TransferDtoSave transferToFriend(TransferDto transferDto) {
        BankAccount bankAccountDebit = getBankAccount();
        BankAccount bankAccountCredit = bankAccountRepository.findByUserCustomEmail(transferDto.getUserCustom().getEmail()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));

        BigDecimal amount = transferDto.getAmount();
        BigDecimal fees = calculateTransactionFees(amount);

        // debit the debtor
        debitAccount(bankAccountDebit, amount);
        // credit the creditor
        creditAccount(bankAccountCredit, amount);

        return new TransferDtoSave(bankAccountDebit, bankAccountCredit, amount, fees, transferDto.getDescription(), transferDto.getDate());
    }

    @Override
    public CashOutDtoResponse transferToIban(@Valid CashOutTransferRequestDto cashOutTransferRequestDto) {

        BankAccount bankAccountDebit = getBankAccount();
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
    @Override
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
    @Override
    public void creditAccount(BankAccount bankAccount, BigDecimal amount) {
        bankAccount.setBalance(bankAccount.getBalance().add(amount));
        bankAccountRepository.save(bankAccount);
    }

    /**
     * Get the user bank account
     *
     * @return BankAccount
     */
    public BankAccount getBankAccount() {
        String userEmail = authUtils.getCurrentUserEmail();
        return bankAccountRepository.findByUserCustomEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));
    }


    /**
     * Methode to display user IBAN and Balance
     *
     * @return CashOutRequestDto
     */
    @Override
    public CashOutRequestDto getCashOut() {

        BankAccount bankAccount = this.getBankAccount();

        return new CashOutRequestDto(
                bankAccount.getBalance(),
                bankAccount.getIban()
        );
    }

    /**
     * Save user Iban
     *
     * @param ibanRequestDto ibanRequestDto
     */
    @Override
    public void saveIban(IbanRequestDto ibanRequestDto) {
        BankAccount bankAccount = this.getBankAccount();
        bankAccount.setIban(ibanRequestDto.getIban());
        bankAccountRepository.save(bankAccount);
    }

    /**
     * Delete the user Iban
     */

    @Override
    public void deleteIban() {
        BankAccount bankAccount = this.getBankAccount();
        bankAccount.setIban(null);
        bankAccountRepository.save(bankAccount);

    }
}
