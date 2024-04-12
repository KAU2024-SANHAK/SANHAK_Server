package org.kau.kkoolbeeServer.domain.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
@JsonPropertyOrder({ "diary_content", "advice","feeling"})

@Getter
@AllArgsConstructor
public class DiaryContentResponseDto {
    @JsonProperty("diary_content")
    private String diaryContent;
    private AdviceResponseDto advice;
    private String feeling;


}
