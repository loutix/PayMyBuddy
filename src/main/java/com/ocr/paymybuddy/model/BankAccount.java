package com.ocr.paymybuddy.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bank_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column( nullable = false, columnDefinition = "integer default 0")
    private Integer balance = 0;

    @OneToOne( cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", nullable = false)
    private UserCustom userCustom;

    @OneToMany(
            mappedBy = "bankAccount",
            cascade = CascadeType.ALL
    )

    List<Transaction> transactionList = new ArrayList<>();

}
