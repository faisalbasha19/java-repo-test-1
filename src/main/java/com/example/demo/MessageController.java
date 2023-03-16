package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    
    @RequestMapping("/message")
    public String message() {
        return "Hello I am faisal bin basha";
    }
}
