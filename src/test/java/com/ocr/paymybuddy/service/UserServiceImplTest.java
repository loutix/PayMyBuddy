
package com.ocr.paymybuddy.service;

import com.ocr.paymybuddy.dto.RegisterDto;
import com.ocr.paymybuddy.model.FriendShip;
import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.repository.UserRepository;
import com.ocr.paymybuddy.utilities.AuthUtils;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    UserServiceImpl userServiceImpl;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthUtils authUtils;

    private final List<UserCustom> userCustomList = new ArrayList<>();

    UserCustom userCustom1 = new UserCustom();
    UserCustom userCustom2 = new UserCustom();
    UserCustom userCustom3 = new UserCustom();

    @BeforeEach
    public void setUp() {

        userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder, authUtils);

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

        userCustomList.add(userCustom1);
        userCustomList.add(userCustom2);
        userCustomList.add(userCustom3);


//        FriendShip User2

        List<FriendShip> friendShipList = new ArrayList<>();
        FriendShip friendShip1 = new FriendShip();
        friendShip1.setFriend(userCustom1);

        FriendShip friendShip2 = new FriendShip();
        friendShip2.setFriend(userCustom3);

        friendShipList.add(friendShip1);
        friendShipList.add(friendShip2);

        userCustom2.setFriendShipList(friendShipList);
    }


    @Test
    @WithMockUser
    @DisplayName("save a new user")
    public void testSaveUser() {
        //GIVEN
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");
        registerDto.setEmail("john.doe@test.com");
        registerDto.setPassword("password");


        //WHEN
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserCustom.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserCustom savedUser = userServiceImpl.saveUser(registerDto);

        //THEN
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(UserCustom.class));
        assertNotNull(savedUser);
        assertEquals("John Doe", savedUser.getFullName());
        assertEquals(registerDto.getEmail(), savedUser.getEmail());
    }

    @Test
    @WithMockUser
    @DisplayName("save a new user but already exist")
    public void testSaveUserFailed() {
        //GIVEN
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");
        registerDto.setEmail("john.doe@test.com");
        registerDto.setPassword("password");

        //WHEN
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        //THEN
        assertThrows(ValidationException.class, () -> userServiceImpl.saveUser(registerDto));
        verify(userRepository, times(1)).existsByEmail(anyString());
    }


    @Test
    @WithMockUser
    @DisplayName("Find all users")
    public void testFindAllUsers() {
        //GIVEN
        String email = "loic@test.com";

        //WHEN
        when(authUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findAll()).thenReturn(userCustomList);
        List<UserCustom> result = userServiceImpl.findAllUsers();

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(userRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertFalse(result.stream().anyMatch(user -> user.getEmail().equals(email)));
        assertTrue(result.stream().anyMatch(user -> user.getEmail().equals("chloe@test.com")));
    }

    @Test
    @WithMockUser
    @DisplayName("Find all users")
    public void testGetAuthFriendShip() {

        //GIVEN
        String email = "loic@test.com";

        //WHEN
        when(authUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(userCustom2));

        List<UserCustom> result = userServiceImpl.getAuthFriendShip();

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(userRepository, times(1)).findByEmail(email);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(userCustom -> userCustom.getEmail().equals(userCustom1.getEmail())));
        assertTrue(result.stream().anyMatch(userCustom -> userCustom.getEmail().equals(userCustom3.getEmail())));
    }

    @Test
    @WithMockUser
    @DisplayName("Find all users failed")
    public void testGetAuthFriendShipFailed() {

        //GIVEN
        String email = "noexist@test.com";

        //WHEN
        when(authUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.getAuthFriendShip());

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(userRepository, times(1)).findByEmail(email);

    }


    //todo help chloé, fonction qui appel une autre fonction. deander pour Transactionnal RollBack
//    @Test
//    @WithMockUser
//    @DisplayName("Find not friend")
//    public void testGetAuthNotFriendShip() {
//
//        //GIVEN
//        UserCustom currentUser = userCustom2;
//        List<UserCustom> friendList = new ArrayList<>();
//        friendList.add(userCustom3);
//        String currentUserEmail = "loic@test.com";
//
//
//        when(authUtils.getCurrentUserEmail()).thenReturn(currentUserEmail);
//        when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(currentUser));
//
//        when(userServiceImpl.findAllUsers()).thenReturn(userCustomList);
//        when(userServiceImpl.getAuthNotFriendShip()).thenReturn(friendList);
//
//        //WHEN
//        List<UserCustom> result = userServiceImpl.getAuthNotFriendShip();
//
//        //THEN
//        verify(userServiceImpl, times(1)).findAllUsers();
//        verify(userServiceImpl,times(1)).getAuthNotFriendShip();
//    }

    @Test
    @WithMockUser
    @DisplayName("Add new friend")
    public void testAddFriendShip() {

        //GIVEN
        UserCustom currentUser = userCustom2;
        String currentUserEmail = "loic@test.com";
        Integer id = 2;

        when(authUtils.getCurrentUserEmail()).thenReturn(currentUserEmail);
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));

        userRepository.save(currentUser);

        //WHEN
        userServiceImpl.addFriendShip(id);

        // THEN
        verify(userRepository, times(1)).findByEmail(currentUserEmail);
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(2)).save(currentUser);
    }

    @Test
    @WithMockUser
    @DisplayName("Delete a friend")
    public void testDeleteFriendShip() {

        //GIVEN
        UserCustom currentUser = userCustom2;
        String currentUserEmail = "loic@test.com";
        Integer id = 2;

        when(authUtils.getCurrentUserEmail()).thenReturn(currentUserEmail);
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));

        userRepository.save(currentUser);

        //WHEN
        userServiceImpl.deleteFriendShip(id);
        // THEN
        verify(userRepository, times(1)).findByEmail(currentUserEmail);
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(2)).save(currentUser);
    }

    @Test
    @WithMockUser
    @DisplayName("Delete a friend failed")
    public void testDeleteFriendShipFailed() {

        //GIVEN
        String currentUserEmail = "loic@test.com";
        Integer id = 2;

        when(authUtils.getCurrentUserEmail()).thenReturn(currentUserEmail);
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.empty());

        // THEN
        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.deleteFriendShip(id));

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(userRepository, times(1)).findByEmail(currentUserEmail);

    }

    @Test
    @WithMockUser
    @DisplayName("Delete a friend failed User not found with email")
    public void testDeleteFriendShipFailedUserNotFound() {

        //GIVEN
        UserCustom currentUser = userCustom2;
        String currentUserEmail = "loic@test.com";
        Integer id = 2;

        when(authUtils.getCurrentUserEmail()).thenReturn(currentUserEmail);
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // THEN
        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.deleteFriendShip(id));

        //THEN
        verify(authUtils, times(1)).getCurrentUserEmail();
        verify(userRepository, times(1)).findByEmail(currentUserEmail);
        verify(userRepository, times(1)).findById(id);

    }
}