package com.ocr.paymybuddy.repository;

import com.ocr.paymybuddy.model.UserCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserCustom,Integer> {
    Optional<UserCustom> findByEmail(String email);

    Boolean existsByEmail(String email);


}
