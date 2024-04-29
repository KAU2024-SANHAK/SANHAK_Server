package org.kau.kkoolbeeServer.domain.diary.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SlowTypeCreateResponseDto {

    private Long diaryId;
    private String diaryContent;
    private String diaryTitle;
    private String imageurl;
}
