package com.ocr.paymybuddy.service;

import com.ocr.paymybuddy.constants.TransactionType;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import com.ocr.paymybuddy.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Save a transaction between user and their friend
     *
     * @param transferDtoSave transferDtoSave
     */
    public void saveTransaction(TransferDtoSave transferDtoSave) {
        // Debtor
        String fees = transferDtoSave.getFees().toString();
        String descriptionWithFees = transferDtoSave.getDescription() + " (inc."+ fees + "€ fees)";
        //Debtor
        saveSingleTransaction(transferDtoSave,descriptionWithFees ,transferDtoSave.getAmount().add(transferDtoSave.getFees()), TransactionType.DEBIT, transferDtoSave.getBankOrigin(), transferDtoSave.getBankTarget());
        //Creditor
        saveSingleTransaction(transferDtoSave,transferDtoSave.getDescription(),transferDtoSave.getAmount(), TransactionType.CREDIT, transferDtoSave.getBankTarget(), transferDtoSave.getBankOrigin());
    }


    private void saveSingleTransaction(TransferDtoSave transferDtoSave, String description,  BigDecimal amount, TransactionType transactionType, BankAccount owner, BankAccount friend) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionType(transactionType);
        transaction.setBankAccount(owner);
        transaction.setFriendBankAccount(friend);
        transaction.setDate(transferDtoSave.getDate());
        transactionRepository.save(transaction);

    }


    public Page<Transaction> getTransactionsByBankAccount(BankAccount bankAccount, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return transactionRepository.findByBankAccountOrderByDateDesc(bankAccount, pageRequest);

    }


}
