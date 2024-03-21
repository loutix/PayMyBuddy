package com.ocr.paymybuddy.repository;

import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByBankAccountOrderByDateDesc(BankAccount bankAccount, Pageable pageable);

}
