package phucitdev.course.modules.auth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import phucitdev.course.modules.auth.dto.*;
import phucitdev.course.modules.auth.entity.Role;

import java.util.UUID;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    void logoutOfSystem(LogoutRequest logoutRequest);
    BlockAccountResponse blockAccount(UUID accountId,  BlockAccountRequest request);
    Page<AccountResponse> getAccounts(Pageable pageable, String keyword, Role role);
}
