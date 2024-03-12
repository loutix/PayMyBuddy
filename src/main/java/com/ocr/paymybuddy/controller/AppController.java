package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
public class AppController {


    private final UserService userService;

    public AppController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/add-connection")
    public String addConnection(Model model, Principal principal) {

        List<UserCustom> friendList = userService.getAuthFriendShip(principal);
        List<UserCustom> userList = userService.getAuthNotFriendShip(principal);

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
