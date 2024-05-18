package org.kau.kkoolbeeServer.domain.diary.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SlowTypeCreateResponseDto {

    private Long diaryId;
    private String diaryContent;
    private String diaryTitle;
    private String imageUrl;
    private LocalDateTime createdDate;


}
