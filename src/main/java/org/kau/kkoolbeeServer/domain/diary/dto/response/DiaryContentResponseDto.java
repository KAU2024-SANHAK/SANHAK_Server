package org.kau.kkoolbeeServer.domain.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;

@Getter
@AllArgsConstructor
public class DiaryContentResponseDto {
    @JsonProperty("diary_content")
    private String diaryContent;
    private AdviceResponseDto advice;
    private String feeling;


}
