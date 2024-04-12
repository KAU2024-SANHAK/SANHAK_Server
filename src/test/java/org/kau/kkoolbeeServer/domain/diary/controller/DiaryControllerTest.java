package org.kau.kkoolbeeServer.domain.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kau.kkoolbeeServer.domain.advice.Advice;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.kau.kkoolbeeServer.domain.diary.dto.request.DiaryContentRequestDto;

import org.kau.kkoolbeeServer.domain.diary.dto.response.DiaryContentResponseDto;
import org.kau.kkoolbeeServer.domain.diary.service.DiaryService;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.dto.enums.SuccessType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DiaryController.class)
public class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiaryService diaryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetDiaryContents() throws Exception {
        // Given
        DiaryContentRequestDto requestDto = new DiaryContentRequestDto();
        requestDto.setDiary_id(1L);

        Advice advice = new Advice(); // Advice 객체 생성
        advice.setSpicy_advice("spicy_advice");
        advice.setKind_advice("kind_advice");
        Diary diary = new Diary(); // Diary 객체 생성
        diary.setContent("안뇽안뇽안뇽안뇽");
        diary.setFeeling(Feeling.ANGRY);
        diary.setAdvice(advice); // Diary 객체에 Advice 설정


        AdviceResponseDto adviceResponseDto = new AdviceResponseDto("spicy_advice", "kind_advice");
        DiaryContentResponseDto responseDto = new DiaryContentResponseDto(diary.getContent(), adviceResponseDto,diary.getFeeling().toString());
        given(diaryService.findDiaryById(any(Long.class))).willReturn(Optional.of(diary));

        // When & Then
        mockMvc.perform(post("/api/diary/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ApiResponse.success(SuccessType.PROCESS_SUCCESS, responseDto))));
    }
}
