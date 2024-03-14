package com.ocr.paymybuddy.security;

import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * load current user details from database.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user details from database and create an UserDetails Object to allow  Spring Security to find this user in the application.
     *
     * @param email email
     * @return UserDetails
     * @throws UsernameNotFoundException e
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserCustom userCustom = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(userCustom.getEmail())
                .password(userCustom.getPassword())
                .build();
    }


}
