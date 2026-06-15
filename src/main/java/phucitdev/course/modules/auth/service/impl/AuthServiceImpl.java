package phucitdev.course.modules.auth.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phucitdev.course.commo.exception.auth.*;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.auth.dto.*;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.entity.RefreshToken;
import phucitdev.course.modules.auth.entity.Role;
import phucitdev.course.modules.auth.repository.AuthRepository;
import phucitdev.course.modules.auth.repository.RefreshTokenRepository;
import phucitdev.course.modules.auth.security.CustomUserDetails;
import phucitdev.course.modules.auth.security.JwtTokenProvider;
import phucitdev.course.modules.auth.security.SecurityUtils;
import phucitdev.course.modules.auth.service.AuthService;
import phucitdev.course.modules.studentProfile.entity.StudentProfile;
import phucitdev.course.modules.studentProfile.repository.StudentProfileRepository;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;
import phucitdev.course.modules.teacherProfile.repository.TeacherProfileRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    AuthRepository authRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    TeacherProfileRepository teacherProfileRepository;
    @Autowired
    StudentProfileRepository studentProfileRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email đã tồn tại");
        }
        Role role = Role.from(request.getRole());
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setFullName(request.getFullName());
        account.setRole(role);
        account.setIsActive(true);
        authRepository.save(account);
        switch (role) {
            case TEACHER -> {
                TeacherProfile teacherProfile =
                        new TeacherProfile();
                teacherProfile.setAccount(account);
                teacherProfileRepository
                        .save(teacherProfile);
            }
            case STUDENT -> {
                StudentProfile studentProfile =
                        new StudentProfile();
                studentProfile.setAccount(account);
                studentProfileRepository
                        .save(studentProfile);
            }
            default -> {
                // ADMIN
            }
        }
        return new RegisterResponse("Đăng ký thành công!");
    }
    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Account account = userDetails.getAccount();

            if (Boolean.FALSE.equals(account.getIsActive())) {
                throw new ForbiddenException("Tài khoản của bạn đã bị khóa");
            }
            Role requestRole = Role.from(request.getRole());
            if (!account.getRole().equals(requestRole)) {
                throw new ForbiddenException("Bạn không thể đăng nhập với vai trò này");
            }
            String accessToken = jwtTokenProvider.generateAccessToken(account.getId(), account.getEmail());
            String refreshToken = jwtTokenProvider.generateRefreshToken(account, request.getDeviceId(), request.getDeviceInfo());

            UserInfo user = new UserInfo();
            user.setId(account.getId().toString());
            user.setEmail(account.getEmail());
            user.setFullName(account.getFullName());
            user.setRole(account.getRole().name());

            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    "Đăng nhập thành công!",
                    user
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken  = request.getRefreshToken();
        jwtTokenProvider.validateRefreshToken(refreshToken);
        RefreshToken tokenInDb = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(InvalidTokenException::new);
        if (tokenInDb.isRevoked()) {
            throw new InvalidTokenException();
        }
        if (tokenInDb.getExpiredAt().before(new Date())) {
            tokenInDb.setRevoked(true);
            refreshTokenRepository.save(tokenInDb);
            throw new TokenExpiredException();
        }
        Account account = tokenInDb.getAccount();
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                account.getId(),
                account.getEmail()
        );
        return new  RefreshTokenResponse(newAccessToken);
    }

    @Override
    public void logoutOfSystem(LogoutRequest logoutRequest) {
        RefreshToken refreshTokenInDB = jwtTokenProvider.verifyRefreshToken(logoutRequest.getRefreshToken());
        refreshTokenInDB.setRevoked(true);
        refreshTokenRepository.save(refreshTokenInDB);
    }
    @Override
    @Transactional
    public BlockAccountResponse blockAccount(UUID accountId, BlockAccountRequest request) {
        try {
            Account account = authRepository.findById(accountId).orElseThrow(() ->
                            new NotFoundException(
                                    "Không tìm thấy tài khoản"
                            ));
            switch (request.getType()) {
                case BLOCK -> {

                    if (!account.getIsActive()) {
                        throw new BadRequestException(
                                "Tài khoản đã bị chặn trước đó"
                        );
                    }
                    account.setIsActive(false);
                }
                case UNBLOCK -> {
                    if (account.getIsActive()) {
                        throw new BadRequestException(
                                "Tài khoản đang hoạt động"
                        );
                    }

                    account.setIsActive(true);
                }
            }

            authRepository.save(account);

            return new BlockAccountResponse(
                    request.getType().name()
                            .equals("BLOCK")
                            ? "Chặn tài khoản thành công!"
                            : "Mở chặn tài khoản thành công!"
            );

        } catch (Exception e) {
            throw new BadRequestException(
                    e.getMessage()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> getAccounts(Pageable pageable, String keyword, Role role) {
        try {
            Page<Account> accounts = authRepository.findAccounts(keyword, role, pageable);
            return accounts.map(account ->
                    new AccountResponse(
                            account.getId(),
                            account.getFullName(),
                            account.getEmail(),
                            account.getRole(),
                            account.getIsActive()
                    )
            );

        } catch (Exception e) {
            throw new BadRequestException(
                    "Lỗi khi lấy danh sách tài khoản"
            );
        }
    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
         Account account = SecurityUtils.getCurrentAccount();
        // check confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }
        // encode password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        // update password
        account.setPassword(encodedPassword);
        authRepository.save(account);
        return new ChangePasswordResponse("Đổi mật khẩu thành công!");
    }
}
