package com.example.airlabproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Chỉ điều hướng tới trang index, mọi nghiệp vụ dùng REST API
    @GetMapping("/")
    public String home() {
        return "index";
    }
}