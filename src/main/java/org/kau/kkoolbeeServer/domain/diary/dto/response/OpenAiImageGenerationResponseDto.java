package org.kau.kkoolbeeServer.domain.diary.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OpenAiImageGenerationResponseDto {

    private Long created;
    private List<ImageData> data;

    @Getter
    @NoArgsConstructor
    public static class ImageData {
        private String url;
    }
}
