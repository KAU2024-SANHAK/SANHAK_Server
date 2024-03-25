package org.kau.kkoolbeeServer.global.common.model;

import lombok.Getter;
import org.kau.kkoolbeeServer.global.common.enums.ErrorType;

//[호출 관계] Controller(요청 처리) -> Service(순서) -> Repository -> DB
//throw new CustomException(ERROR_TYPE);
//RuntimeException은 "특별한 애 (언체크 예외)"
@Getter
public class CustomException extends RuntimeException {

    private final ErrorType errorType;

    public CustomException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public int getHttpStatus() {
        return errorType.getHttpStatusCode();
    }
}
