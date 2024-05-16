package org.kau.kkoolbeeServer.domain.diary.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryDeleteRequestDto {
    private Long diaryId;
}
