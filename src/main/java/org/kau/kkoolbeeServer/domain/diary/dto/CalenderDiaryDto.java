package org.kau.kkoolbeeServer.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CalenderDiaryDto {
    private Long diaryId;
    private String diaryTitle;
    private LocalDateTime createdDate;


}
