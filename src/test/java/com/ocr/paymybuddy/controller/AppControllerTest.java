package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.security.SecurityConfig;
import com.ocr.paymybuddy.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppController.class)
@ContextConfiguration(classes = {SecurityConfig.class, AppController.class})
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserServiceImpl userServiceImpl;


    @Test
    @WithMockUser
    @DisplayName("Get form add friend")
    public void testAddConnection() throws Exception {
        // Given
        List<UserCustom> friendList = new ArrayList<>();
        List<UserCustom> userList = new ArrayList<>();
        when(userServiceImpl.getAuthFriendShip()).thenReturn(friendList);
        when(userServiceImpl.getAuthNotFriendShip()).thenReturn(userList);

        // When/Then
        mockMvc.perform(get("/add-connection"))
                .andExpect(status().isOk())
                .andExpect(view().name("addConnection"))
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().attribute("friendList", friendList))
                .andExpect(model().attributeExists("userList"))
                .andExpect(model().attribute("userList", userList));
    }

    @Test
    @WithMockUser
    @DisplayName("Save new friend")
    public void testSaveNewFriend() throws Exception {

        //Given
        Integer id = 1;

        //When //Then
        mockMvc.perform(get("/add-friend/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add-connection"));

        verify(userServiceImpl, times(1)).addFriendShip(id);
    }


    @Test
    @WithMockUser
    @DisplayName("delete a friend")
    public void testDeleteFriend() throws Exception {
        //Given
        Integer id = 1;
        //When //Then
        mockMvc.perform(get("/delete-friend/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add-connection"));

        verify(userServiceImpl, times(1)).deleteFriendShip(id);
    }


    @Test
    @WithMockUser
    @DisplayName("display home page")
    public void testWelcomePage() throws Exception {

        //When //Then
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

    }

    @Test
    @WithMockUser
    @DisplayName("others display home page")
    public void testOtherWelcomePage() throws Exception {

        //When //Then
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

    }


}


