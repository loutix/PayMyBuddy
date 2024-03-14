package com.ocr.paymybuddy.interfaces;

import com.ocr.paymybuddy.dto.CashOutDtoResponse;
import com.ocr.paymybuddy.dto.DepositDtoSave;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionService {

    void saveTransaction(TransferDtoSave transferDtoSave);

    void saveDeposit(DepositDtoSave depositDtoSave);

    void saveCashOut(CashOutDtoResponse cashOutDtoResponse);

    Page<Transaction> getTransactionsByBankAccount(BankAccount bankAccount, int page);

}
