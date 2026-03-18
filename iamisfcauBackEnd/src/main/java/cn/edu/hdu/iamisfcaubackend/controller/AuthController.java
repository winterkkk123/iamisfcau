package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.LoginRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UserMeDto;
import cn.edu.hdu.iamisfcaubackend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public UserMeDto login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}