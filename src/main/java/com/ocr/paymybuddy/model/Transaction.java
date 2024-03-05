package com.ocr.paymybuddy.model;

import com.ocr.paymybuddy.constants.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String description;

   @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private LocalDateTime date;

    @Transient
    private String formattedDate;


    @ManyToOne(cascade = {
            CascadeType.ALL
    })
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @ManyToOne(cascade = {
            CascadeType.ALL
    })
    @JoinColumn(name = "friend_bank_id", nullable = false)
    private BankAccount friendBankAccount;


    /**
     * calculated field for the front
     * @return date formatted
     */
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm"));
    }

}
