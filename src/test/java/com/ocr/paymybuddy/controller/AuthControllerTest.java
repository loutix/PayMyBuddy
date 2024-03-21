package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.dto.RegisterDto;
import com.ocr.paymybuddy.repository.UserRepository;
import com.ocr.paymybuddy.security.SecurityConfig;
import com.ocr.paymybuddy.service.BankServiceImpl;
import com.ocr.paymybuddy.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = {SecurityConfig.class, AuthController.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private BankServiceImpl bankServiceImpl;

    @MockBean
    private UserRepository userRepository;


    @Test
    @DisplayName("Get the login page")
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/login"));
    }

    @Test
    @DisplayName("Get the register page")
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeExists("registerDto"));
    }

    @Test
    @DisplayName("Post new user")
    public void testSaveRegistrationSuccess() throws Exception {

        //GIVEN
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName("Bernard");
        registerDto.setLastName("Bianci");
        registerDto.setEmail("bernard@gmail.com");
        registerDto.setPassword("password");

        //WHEN
        mockMvc.perform(post("/register/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", registerDto.getEmail())
                        .param("password", registerDto.getPassword())
                        .param("firstName", registerDto.getFirstName())
                        .param("lastName", registerDto.getLastName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));

    }

    @Test
    @DisplayName("Post non unique user email")
    public void testSaveRegistrationError() throws Exception {

        //GIVEN
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName("Bernard");
        registerDto.setLastName("Bianci");
        registerDto.setEmail("bernard@gmail.com");
        registerDto.setPassword("password");

        // WHEN
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        mockMvc.perform(post("/register/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", registerDto.getEmail())
                        .param("password", registerDto.getPassword())
                        .param("firstName", registerDto.getFirstName())
                        .param("lastName", registerDto.getLastName()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeExists("emailNotUnique"));

        //THEN
        verify(userRepository, times(1)).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Post new user with validation errors")
    public void testSaveRegistrationValidationError() throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName("");
        registerDto.setLastName("");
        registerDto.setEmail("");
        registerDto.setPassword("");

        // When
        mockMvc.perform(post("/register/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", registerDto.getEmail())
                        .param("password", registerDto.getPassword())
                        .param("firstName", registerDto.getFirstName())
                        .param("lastName", registerDto.getLastName()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeExists("registerDto"))
                .andExpect(model().attributeHasFieldErrors("registerDto", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registerDto", "lastName"))
                .andExpect(model().attributeHasFieldErrors("registerDto", "email"))
                .andExpect(model().attributeHasFieldErrors("registerDto", "password"));
    }
}