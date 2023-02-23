package com.example.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("info")
    @ResponseStatus(HttpStatus.OK)
    public String testSecurity(HttpServletRequest request){
        System.err.println("sda");
        return "Hello from secured endpoint!";
    }

    @GetMapping("free")
    public String ms(HttpServletRequest request){
        return "Hello!";
    }

}
