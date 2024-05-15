package org.kau.kkoolbeeServer.domain.diary.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class DiaryUpdateRequestDto {
    private MultipartFile imageUrl;
    private Long diaryId;
    private String diaryTitle;
    private String diaryContent;


}
