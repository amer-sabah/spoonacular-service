package com.wiley.spoonacular.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SpoonacularServiceController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spoonacular Service!";
    }
}
