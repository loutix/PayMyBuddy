package com.ocr.paymybuddy.service;

import com.ocr.paymybuddy.dto.CashOutDtoResponse;
import com.ocr.paymybuddy.dto.DepositDtoSave;
import com.ocr.paymybuddy.dto.TransferDtoSave;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.Transaction;
import com.ocr.paymybuddy.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    TransactionServiceImpl transactionService;


    @Mock
    TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        transactionService = new TransactionServiceImpl(transactionRepository);
    }

    @Test
    void saveTransaction() {
        //GIVEN
        TransferDtoSave transferDtoSave = new TransferDtoSave();
        transferDtoSave.setBankOrigin(new BankAccount());
        transferDtoSave.setBankTarget(new BankAccount());
        transferDtoSave.setAmount(new BigDecimal(100));
        transferDtoSave.setFees(new BigDecimal(10));
        transferDtoSave.setDescription("description test");
        transferDtoSave.setDate(LocalDateTime.now());

        when(transactionRepository.save(any())).thenReturn(new Transaction());

        //WHEN
        transactionService.saveTransaction(transferDtoSave);
        //THEN
        verify(transactionRepository, times(2)).save(any());
    }

    @Test
    void saveDeposit() {
        //GIVEN
        DepositDtoSave depositDtoSave = new DepositDtoSave(new BankAccount(), new BigDecimal(100));
        when(transactionRepository.save(any())).thenReturn(new Transaction());

        //WHEN
        transactionService.saveDeposit(depositDtoSave);
        //THEN
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void saveCashOut() {

        //GIVEN
        CashOutDtoResponse cashOutDtoResponse = new CashOutDtoResponse(new BankAccount(), new BigDecimal(100), new BigDecimal(10));
        when(transactionRepository.save(any())).thenReturn(new Transaction());

        //WHEN
        transactionService.saveCashOut(cashOutDtoResponse);

        //THEN
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void getTransactionsByBankAccount() {

        //GIVEN
        BankAccount bankAccount = new BankAccount();
        int page = 0;
        Page mockedPage = mock(Page.class);
        when(transactionRepository.findByBankAccountOrderByDateDesc(eq(bankAccount), any(PageRequest.class))).thenReturn(mockedPage);

        //WHEN
        Page<Transaction> result = transactionService.getTransactionsByBankAccount(bankAccount, page);

        //THEN
        assertEquals(mockedPage, result);
        verify(transactionRepository, times(1)).findByBankAccountOrderByDateDesc(eq(bankAccount), any(PageRequest.class));
    }


    @Test
    void testGetFormattedDate() {
        // GIVEN
        Transaction transaction = new Transaction();
        LocalDateTime date = LocalDateTime.of(2024, 03, 01, 02, 03);
        transaction.setDate(date);

        // WHEN
        String formattedDate = transaction.getFormattedDate();

        // THEN
        String expectedFormattedDate = "01-03-2024 - 02:03";
        assertEquals(expectedFormattedDate, formattedDate);
    }


}