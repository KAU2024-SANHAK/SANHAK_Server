package org.kau.kkoolbeeServer.domain.diary.controller;

import jakarta.validation.Valid;
/*import org.kau.kkoolbeeServer.S3.S3UploaderService;*/
import org.kau.kkoolbeeServer.S3.S3UploaderService;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.dto.request.FeelingListRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.CalenderDiaryResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.request.CurrentDateRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.request.DiaryContentRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.DiaryContentResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.FeelingListResponseDto;
import org.kau.kkoolbeeServer.domain.diary.service.DiaryService;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType;
import org.kau.kkoolbeeServer.global.common.dto.enums.SuccessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class DiaryController {


    private DiaryService diaryService;
    private final S3UploaderService s3UploaderService;
    @Autowired
    public DiaryController(DiaryService diaryService,S3UploaderService s3UploaderService) {
        this.diaryService = diaryService;
        this.s3UploaderService=s3UploaderService;

    }

    @PostMapping("/api/diary/content")
    public ResponseEntity<ApiResponse<?>> getDiaryContents(@RequestBody DiaryContentRequestDto diaryContentRequestDto) {

           Long diaryId = diaryContentRequestDto.getDiaryId();

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

    }
    @PostMapping("/api/diary/list/calendar")
    public ResponseEntity<ApiResponse<?>> getDiariesByMonth(@RequestBody CurrentDateRequestDto requestDto){
        LocalDateTime currentDate = requestDto.getCurrentDate();

            List<Diary>diaries=diaryService.findDiariesByMonth(currentDate);
            if(diaries.isEmpty()){
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_EXCEPTION.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_EXCEPTION, "해당 월에 대한 일기가 존재하지 않습니다."));
            }

            List<CalenderDiaryResponseDto> diaryDtos=diaries.stream()
                    .map(diary -> new CalenderDiaryResponseDto(diary.getId(), diary.getTitle(), diary.getWritedAt()))
                    .collect(Collectors.toList());

            Map<String,List<CalenderDiaryResponseDto>> responseMap= Map.of("monthList",diaryDtos);
            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESS, responseMap));




    }

    @PostMapping("/api/diary/list/feeling")
    public ResponseEntity<ApiResponse<?>> getDiariesByFeeling(@RequestBody FeelingListRequestDto requestDto) {


            List<Diary> diaries = diaryService.findDiariesByFeeling(requestDto.getFeeling());

            if (diaries.isEmpty()) {
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_EXCEPTION.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_EXCEPTION, "해당 감정에 대한 일기가 존재하지 않습니다."));
            }

            List<FeelingListResponseDto> feelingList = diaries.stream()
                    .map(diary -> new FeelingListResponseDto(diary.getId(), diary.getWritedAt(), diary.getTitle()))
                    .collect(Collectors.toList());

            Map<String, List<FeelingListResponseDto>> responseMap = Map.of("feelingList", feelingList);
            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESS, responseMap));

    }

    @PostMapping("/api/diary/create/slow")
    public ResponseEntity<ApiResponse<?>> createSlowTypeDiary(@RequestPart("imageurl")MultipartFile image,
                                                              @RequestPart("diaryTitle") String diaryTitle,
                                                              @RequestPart("diaryContent") String diaryContent){

        try {
            String imageUrl = s3UploaderService.upload(image);
            Diary diary = new Diary();
            diary.setTitle(diaryTitle);
            diary.setContent(diaryContent);
            diary.setImageurl(imageUrl);

            Diary savedDiary=diaryService.saveDiary(diary);

            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESS,diary.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR,"서버 내부 오류"));
        }
    }




}