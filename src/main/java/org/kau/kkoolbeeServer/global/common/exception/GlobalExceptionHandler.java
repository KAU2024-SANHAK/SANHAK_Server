package org.kau.kkoolbeeServer.global.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.model.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static org.kau.kkoolbeeServer.global.common.enums.ErrorType.INTERNAL_SERVER_ERROR;
import static org.kau.kkoolbeeServer.global.common.enums.ErrorType.REQUEST_VALIDATION_EXCEPTION;

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

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validateDetails.put(validKeyName, error.getDefaultMessage());
        }
        return ApiResponse.error(REQUEST_VALIDATION_EXCEPTION, validateDetails);
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
    protected ApiResponse<Exception> handleException(final Exception e, final HttpServletRequest request) throws IOException {
        return ApiResponse.error(INTERNAL_SERVER_ERROR, e);
    }
}
