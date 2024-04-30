package org.kau.kkoolbeeServer.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FeelingListResponseDto {
    private Long diaryId;
    private LocalDateTime createdDate;
    private String diaryTitle;

}
