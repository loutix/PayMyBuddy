package com.ocr.paymybuddy.controller;

import com.ocr.paymybuddy.model.UserCustom;
import com.ocr.paymybuddy.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AppController {


    private final UserServiceImpl userServiceImpl;

    public AppController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }


    @GetMapping("/add-connection")
    public String addConnection(Model model) {

        List<UserCustom> friendList = userServiceImpl.getAuthFriendShip();
        List<UserCustom> userList = userServiceImpl.getAuthNotFriendShip();

        model.addAttribute("friendList", friendList);
        model.addAttribute("userList", userList);

        return "addConnection";
    }

    @GetMapping("/add-friend/{id}")
    public String AddFriendShip(@PathVariable(value = "id") Integer id) {
        this.userServiceImpl.addFriendShip(id);
        return "redirect:/add-connection";
    }

    @GetMapping("/delete-friend/{id}")
    public String DeleteFriendShip(@PathVariable(value = "id") Integer id) {
        this.userServiceImpl.deleteFriendShip(id);
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
