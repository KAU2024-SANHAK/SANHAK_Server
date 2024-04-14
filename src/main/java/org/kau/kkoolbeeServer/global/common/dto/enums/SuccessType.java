package org.kau.kkoolbeeServer.global.common.dto.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessType {

    /**
     * 200 OK
     */
    PROCESS_SUCCESS(HttpStatus.CREATED, "요청이 성공했습니다."), //api 명세서에 201로 나와있기에 바꿨습니다
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
