package org.kau.kkoolbeeServer.domain.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;

import java.time.LocalDateTime;

@JsonPropertyOrder({ "diaryId","createdDate","diary_content", "advice","feeling","imageUrl","diaryTitle"})

@Getter
@AllArgsConstructor
public class DiaryContentResponseDto {
    private Long diaryId;
    private LocalDateTime createdDate;
    @JsonProperty("diary_content")
    private String diaryContent;
    private AdviceResponseDto advice;
    private String feeling;
    private String imageUrl;
    private String diaryTitle;


}
