package phucitdev.course.commo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import phucitdev.course.commo.exception.auth.*;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.commo.exception.material.DuplicationException;
import phucitdev.course.commo.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(BadRequestException ex) {
        ApiResponse<?> response = new ApiResponse<>(
                        400,
                        ex.getMessage(),
                        null
                );
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "message",
                        "Chỉ ADMIN mới được thực hiện thao tác này"
                ));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<?> response = new ApiResponse<>(400, "Validation failed", errors);

        return ResponseEntity.status(400).body(response);
    }
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailExists(AlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(409, ex.getMessage(), null));
    }
    // Account
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, ex.getMessage(), null));
    }
    // classroom
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAccountNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, ex.getMessage(), null));
    }
    // material
    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<ApiResponse<?>> handleAccountNotFound(DuplicationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(409, ex.getMessage(), null));
    }
    // AUTH
    @ExceptionHandler(UnauthenticatedException.class)
    public ApiResponse<?> handleUnauthenticated(UnauthenticatedException ex) {
        return new ApiResponse<>(401, ex.getMessage(), null);
    }
    // Username or password wrong
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(401)
                .body(new ApiResponse<>(401, ex.getMessage(), null));
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<?>> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity
                .status(403)
                .body(new ApiResponse<>(403, ex.getMessage(), null));
    }
    // TOKEN
    @ExceptionHandler(MissingTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingToken(MissingTokenException ex) {
        ApiResponse<?> response = new ApiResponse<>(401, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(InvalidTokenTypeException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidTokenType(InvalidTokenTypeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(401, ex.getMessage(), null));
    }
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<?>> handleExpired(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(401, ex.getMessage(), null));
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(401, ex.getMessage(), null));
    }
    @ExceptionHandler(InvalidDeviceIdException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidRequest(InvalidDeviceIdException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, ex.getMessage(), null));
    }
}
