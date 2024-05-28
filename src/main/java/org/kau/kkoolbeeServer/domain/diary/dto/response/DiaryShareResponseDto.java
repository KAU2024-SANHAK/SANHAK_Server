package org.kau.kkoolbeeServer.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Feeling;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryShareResponseDto {
    private Long diaryId;
    private String diaryContent;
    private String diaryTitle;
    private String imageUrl;
    private LocalDateTime createdDate;
    private String userName;
    private AdviceResponseDto advice;
    private String feeling;
}
