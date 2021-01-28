package de.uniba.dsg.beverage_store.spring_boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {

    @GetMapping
    public String getRegister() {
        return "register";
    }
}
