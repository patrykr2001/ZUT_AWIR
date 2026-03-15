package edu.zut.awir.awir1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HelloWorldController {
    @GetMapping("/")
    String hello() {
        return "Hello World!";
    }
}
