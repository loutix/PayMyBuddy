package com.ocr.paymybuddy.interfaces;

import com.ocr.paymybuddy.dto.RegisterDto;
import com.ocr.paymybuddy.model.UserCustom;

import java.util.List;

public interface UserService {

    /**
     * Saves a new user based on the provided registration details.
     *
     * @param registerDto the registration details of the user
     * @return the saved user
     */
    UserCustom saveUser(RegisterDto registerDto);

    /**
     * Retrieves a list of all users.
     *
     * @return a list of all users
     */
    List<UserCustom> findAllUsers();

    /**
     * Retrieves a list of friends for the authenticated user.
     *
     * @return a list of friends for the authenticated user
     */
    List<UserCustom> getAuthFriendShip();

    /**
     * Retrieves a list of users who are not friends with the authenticated user.
     *
     * @return a list of users who are not friends with the authenticated user
     */
    List<UserCustom> getAuthNotFriendShip();

    /**
     * Adds a friend for the authenticated user.
     *
     * @param id the ID of the user to be added as a friend
     */
    void addFriendShip(Integer id);

    /**
     * Deletes a friend for the authenticated user.
     *
     * @param id the ID of the friend to be removed
     */
    void deleteFriendShip(Integer id);

}
