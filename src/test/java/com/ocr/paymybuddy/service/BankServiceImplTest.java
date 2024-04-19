package com.ocr.paymybuddy.service;

import com.ocr.paymybuddy.constants.Fare;
import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.BankAccountRepository;
import com.ocr.paymybuddy.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {


    BankServiceImpl bankServiceImpl;

    @Mock
    BankAccountRepository bankAccountRepository;
    @Mock
    AuthUtils authUtils;
    UserCustom userCustom1 = new UserCustom();
    UserCustom userCustom2 = new UserCustom();
    UserCustom userCustom3 = new UserCustom();

    @BeforeEach
    public void setUp() {
        bankServiceImpl = new BankServiceImpl(bankAccountRepository, authUtils);
        //user1
        userCustom1.setFirstName("charles");
        userCustom1.setLastName("Boui");
        userCustom1.setEmail("charles@gmail.com");
        userCustom1.setPassword("password");
        userCustom1.setId(1);

        //user2
        userCustom2.setFirstName("Loïc");
        userCustom2.setLastName("test");
        userCustom2.setEmail("loic@test.com");
        userCustom2.setPassword("password");
        userCustom2.setId(2);


        userCustom3.setFirstName("Chloé");
        userCustom3.setLastName("testeuse");
        userCustom3.setEmail("chloe@test.com");
        userCustom3.setPassword("password");
        userCustom3.setId(3);

    }


    @Test
    @WithMockUser
    @DisplayName("Save a new bank account")
    void saveBankAccount() {
        //GIVEN
        UserCustom userCustom = new UserCustom();
        //WHEN
        bankServiceImpl.saveBankAccount(userCustom);
        //THEN
        verify(bankAccountRepository, times(1)).save(any());

//        L'interface ArgumentCaptor fait partie du framework Mockito
//        et est utilisée pour capturer les arguments passés à une méthode lors de son appel dans un test.
//        Cela permet d'inspecter ces arguments pour effectuer des assertions sur eux.

        ArgumentCaptor<BankAccount> captor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(captor.capture());
        BankAccount savedBankAccount = captor.getValue();

        assertNotNull(savedBankAccount);
        assertEquals(userCustom, savedBankAccount.getUserCustom());
        assertEquals(BigDecimal.ZERO, savedBankAccount.getBalance());

    }

    @Test
    @WithMockUser
    @DisplayName("Increase balance with a new deposit")
    void creditDeposit() {
        //GIVEN
        DepositRequestDto depositRequestDto = new DepositRequestDto();
        depositRequestDto.setCredit(new BigDecimal(100));

        String currentEmail = "loic@test.com";

        BankAccount bankAccount = new BankAccount();
        //this.getBanAccount
        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.of(bankAccount));

        //WHEN
        bankServiceImpl.creditDeposit(depositRequestDto);

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(bankAccountRepository, times(1)).findByUserCustomEmail(anyString());
        verify(bankAccountRepository, times(1)).save(bankAccount);
        assertEquals(BigDecimal.valueOf(100), bankAccount.getBalance());
    }

    @Test
    @WithMockUser
    @DisplayName("Control balance sufficient balance")
    void controlBalanceReturnTrue() {
        //GIVEN
        BigDecimal amount = new BigDecimal(100);
        BigDecimal balance = new BigDecimal(1000);
        //WHEN
        Boolean result = bankServiceImpl.controlBalance(amount, balance);
        //THEN
        assertNotNull(result);
        assertTrue(result);

    }

    @Test
    @WithMockUser
    @DisplayName("Control balance insufficient balance")
    void controlBalanceReturnFalse() {
        //GIVEN
        BigDecimal amount = new BigDecimal(1000);
        BigDecimal balance = new BigDecimal(100);
        //WHEN
        Boolean result = bankServiceImpl.controlBalance(amount, balance);
        //THEN
        assertNotNull(result);
        assertFalse(result);

    }


    @Test
    @WithMockUser
    @DisplayName("Calcul fees")
    void calculateTransactionFees() {
        //GIVEN
        BigDecimal amount = new BigDecimal("100");
        //WHEN
        BigDecimal result = bankServiceImpl.calculateTransactionFees(amount);
        //THEN
        assertNotNull(result);
        assertEquals(new BigDecimal("0.50"), result);
    }

    @Test
    @WithMockUser
    @DisplayName("Transfer to friend")
    void transferToFriend() {
        //GIVEN
        String currentEmail = "loic@test.com";

        BankAccount bankAccountToDebit = new BankAccount();
        BankAccount bankAccountToCredit = new BankAccount();

        UserCustom receiver = userCustom3;

        TransferDto transferDto = new TransferDto();
        transferDto.setAmount(new BigDecimal("100"));
        transferDto.setDescription("description test");
        transferDto.setUserCustom(receiver);

        //getBankAccount
        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.of(bankAccountToDebit));
        //bank account to credit
        when(bankAccountRepository.findByUserCustomEmail(userCustom3.getEmail())).thenReturn(Optional.of(bankAccountToCredit));

        //WHEN
        TransferDtoSave result = bankServiceImpl.transferToFriend(transferDto);

        System.out.println(transferDto);
        System.out.println(result);

        //THEN
        assertNotNull(result);
        assertEquals("description test", result.getDescription());
        assertEquals(new BigDecimal("100"), result.getAmount());
        assertEquals(new BigDecimal("0.50"), result.getFees());
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(bankAccountRepository, times(1)).findByUserCustomEmail(currentEmail);

    }

    @Test
    @WithMockUser
    @DisplayName("Transfer to friend not found")
    void transferToFriendNotFound() {
        //GIVEN
        String currentEmail = "loic@test.com";

        UserCustom receiver = userCustom3;

        TransferDto transferDto = new TransferDto();
        transferDto.setAmount(new BigDecimal("100"));
        transferDto.setDescription("description test");
        transferDto.setUserCustom(receiver);

        //getBankAccount
        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.empty());

        //WHEN
        assertThrows(UsernameNotFoundException.class, () -> bankServiceImpl.transferToFriend(transferDto));

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(bankAccountRepository, times(1)).findByUserCustomEmail(currentEmail);

    }


    @Test
    @WithMockUser
    @DisplayName("Transfer to IBAN")
    void testTransferToIban() {

        //GIVEN
        CashOutTransferRequestDto cashOutTransferRequestDto = new CashOutTransferRequestDto();
        BigDecimal amount = new BigDecimal(100);
        cashOutTransferRequestDto.setDebit(amount);

        String currentEmail = "loic@test.com";
        BankAccount bankAccountDebit = new BankAccount();
        bankAccountDebit.setUserCustom(userCustom2);

        BigDecimal fees = amount.multiply(Fare.TRANSACTION_FEE_RATE).setScale(2, RoundingMode.HALF_UP);

        //getBankAccount
        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.of(bankAccountDebit));

        //WHEN
        CashOutDtoResponse result = bankServiceImpl.transferToIban(cashOutTransferRequestDto);

        //THEN
        assertEquals(amount, result.getAmount());
        assertEquals(fees, result.getFees());
        assertEquals(userCustom2, result.getBankAccount().getUserCustom());
    }

    @Test
    @WithMockUser
    @DisplayName("Debit bank account")
    void debitAccount() {

        //GIVEN
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(new BigDecimal(1000));

        BigDecimal amount = new BigDecimal("100");
        BigDecimal balanceControl = bankAccount.getBalance().subtract(amount.add(new BigDecimal("0.50")));

        //WHEN
        bankServiceImpl.debitAccount(bankAccount, amount);

        //THEN
        verify(bankAccountRepository, times(1)).save(bankAccount);
        assertEquals(balanceControl, bankAccount.getBalance());
    }

    @Test
    @WithMockUser
    @DisplayName("Credit bank account")
    void testCreditAccount() {
        //GIVEN
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(new BigDecimal("1000"));

        BigDecimal amount = new BigDecimal("100");

        BigDecimal controlBalance = new BigDecimal("1100");

        //WHEN
        bankServiceImpl.creditAccount(bankAccount, amount);

        //THEN
        assertEquals(controlBalance, bankAccount.getBalance());

    }

    @Test
    @WithMockUser
    @DisplayName("Get bankAccount")
    void getBankAccount() {

        //GIVEN
        String currentEmail = "loic@test.com";

        BankAccount bankAccount = new BankAccount();
        bankAccount.setUserCustom(userCustom2);

        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.of(bankAccount));

        //WHEN
        BankAccount result = bankServiceImpl.getBankAccount();
        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(bankAccountRepository, times(1)).findByUserCustomEmail(anyString());
        assertEquals(currentEmail, result.getUserCustom().getEmail());

    }

    @Test
    @WithMockUser
    @DisplayName("Get getCashOut ")
    void getCashOut() {
        //GIVEN
        String currentEmail = "loic@test.com";

        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("FRTEST123");
        bankAccount.setBalance(new BigDecimal("1000"));

        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.of(bankAccount));

        //WHEN
        CashOutRequestDto result = bankServiceImpl.getCashOut();

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(bankAccountRepository, times(1)).findByUserCustomEmail(anyString());
        assertEquals(bankAccount.getBalance(), result.getBalance());
        assertEquals(bankAccount.getIban(), result.getIban());

    }

    @Test
    @WithMockUser
    @DisplayName("Save a new Iban")
    void saveIban() {
        //GIVEN
        IbanRequestDto ibanRequestDto = new IbanRequestDto();
        ibanRequestDto.setIban("FR132465789");

        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(new BigDecimal("1000"));

        String currentEmail = "loic@test.com";
        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.of(bankAccount));

        //WHEN
        bankServiceImpl.saveIban(ibanRequestDto);

        //THEN
        verify(bankAccountRepository, times(1)).save(bankAccount);
        assertEquals("FR132465789", bankAccount.getIban());
    }

    @Test
    @WithMockUser
    @DisplayName("Delete an Iban")
    void deleteIban() {
        //GIVEN
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("FR123456789");

        String currentEmail = "loic@test.com";
        when(authUtils.getCurrentUserEmail()).thenReturn(currentEmail);
        when(bankAccountRepository.findByUserCustomEmail(currentEmail)).thenReturn(Optional.of(bankAccount));

        //WHEN
        bankServiceImpl.deleteIban();

        //THEN
        assertNull(bankAccount.getIban());

    }
}