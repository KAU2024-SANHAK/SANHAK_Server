package org.kau.kkoolbeeServer.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;

@Getter
@AllArgsConstructor
public class DiaryContentResponseDto {
    private String diaryContent;
    private AdviceResponseDto advice;
    private String feeling;


}
