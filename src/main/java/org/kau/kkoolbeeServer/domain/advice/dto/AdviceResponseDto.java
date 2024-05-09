package org.kau.kkoolbeeServer.domain.advice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kau.kkoolbeeServer.domain.advice.Advice;

@Getter
@AllArgsConstructor
public class AdviceResponseDto {
    private String spicy;
    private String kind;

    public static AdviceResponseDto fromAdviceOrNull(Advice advice) {
        if (advice == null) {
            return new AdviceResponseDto(null, null);
        } else {
            return new AdviceResponseDto(advice.getSpicy_advice(), advice.getKind_advice());
        }
    }  //advice가 null이어도 응답메시지를 보내야하므로
}
