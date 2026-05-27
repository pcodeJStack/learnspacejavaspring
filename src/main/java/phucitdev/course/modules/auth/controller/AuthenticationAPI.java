package phucitdev.course.modules.auth.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.auth.dto.*;
import phucitdev.course.modules.auth.service.AuthService;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin("*")
@RequestMapping("/api/auth/")
@RestController

public class AuthenticationAPI {
    @Autowired
    AuthService authService;
    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest ){
       LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse refreshTokenResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(refreshTokenResponse);
    }
//    @PostMapping("logout")
//    public ResponseEntity logout(@RequestBody LogoutRequest LogoutRequest ) {
//        if (LogoutRequest == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("message", "Refresh token missing"));
//        }
//        if(LogoutRequest != null){
//            authService.logoutOfSystem(LogoutRequest);
//        }
//
//        Map<String, Object> res = new LinkedHashMap<>();
//        res.put("message", "Logout successfully");
//        return ResponseEntity.ok(res);
//    }
}
