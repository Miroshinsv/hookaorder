package ru.hookaorder.backend.feature.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hookaorder.backend.feature.JWT.entity.JWTRequest;
import ru.hookaorder.backend.feature.JWT.entity.JWTResponse;
import ru.hookaorder.backend.feature.JWT.entity.RefreshJwtRequest;
import ru.hookaorder.backend.feature.auth.service.AuthService;

import javax.security.auth.message.AuthException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest authRequest) throws AuthException {
        final JWTResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<JWTResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JWTResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JWTResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
