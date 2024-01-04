package cn.master.zeus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by 11's papa on 01/02/2024
 **/
@RestController
@RequestMapping("/home")
public class HomeController {
    @PostMapping("/public")
    public ResponseEntity<String> publicResource() {
        return ResponseEntity.ok("public Resource ");
    }

    @GetMapping("/protected")
    public String protectedResource() {
        return "protected Resource ";
    }
}
