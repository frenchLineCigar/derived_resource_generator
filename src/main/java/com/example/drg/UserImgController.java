package com.example.drg;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserImgController {
    @RequestMapping("/img")
    public String showImg() {
        return "안녕";
    }
}
