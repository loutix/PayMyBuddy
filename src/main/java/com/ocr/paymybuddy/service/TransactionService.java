package com.ocr.paymybuddy.service;

import com.ocr.paymybuddy.constants.TransactionType;
import com.ocr.paymybuddy.dto.CashOutDtoResponse;
import com.ocr.paymybuddy.dto.DepositDtoSave;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import com.ocr.paymybuddy.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

        String fees = transferDtoSave.getFees().toString();
        String descriptionWithFees = transferDtoSave.getDescription() + " (inc. " + fees + "€ fees)";
        BigDecimal amountWithFees = transferDtoSave.getAmount().add(transferDtoSave.getFees());

        //Debtor
        saveSingleTransaction(
                amountWithFees,
                descriptionWithFees,
                TransactionType.DEBIT,
                transferDtoSave.getBankOrigin(),
                transferDtoSave.getBankTarget(),
                transferDtoSave.getDate()
        );
        //Creditor
        saveSingleTransaction(
                transferDtoSave.getAmount(),
                transferDtoSave.getDescription(),
                TransactionType.CREDIT,
                transferDtoSave.getBankTarget(),
                transferDtoSave.getBankOrigin(),
                transferDtoSave.getDate()
        );
    }

    /**
     * Save a deposit transaction
     *
     * @param depositDtoSave depositDtoSave
     */
    public void saveDeposit(DepositDtoSave depositDtoSave) {
        //Creditor
        saveSingleTransaction(
                depositDtoSave.getAmount(),
                depositDtoSave.getDescription(),
                TransactionType.CREDIT,
                depositDtoSave.getBankAccount(),
                depositDtoSave.getBankAccount(),
                depositDtoSave.getDate()
        );
    }


    /**
     * Save a cash-out transaction
     *
     * @param cashOutDtoResponse cashOutDtoResponse
     */
    public void saveCashOut(CashOutDtoResponse cashOutDtoResponse) {

        BigDecimal amountWithFees = cashOutDtoResponse.getAmount().add(cashOutDtoResponse.getFees());
        String descriptionWithFees = cashOutDtoResponse.getDescription() + " (inc. " + cashOutDtoResponse.getFees() + "€ fees)";
        //Debtor
        saveSingleTransaction(
                amountWithFees,
                descriptionWithFees,
                TransactionType.DEBIT,
                cashOutDtoResponse.getBankAccount(),
                cashOutDtoResponse.getBankAccount(),
                cashOutDtoResponse.getDate()
        );
    }

    /**
     * @param amount amount
     * @param description  description
     * @param transactionType transactionType
     * @param owner owner
     * @param friend friend
     * @param date date
     */
    private void saveSingleTransaction(BigDecimal amount, String description, TransactionType transactionType, BankAccount owner, BankAccount friend, LocalDateTime date) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionType(transactionType);
        transaction.setBankAccount(owner);
        transaction.setFriendBankAccount(friend);
        transaction.setDate(date);
        transactionRepository.save(transaction);
    }


    public Page<Transaction> getTransactionsByBankAccount(BankAccount bankAccount, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return transactionRepository.findByBankAccountOrderByDateDesc(bankAccount, pageRequest);

    }


}
