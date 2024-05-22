package org.kau.kkoolbeeServer.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImageCreateResponseDto {

    private Long diaryId;
    private String image_url;

}
