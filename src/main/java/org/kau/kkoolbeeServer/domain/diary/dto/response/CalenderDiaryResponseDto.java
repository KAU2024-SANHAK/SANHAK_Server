package org.kau.kkoolbeeServer.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CalenderDiaryResponseDto {
    private Long diaryId;
    private String diaryTitle;
    private LocalDateTime createdDate;
    private String imageUrl;


}
