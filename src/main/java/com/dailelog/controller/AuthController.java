package com.dailelog.controller;

import com.dailelog.config.AppConfig;
import com.dailelog.request.Signup;
import com.dailelog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AppConfig appConfig;

    @PostMapping("/auth/signup")
    public void signup(@RequestBody @Valid Signup signup) {
        authService.signup(signup);
    }
}
