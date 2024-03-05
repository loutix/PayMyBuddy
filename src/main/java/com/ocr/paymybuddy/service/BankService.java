package com.ocr.paymybuddy.service;


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
        bankAccount.setBalance(0);
        bankAccountRepository.save(bankAccount);

    }


    /**
     *  Get the balance bank account
     * @param userDetails userDetails
     * @return integer
     */
    public Integer getBankAmount(UserDetails userDetails) {
        UserCustom userCustom = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userCustom.getBankAccount().getBalance();
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
        bankAccount.setBalance(bankAccount.getBalance() + depositDto.getCredit());
        bankAccountRepository.save(bankAccount);
        return bankAccount;
    }

    /**
     * User make a money transfer to a friend
     *
     * @param userDetails userDetails
     * @param transferDto transferDto
     * @return TransferDtoSave
     */
    public TransferDtoSave transferToFriend(UserDetails userDetails, TransferDto transferDto) {
        BankAccount bankAccountDebit = bankAccountRepository.findByUserCustomEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));
        BankAccount bankAccountCredit = bankAccountRepository.findByUserCustomEmail(transferDto.getUserCustom().getEmail()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));

        Integer amount = transferDto.getAmount();

        // debit the user account
        bankAccountDebit.setBalance(bankAccountDebit.getBalance() - amount);
        bankAccountRepository.save(bankAccountDebit);

        // credit friend account
        bankAccountCredit.setBalance(bankAccountCredit.getBalance() + amount);
        bankAccountRepository.save(bankAccountCredit);

        return new TransferDtoSave(bankAccountDebit, bankAccountCredit, amount, transferDto.getDescription(), transferDto.getDate());
    }

    public BankAccount getBankAccount(UserDetails userDetails) {
        return bankAccountRepository.findByUserCustomEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Bank account not found"));
    }


}
