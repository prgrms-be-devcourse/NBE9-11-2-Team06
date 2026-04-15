//package com.back.nbe9112team06.global.exceptionHandler;
//
//import com.back.nbe9112team06.global.dto.ApiResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.resource.NoResourceFoundException;
//
//import java.util.stream.Collectors;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    // 1. 비즈니스 예외
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
//        log.warn("BusinessException: {}", ex.getMessage(), ex);
//        return ResponseEntity.status(ex.getHttpStatus())
//                .body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
//    }
//
//    // 2. @Valid 검증 실패
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
//        String message = ex.getBindingResult().getFieldErrors().stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.joining(", "));
//        return ResponseEntity.badRequest()
//                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
//    }
//
//    // 3. 404 Not Found (Spring Boot 3.2 이상)
//    @ExceptionHandler(NoResourceFoundException.class)
//    public ResponseEntity<ApiResponse<?>> handleNotFound(NoResourceFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "요청한 리소스를 찾을 수 없습니다."));
//    }
//
//    // 4. 모든 예상치 못한 예외 (Fallback)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
//        log.error("Unexpected exception occurred", ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."));
//    }
//}
