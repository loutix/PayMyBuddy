package com.ocr.paymybuddy.service;

import com.ocr.paymybuddy.constants.TransactionType;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import com.ocr.paymybuddy.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

        if (!transferDtoSave.getBankOrigin().equals(transferDtoSave.getBankTarget())) {
            // Debitor
            Transaction transactionDebtor = new Transaction();
            transactionDebtor.setAmount(transferDtoSave.getAmount());
            transactionDebtor.setDescription(transferDtoSave.getDescription());
            transactionDebtor.setTransactionType(TransactionType.DEBIT);
            transactionDebtor.setBankAccount(transferDtoSave.getBankOrigin());
            transactionDebtor.setFriendBankAccount(transferDtoSave.getBankTarget());
            transactionDebtor.setDate(transferDtoSave.getDate());
            transactionRepository.save(transactionDebtor);
        }

        //Creditor
        Transaction transactionCreditor = new Transaction();
        transactionCreditor.setAmount(transferDtoSave.getAmount());
        transactionCreditor.setDescription(transferDtoSave.getDescription());
        transactionCreditor.setTransactionType(TransactionType.CREDIT);
        transactionCreditor.setBankAccount(transferDtoSave.getBankTarget());
        transactionCreditor.setFriendBankAccount(transferDtoSave.getBankOrigin());
        transactionCreditor.setDate(transferDtoSave.getDate());
        transactionRepository.save(transactionCreditor);
    }

    public Page<Transaction> getTransactionsByBankAccount(BankAccount bankAccount, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return transactionRepository.findByBankAccountOrderByDateDesc(bankAccount, pageRequest);

    }


}
