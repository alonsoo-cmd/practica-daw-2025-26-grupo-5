package com.stilnovo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UknownController {
    
    @GetMapping("/uknown")
    public String uknown(Model model){

        model.addAttribute("name", "World"); //{{Clave}}, Su-valor

        return "uknown_template"; //vista html con esa info
    }
}