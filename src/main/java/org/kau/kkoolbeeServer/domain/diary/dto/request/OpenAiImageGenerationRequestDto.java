package org.kau.kkoolbeeServer.domain.diary.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenAiImageGenerationRequestDto {
    private String model;
    private String prompt;
    private int n;

    private String size;

    @Builder
    public OpenAiImageGenerationRequestDto(String model,String prompt, int n, String size) {
        this.model=model;
        this.prompt = prompt;
        this.n = n;
        this.size = size;
    }
}
