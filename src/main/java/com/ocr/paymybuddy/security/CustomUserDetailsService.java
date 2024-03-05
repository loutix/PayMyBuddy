package com.ocr.paymybuddy.security;

import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserCustom userCustom = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(userCustom.getEmail())
                .password(userCustom.getPassword())
                .roles("USER")
                .build();
    }


}
