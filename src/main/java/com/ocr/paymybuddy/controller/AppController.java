package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AppController {


    private final UserService userService;

    public AppController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/add-connection")
    public String addConnection(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        List<UserCustom> friendList = userService.getAuthFriendShip(userDetails);
        List<UserCustom> userList = userService.getAuthNotFriendShip(userDetails);

        model.addAttribute("friendList", friendList);
        model.addAttribute("userList", userList);

        return "addConnection";
    }

    @GetMapping("/add-friend/{id}")
    public String AddFriendShip(@PathVariable(value = "id") Integer id) {
        this.userService.addFriendShip(id);
        return "redirect:/add-connection";
    }

    @GetMapping("/delete-friend/{id}")
    public String DeleteFriendShip(@PathVariable(value = "id") Integer id) {
        this.userService.deleteFriendShip(id);
        return "redirect:/add-connection";
    }


    //others pages
    @GetMapping("/home")
    public String welcomePage() {
        return "home";
    }

    @GetMapping("/")
    public String HomePage() {
        return "redirect:/home";
    }





}
