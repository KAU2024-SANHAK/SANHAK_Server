package org.kau.kkoolbeeServer.domain.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kau.kkoolbeeServer.domain.advice.Advice;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.kau.kkoolbeeServer.domain.diary.dto.request.CurrentDateRequestDto;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(content().json(objectMapper.writeValueAsString(ApiResponse.success(SuccessType.PROCESS_SUCCESS, responseDto))))
                .andDo(print());

    }

    /*@Test
    public void getDiariesByMonth_ReturnsDiaryList() throws Exception {
        given(diaryService.findDiariesByMonth(LocalDateTime.of(2024, 1, 7, 0, 0))).willReturn(diaryList);
        Diary diary1 = new Diary(1L, "Title1", "Content1", LocalDateTime.of(2024, 1, 7, 0, 0));
        Diary diary2 = new Diary(2L, "Title2", "Content2", LocalDateTime.of(2024, 1, 8, 0, 0));
        Diary diary3 = new Diary(3L, "Title3", "Content3", LocalDateTime.of(2024, 1, 9, 0, 0));

        diaryList = Arrays.asList(diary1, diary2, diary3);
        mockMvc.perform(post("/api/diary/list/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CurrentDateRequestDto(LocalDateTime.of(2024, 1, 7, 0, 0)))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(jsonPath("$.data.monthList[0].diaryId").value(diaryList.get(0).getId()))
                .andExpect(jsonPath("$.data.monthList[1].diaryTitle").value(diaryList.get(1).getTitle()))
                .andExpect(jsonPath("$.data.monthList[2].createdDate").value(diaryList.get(2).getCreatedAt().toString()));
    }*/
}
