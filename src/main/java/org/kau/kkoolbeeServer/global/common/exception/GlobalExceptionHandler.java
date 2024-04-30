package org.kau.kkoolbeeServer.global.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.exception.model.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType.INTERNAL_SERVER_ERROR;
import static org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType.REQUEST_VALIDATION_ERROR;

@Slf4j // 로그 출력
@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {
// 오류를 ApiResponse에 담아서 처리해주는 친구 (감시하고 있음)

//    @Size(min = 2, max = 8, message = "닉네임은 2글자 이상 8글자 이하여야합니다.")
//    private String nickname;
//    + Controller 계층에 @Valid 어노테이션
    /**
     * VALIDATION_ERROR
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse<?> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        Errors errors = e.getBindingResult();
        Map<String, String> validateDetails = new HashMap<>();
        log.error("Unexpected error occurred not valid", e);

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validateDetails.put(validKeyName, error.getDefaultMessage());
        }
        return ApiResponse.error(REQUEST_VALIDATION_ERROR, validateDetails);
    }

    /**
     * CUSTOM_ERROR
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<?>> handleBusinessException(CustomException e) {

        log.error("[EXCEPTION] CustomException Occured: {}", e.getMessage(), e);

        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getErrorType(), e.getMessage()));
    }

    /**
     * 500 INTERNEL_SERVER
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<?> handleException(final Exception e, final HttpServletRequest request) throws IOException {
        log.error("Unexpected error occurred", e);
        return ApiResponse.error(INTERNAL_SERVER_ERROR);
    }

    //이부분수정   서버내부오류는 오류 데이터를 줄 필요가 없이 서버내부오류만 띄우면 되므로!!

    @ResponseStatus(HttpStatus.BAD_REQUEST)    //이부분추가
    @ExceptionHandler(HttpMessageConversionException.class)
    protected ApiResponse<?> handleHttpMessageConversionException(HttpMessageConversionException e) {
        // 로그 기록, 에러 메시지 생성 등 필요한 처리
        log.error("Unexpected error occurred", e);
        return ApiResponse.error(REQUEST_VALIDATION_ERROR, "잘못된 요청 형식입니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ApiResponse<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        // 로그 기록, 오류 메시지 생성 및 기타 필요한 처리
        log.error("Unexpected error occurred", e);
        return ApiResponse.error(REQUEST_VALIDATION_ERROR, "잘못된 요청 형식입니다.");
    }

    //이부분추가


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    protected ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        // 로그 기록, 오류 메시지 생성 및 기타 필요한 처리
        log.error("Unexpected error occurred", e);
        return ApiResponse.error(REQUEST_VALIDATION_ERROR, "잘못된 요청 형식입니다.");
    }

    //이부분추가





}
