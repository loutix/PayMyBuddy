package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.dto.*;
import com.ocr.paymybuddy.model.BankAccount;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.security.SecurityConfig;
import com.ocr.paymybuddy.service.BankServiceImpl;
import com.ocr.paymybuddy.service.TransactionServiceImpl;
import com.ocr.paymybuddy.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BankController.class)
@ContextConfiguration(classes = {SecurityConfig.class, BankController.class})
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankServiceImpl bankServiceImpl;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private TransactionServiceImpl transactionServiceImpl;


    @Test
    @WithMockUser
    @DisplayName("Get the deposit form")
    public void testGetMyBankAccount() throws Exception {

        //Given
        BankAccount bankAccount = new BankAccount();
        BigDecimal balance = new BigDecimal(100);
        bankAccount.setBalance(balance);

        DepositResponseDto depositResponseDto = new DepositResponseDto(balance);
        DepositRequestDto depositRequestDto = new DepositRequestDto();

        //When
        when(bankServiceImpl.getBankAccount()).thenReturn(bankAccount);

        mockMvc.perform(get("/deposit"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank/deposit"))
                .andExpect(model().attributeExists("bankAmount"))
                .andExpect(model().attribute("bankAmount", depositResponseDto.getBalance()))
                .andExpect(model().attributeExists("depositRequestDto"))
                .andExpect(model().attribute("depositRequestDto", depositRequestDto));

        //Then
        verify(bankServiceImpl, times(1)).getBankAccount();
    }

    @Test
    @WithMockUser
    @DisplayName("Save a deposit")
    public void testRegistrationDeposit() throws Exception {

        DepositRequestDto depositRequestDto = new DepositRequestDto();
        depositRequestDto.setCredit(new BigDecimal(1000));


        //GIVEN
        BankAccount bankAccount = new BankAccount();
        when(bankServiceImpl.creditDeposit(depositRequestDto)).thenReturn(bankAccount);


        //WHEN
        mockMvc.perform(post("/deposit/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("credit", String.valueOf(depositRequestDto.getCredit())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/deposit?success"));


        //THEN
        verify(bankServiceImpl, times(1)).creditDeposit(depositRequestDto);
    }


    @Test
    @WithMockUser
    @DisplayName("Error in form save a deposit")
    public void testRegistrationDepositFailed() throws Exception {

        //GIVEN
        DepositRequestDto depositRequestDto = new DepositRequestDto();
        depositRequestDto.setCredit(new BigDecimal("200000"));

        //WHEN
        mockMvc.perform(post("/deposit/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("credit", String.valueOf(depositRequestDto.getCredit())))
                .andExpect(status().isOk())
                .andExpect(view().name("bank/deposit"));


        //THEN
        verify(bankServiceImpl, times(0)).creditDeposit(depositRequestDto);
    }


    @Test
    @WithMockUser
    @DisplayName("Display form cash out")
    public void testGetCashOut() throws Exception {

        //GIVEN
        CashOutRequestDto cashOutRequestDto = new CashOutRequestDto(new BigDecimal(5000), "FR12345678901234567890123456");
        IbanRequestDto ibanRequestDto = new IbanRequestDto();
        CashOutTransferRequestDto cashOutTransferRequestDto = new CashOutTransferRequestDto();

        //WHEN
        when(bankServiceImpl.getCashOut()).thenReturn(cashOutRequestDto);


        mockMvc.perform(get("/cash-out"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank/cashOut"))
                .andExpect(model().attributeExists("cashOutRequestDto"))
                .andExpect(model().attribute("cashOutRequestDto", cashOutRequestDto))
                .andExpect(model().attributeExists("ibanRequestDto"))
                .andExpect(model().attribute("ibanRequestDto", ibanRequestDto))
                .andExpect(model().attributeExists("cashOutTransferRequestDto"))
                .andExpect(model().attribute("cashOutTransferRequestDto", cashOutTransferRequestDto));

        //THEN
        verify(bankServiceImpl, times(1)).getCashOut();

    }


    @Test
    @WithMockUser
    @DisplayName("Save a cash out")
    public void saveCashOut() throws Exception {

        //GIVEN
        CashOutTransferRequestDto cashOutTransferRequestDto = new CashOutTransferRequestDto();
        cashOutTransferRequestDto.setDebit(new BigDecimal(500));

        BigDecimal balance = new BigDecimal(10000);

        CashOutDtoResponse cashOutDtoResponse = new CashOutDtoResponse(new BankAccount(), new BigDecimal(100), new BigDecimal(1));

        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(balance);

        //WHEN
        when(bankServiceImpl.getBankAccount()).thenReturn(bankAccount);
        when(bankServiceImpl.controlBalance(cashOutTransferRequestDto.getDebit(), bankAccount.getBalance())).thenReturn(true);
        when(bankServiceImpl.transferToIban(cashOutTransferRequestDto)).thenReturn(cashOutDtoResponse);

        mockMvc.perform(post("/cash-out/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("debit", cashOutTransferRequestDto.getDebit().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cash-out?success"));


        //THEN
        verify(bankServiceImpl, times(1)).getBankAccount();
        verify(bankServiceImpl, times(1)).controlBalance(cashOutTransferRequestDto.getDebit(), balance);
        verify(bankServiceImpl, times(1)).transferToIban(cashOutTransferRequestDto);
        verify(transactionServiceImpl, times(1)).saveCashOut(cashOutDtoResponse);
    }


    @Test
    @WithMockUser
    @DisplayName("Error form save a cash out")
    public void saveCashOutFailed() throws Exception {

        //GIVEN
        CashOutTransferRequestDto cashOutTransferRequestDto = new CashOutTransferRequestDto();
        cashOutTransferRequestDto.setDebit(new BigDecimal(50000));

        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(new BigDecimal(100));

        CashOutRequestDto cashOutRequestDto = new CashOutRequestDto(bankAccount.getBalance(), "FR12345678901234567890123456");

        when(bankServiceImpl.getCashOut()).thenReturn(cashOutRequestDto);

        //WHEN
        mockMvc.perform(post("/cash-out/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("debit", cashOutTransferRequestDto.getDebit().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("bank/cashOut"));

        //THEN
        verify(bankServiceImpl, times(1)).getCashOut();
        verify(bankServiceImpl, times(0)).getBankAccount();
    }

    @Test
    @WithMockUser
    @DisplayName("Generate an an error insufficient fund to cash out")
    public void insufficientFundToCashOut() throws Exception {

        //GIVEN
        CashOutTransferRequestDto cashOutTransferRequestDto = new CashOutTransferRequestDto();
        cashOutTransferRequestDto.setDebit(new BigDecimal(500));
        BigDecimal balance = new BigDecimal(100);
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(balance);

        //WHEN
        when(bankServiceImpl.getBankAccount()).thenReturn(bankAccount);
        when(bankServiceImpl.controlBalance(cashOutTransferRequestDto.getDebit(), bankAccount.getBalance())).thenReturn(false);

        mockMvc.perform(post("/cash-out/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("debit", cashOutTransferRequestDto.getDebit().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cash-out?error"));

        //THEN
        verify(bankServiceImpl, times(1)).getBankAccount();
        verify(bankServiceImpl, times(1)).controlBalance(cashOutTransferRequestDto.getDebit(), balance);
    }

    @Test
    @WithMockUser
    @DisplayName("Error inIBAN")
    public void testSaveCashOutIbanFailed() throws Exception {

        //GIVEN
        IbanRequestDto ibanRequestDto = new IbanRequestDto();
        ibanRequestDto.setIban("");

        CashOutRequestDto cashOutRequestDto = new CashOutRequestDto(new BigDecimal(1000), ibanRequestDto.getIban());

        //WHEN
        when(bankServiceImpl.getCashOut()).thenReturn(cashOutRequestDto);

        mockMvc.perform(post("/cash-out/iban/save")
                        .param("iban", ibanRequestDto.getIban()))
                .andExpect(status().isOk())
                .andExpect(view().name("bank/cashOut"))
                .andExpect(model().attributeExists("ibanError"));

        //THEN
        verify(bankServiceImpl, times(1)).getCashOut();
        verify(bankServiceImpl, times(0)).saveIban(ibanRequestDto);
    }


    @Test
    @WithMockUser
    @DisplayName("save an IBAN")
    public void testSaveCashOutIban() throws Exception {

        //GIVEN
        IbanRequestDto ibanRequestDto = new IbanRequestDto();
        ibanRequestDto.setIban("FR12345678901234567890123456");

        CashOutRequestDto cashOutRequestDto = new CashOutRequestDto(new BigDecimal(1000), ibanRequestDto.getIban());

        //WHEN
        when(bankServiceImpl.getCashOut()).thenReturn(cashOutRequestDto);

        mockMvc.perform(post("/cash-out/iban/save")
                        .param("iban", ibanRequestDto.getIban()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cash-out?successIban"));

        //THEN
        verify(bankServiceImpl, times(1)).getCashOut();
        verify(bankServiceImpl, times(1)).saveIban(ibanRequestDto);
    }


    @Test
    @WithMockUser
    @DisplayName("Delete IBAN and return cash-out view")
    public void testDeleteIban() throws Exception {
        // GIVEN
        doNothing().when(bankServiceImpl).deleteIban();

        // WHEN
        mockMvc.perform(get("/cash-out/iban/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cash-out"));

        // THEN
        verify(bankServiceImpl, times(1)).deleteIban();
    }

//todo revoir ce test avec la pagination

//    @Test
//    @WithMockUser
//    @DisplayName("Get page transfer")
//    public void testGetTransfer() throws Exception {
//        // Créez des transactions fictives
//        List<Transaction> transactionList = new ArrayList<>();
//        Transaction transaction1 = new Transaction();
//        transaction1.setAmount(new BigDecimal(100));
//        transaction1.setDescription("test");
//        transaction1.setDate(LocalDateTime.now());
//        transaction1.setBankAccount(new BankAccount());
//        transaction1.setTransactionType(CREDIT);
//        transactionList.add(transaction1);
//
//        Page<Transaction> pageTransaction = new PageImpl<>(transactionList);
//
//        when(transactionServiceImpl.getTransactionsByBankAccount(any(BankAccount.class), eq(2))).thenReturn(pageTransaction);
//
//
//        mockMvc.perform(get("/transfer"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("transfer"));
//
//        verify(userServiceImpl, times(1)).getAuthFriendShip();
//        verify(bankServiceImpl, times(1)).getBankAccount();
//        verify(transactionServiceImpl, times(1)).getTransactionsByBankAccount(new BankAccount(), 0);
//
//    }


    @Test
    @WithMockUser
    @DisplayName("Save a new transfer")
    public void testTransferSave() throws Exception {
        // GIVEN
        UserCustom user = new UserCustom();
        user.setId(1);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setEmail("test@gmail.com");
        user.setPassword("password");


        TransferDto transferDto = new TransferDto();
        transferDto.setAmount(new BigDecimal(100));
        transferDto.setDescription("test_success_description");
        transferDto.setUserCustom(user);

        BankAccount bankAccount = new BankAccount();

        TransferDtoSave transferDtoSave = new TransferDtoSave();
        transferDto.setAmount(new BigDecimal(100));
        transferDto.setDescription("test_success_description");
        transferDto.setUserCustom(user);
        // WHEN
        when(bankServiceImpl.getBankAccount()).thenReturn(bankAccount);
        when(bankServiceImpl.controlBalance(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(true);
        when(bankServiceImpl.transferToFriend(any(TransferDto.class))).thenReturn(transferDtoSave);

        doNothing().when(transactionServiceImpl).saveTransaction(any(TransferDtoSave.class));


        mockMvc.perform(post("/transfer/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", String.valueOf(transferDto.getAmount()))
                        .param("description", transferDto.getDescription())
                        .param("userCustom.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?success"));

        //THEN
        verify(bankServiceImpl, times(1)).getBankAccount();
        verify(bankServiceImpl, times(1)).controlBalance(any(BigDecimal.class), any(BigDecimal.class));
        verify(bankServiceImpl, times(1)).transferToFriend(any(TransferDto.class));
        verify(transactionServiceImpl, times(1)).saveTransaction(any(TransferDtoSave.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Save a new transfer error in form")
    public void testTransferSaveFormError() throws Exception {
        // GIVEN
        TransferDto transferDto = new TransferDto();
        transferDto.setAmount(new BigDecimal(-100));
        transferDto.setDescription("test_invalid_description");
        transferDto.setUserCustom(new UserCustom());


        // WHEN
        mockMvc.perform(post("/transfer/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", String.valueOf(transferDto.getAmount()))
                        .param("description", transferDto.getDescription())
                        .param("userCustom.id))", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?error_input"));

        //THEN
        verify(transactionServiceImpl, times(0)).saveTransaction(any(TransferDtoSave.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Save a new transfer fund not enough")
    public void testTransferSaveNotFundEnough() throws Exception {
        // GIVEN
        UserCustom user = new UserCustom();
        user.setId(1);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setEmail("test@gmail.com");
        user.setPassword("password");


        TransferDto transferDto = new TransferDto();
        transferDto.setAmount(new BigDecimal(100));
        transferDto.setDescription("test_success_description");
        transferDto.setUserCustom(user);

        BankAccount bankAccount = new BankAccount();

        TransferDtoSave transferDtoSave = new TransferDtoSave();
        transferDto.setAmount(new BigDecimal(100));
        transferDto.setDescription("test_success_description");
        transferDto.setUserCustom(user);
        // WHEN
        when(bankServiceImpl.getBankAccount()).thenReturn(bankAccount);
        when(bankServiceImpl.controlBalance(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(false);
        when(bankServiceImpl.transferToFriend(any(TransferDto.class))).thenReturn(transferDtoSave);

        doNothing().when(transactionServiceImpl).saveTransaction(any(TransferDtoSave.class));


        mockMvc.perform(post("/transfer/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", String.valueOf(transferDto.getAmount()))
                        .param("description", transferDto.getDescription())
                        .param("userCustom.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?error"));

        //THEN
        verify(bankServiceImpl, times(1)).getBankAccount();
        verify(bankServiceImpl, times(1)).controlBalance(any(BigDecimal.class), any(BigDecimal.class));
        verify(bankServiceImpl, times(0)).transferToFriend(any(TransferDto.class));
        verify(transactionServiceImpl, times(0)).saveTransaction(any(TransferDtoSave.class));
    }


    @Test
    @WithMockUser
    @DisplayName("Display contact page")
    public void testContactPage() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact"));

    }


}