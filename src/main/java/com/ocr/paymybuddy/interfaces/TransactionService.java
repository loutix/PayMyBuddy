package com.ocr.paymybuddy.interfaces;

import com.ocr.paymybuddy.dto.CashOutDtoResponse;
import com.ocr.paymybuddy.dto.DepositDtoSave;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionService {

    /**
     * Saves a transfer transaction.
     *
     * @param transferDtoSave the details of the transfer to be saved
     */
    void saveTransaction(TransferDtoSave transferDtoSave);

    /**
     * Saves a deposit transaction.
     *
     * @param depositDtoSave the details of the deposit to be saved
     */
    void saveDeposit(DepositDtoSave depositDtoSave);

    /**
     * Saves a cash-out transaction.
     *
     * @param cashOutDtoResponse the details of the cash-out to be saved
     */
    void saveCashOut(CashOutDtoResponse cashOutDtoResponse);

    /**
     * Retrieves a paginated list of transactions for a given bank account.
     *
     * @param bankAccount the bank account for which transactions are to be retrieved
     * @param page        the page number to retrieve
     * @return a page of transactions for the specified bank account
     */
    Page<Transaction> getTransactionsByBankAccount(BankAccount bankAccount, int page);
}
