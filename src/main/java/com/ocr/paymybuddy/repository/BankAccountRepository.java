package com.ocr.paymybuddy.repository;

import com.ocr.paymybuddy.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    Optional<BankAccount> findByUserCustomEmail(String email);


}
