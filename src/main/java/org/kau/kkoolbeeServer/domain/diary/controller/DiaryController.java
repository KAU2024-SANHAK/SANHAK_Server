package org.kau.kkoolbeeServer.domain.diary.controller;

import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.dto.request.DiaryContentRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.DiaryContentResponseDto;
import org.kau.kkoolbeeServer.domain.diary.service.DiaryService;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType;
import org.kau.kkoolbeeServer.global.common.dto.enums.SuccessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class DiaryController {

    private DiaryService diaryService;
    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping("/api/diary/content")
    public ResponseEntity<ApiResponse<?>> getDiaryContents(@RequestBody DiaryContentRequestDto diaryContentRequestDto) {

        Long diaryId = diaryContentRequestDto.getDiaryId();


        try{
            Optional<Diary> diaryOptional = diaryService.findDiaryById(diaryId);
            if (diaryOptional.isPresent()) {

                Diary diary = diaryOptional.get();
                // Diary의 Advice 정보를 AdviceResponseDto 객체로 변환
                AdviceResponseDto adviceResponseDto = new AdviceResponseDto(
                        diary.getAdvice().getSpicy_advice(),    //여기서 null이 나오면 ?
                        diary.getAdvice().getKind_advice()
                );


                DiaryContentResponseDto responseDto = new DiaryContentResponseDto(
                        diary.getContent(),
                        adviceResponseDto,
                        diary.getFeeling().toString(),
                        diary.getImageurl(),
                        diary.getTitle()
                );

                return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESS, responseDto));
            } else {
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_EXCEPTION.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_EXCEPTION));
                //diary 가 null 일 경우 요청이상함 반환
            }

        } catch (Exception e){

            return ResponseEntity.status(ErrorType.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR));
            //다른 오류가 났을 경우 서버에러

        }

    }


}