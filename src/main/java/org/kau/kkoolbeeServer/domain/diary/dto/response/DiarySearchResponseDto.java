package org.kau.kkoolbeeServer.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class DiarySearchResponseDto {
    private Long diaryId;
    private LocalDateTime createdDate;
    private String diaryTitle;
    private String imageUrl;

    private String diaryContent;

    private String keyWord;
}
