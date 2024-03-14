package com.ocr.paymybuddy.interfaces;

import com.ocr.paymybuddy.dto.RegisterDto;
import com.ocr.paymybuddy.model.UserCustom;

import java.util.List;

public interface UserService {

    UserCustom saveUser(RegisterDto registerDto);
    List<UserCustom> findAllUsers();
    List<UserCustom> getAuthFriendShip();
    List<UserCustom> getAuthNotFriendShip();
    void addFriendShip(Integer id);

    void deleteFriendShip(Integer id);

}
