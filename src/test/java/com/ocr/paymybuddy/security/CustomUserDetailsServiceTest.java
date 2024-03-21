package com.ocr.paymybuddy.security;

import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {


    CustomUserDetailsService customUserDetailsService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    @WithMockUser
    void loadUserByUsername() {

        //GIVEN
        String email = "loic@test.com";

        UserCustom user = new UserCustom();
        user.setFirstName("Loïc");
        user.setLastName("test");
        user.setEmail("loic@test.com");
        user.setPassword("password");
        user.setId(1);


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //WHEN
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        System.out.println(result.getAuthorities());

        //THEN
        verify(userRepository,times(1)).findByEmail(anyString());
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));

    }
}