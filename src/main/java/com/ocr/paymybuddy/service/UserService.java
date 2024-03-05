package com.ocr.paymybuddy.service;

import com.ocr.paymybuddy.dto.RegisterDto;
import com.ocr.paymybuddy.model.FriendShip;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.UserRepository;
import com.ocr.paymybuddy.utilities.AuthUtils;
import jakarta.validation.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user
     *
     * @param registerDto registerDto
     * @return UserCustom
     */
    public UserCustom saveUser(RegisterDto registerDto) {

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ValidationException("There is already an account registered with the same email" + registerDto.getEmail());
        }

        UserCustom userCustom = new UserCustom();
        userCustom.setFirstName(registerDto.getFirstName());
        userCustom.setLastName(registerDto.getLastName());
        userCustom.setEmail(registerDto.getEmail());
        userCustom.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        userRepository.save(userCustom);

        return userCustom;
    }


    /**
     * Get all users except the current user
     *
     * @return List<UserCustom>
     */
    public List<UserCustom> findAllUsers() {

        String currentEmail = AuthUtils.getCurrentUserName();
        List<UserCustom> allUsers = userRepository.findAll();

        return allUsers.stream()
                .filter(user -> !user.getEmail().equals(currentEmail))
                .toList();
    }

    /**
     * Get all userCustoms from user friendships
     *
     * @param userDetails userDetails
     * @return List<UserCustom>
     */
    public List<UserCustom> getAuthFriendShip(UserDetails userDetails) {
        UserCustom userCustom = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userCustom.getFriendShipList().stream().map(FriendShip::getFriend).toList();
    }

    /**
     * Get all users except user friendship
     *
     * @param userDetails userDetails
     * @return List<UserCustom>
     */
    public List<UserCustom> getAuthNotFriendShip(UserDetails userDetails) {

        List<UserCustom> allUsers = this.findAllUsers();
        List<UserCustom> friendList = this.getAuthFriendShip(userDetails);

        return allUsers.stream()
                .filter(customUser -> !friendList.contains(customUser))
                .toList();
    }


    /**
     * Add new friendship
     *
     * @param id id
     */
    public void addFriendShip(Integer id) {
        UserCustom userCustom = userRepository.findByEmail(AuthUtils.getCurrentUserName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserCustom friend = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + id));
        userCustom.addFriend(friend);
        userRepository.save(userCustom);
    }


    /**
     * Delete a friendship
     *
     * @param id id
     */
    public void deleteFriendShip(Integer id) {
        UserCustom userCustom = userRepository.findByEmail(AuthUtils.getCurrentUserName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserCustom friend = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + id));
        userCustom.deleteFriend(friend);
        userRepository.save(userCustom);
    }
}
