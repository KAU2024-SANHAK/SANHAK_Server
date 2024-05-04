package org.kau.kkoolbeeServer.domain.diary.controller;

import jakarta.validation.Valid;
import org.kau.kkoolbeeServer.S3.S3UploaderService;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.dto.request.FeelingListRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.CalenderDiaryResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.request.CurrentDateRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.request.DiaryContentRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.DiaryContentResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.FeelingListResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.SlowTypeCreateResponseDto;
import org.kau.kkoolbeeServer.domain.diary.service.DiaryService;
import org.kau.kkoolbeeServer.domain.member.Member;
import org.kau.kkoolbeeServer.domain.member.service.MemberService;

import org.kau.kkoolbeeServer.global.auth.jwt.JwtProvider;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType;
import org.kau.kkoolbeeServer.global.common.dto.enums.SuccessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class DiaryController {


    private DiaryService diaryService;
    private final S3UploaderService s3UploaderService;
    private MemberService memberService;
    private JwtProvider jwtProvider;
    @Autowired
    public DiaryController(DiaryService diaryService,S3UploaderService s3UploaderService,MemberService memberService,JwtProvider jwtProvider) {
        this.diaryService = diaryService;
        this.s3UploaderService=s3UploaderService;
        this.memberService=memberService;
        this.jwtProvider=jwtProvider;

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


            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseDto));
        } else {
            return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                    .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_ERROR));
            //diary 가 null 일 경우 요청이상함 반환
        }

    }
   /* @PostMapping("/api/diary/list/calendar")
    public ResponseEntity<ApiResponse<?>> getDiariesByMonth(@RequestBody CurrentDateRequestDto requestDto){
        LocalDateTime currentDate = requestDto.getCurrentDate();


        List<Diary>diaries=diaryService.findDiariesByMonth(currentDate);
        if(diaries.isEmpty()){
            return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                    .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_ERROR, "해당 월에 대한 일기가 존재하지 않습니다."));
        }

        List<CalenderDiaryResponseDto> diaryDtos=diaries.stream()
                .map(diary -> new CalenderDiaryResponseDto(diary.getId(), diary.getTitle(), diary.getWritedAt()))
                .collect(Collectors.toList());

        Map<String,List<CalenderDiaryResponseDto>> responseMap= Map.of("monthList",diaryDtos);
        return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseMap));*/



    @PostMapping("/api/diary/list/calendar")
    public ResponseEntity<ApiResponse<?>> getDiariesByMonth(Principal principal,@RequestBody CurrentDateRequestDto requestDto){
        Long memberId= JwtProvider.getUserFromPrincipal(principal);
        LocalDateTime currentDate = requestDto.getCurrentDate();

        List<Diary> diaries = diaryService.findDiariesByMonthAndMemberId(currentDate, memberId);

        if(diaries.isEmpty()){
            return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                    .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_ERROR, "해당 월에 대한 일기가 존재하지 않습니다."));
        }

        List<CalenderDiaryResponseDto> diaryDtos=diaries.stream()
                .map(diary -> new CalenderDiaryResponseDto(diary.getId(), diary.getTitle(), diary.getWritedAt()))
                .collect(Collectors.toList());

        Map<String,List<CalenderDiaryResponseDto>> responseMap= Map.of("monthList",diaryDtos);
        return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseMap));




    }

    /*@PostMapping("/api/diary/list/feeling")
    public ResponseEntity<ApiResponse<?>> getDiariesByFeeling( @RequestBody FeelingListRequestDto requestDto) {


            List<Diary> diaries = diaryService.findDiariesByFeeling(requestDto.getFeeling());

            if (diaries.isEmpty()) {
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_ERROR, "해당 감정에 대한 일기가 존재하지 않습니다."));
            }

            List<FeelingListResponseDto> feelingList = diaries.stream()
                    .map(diary -> new FeelingListResponseDto(diary.getId(), diary.getWritedAt(), diary.getTitle()))
                    .collect(Collectors.toList());

            Map<String, List<FeelingListResponseDto>> responseMap = Map.of("feelingList", feelingList);
            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseMap));

    }*/

    @PostMapping("/api/diary/list/feeling")
    public ResponseEntity<ApiResponse<?>> getDiariesByFeeling( Principal principal,@RequestBody FeelingListRequestDto requestDto) {

        Long memberId=JwtProvider.getUserFromPrincipal(principal);
        List<Diary> diaries = diaryService.findDiariesByMemberIdAndFeeling(memberId,requestDto.getFeeling());

        if (diaries.isEmpty()) {
            return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                    .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_ERROR, "해당 감정에 대한 일기가 존재하지 않습니다."));
        }

        List<FeelingListResponseDto> feelingList = diaries.stream()
                .map(diary -> new FeelingListResponseDto(diary.getId(), diary.getWritedAt(), diary.getTitle()))
                .collect(Collectors.toList());

        Map<String, List<FeelingListResponseDto>> responseMap = Map.of("feelingList", feelingList);
        return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseMap));

    }


   /* @PostMapping("/api/diary/create/slow")
    public ResponseEntity<ApiResponse<?>> createSlowTypeDiary(@RequestPart("imageurl")MultipartFile image,
                                                              @RequestPart("diaryTitle") String diaryTitle,
                                                              @RequestPart("diaryContent") String diaryContent){


        try {
            String accessToken = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
            String imageUrl = s3UploaderService.upload(image);
            Long memberId= jwtProvider.getUserFromJwt(accessToken);
            Member member= memberService.findByIdOrThrow(memberId);
            Diary diary = new Diary();
            diary.setTitle(diaryTitle);
            diary.setMember(member);
            diary.setContent(diaryContent);
            diary.setImageurl(imageUrl);

            Diary savedDiary=diaryService.saveDiary(diary);
            SlowTypeCreateResponseDto responseDto=new SlowTypeCreateResponseDto(diary.getId(),diary.getContent(),diary.getTitle(),diary.getImageurl());

            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,responseDto));
        } catch (Exception e) {
            e.printStackTrace();


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR,e.getMessage()));
        }
    }*/
    @PostMapping("/api/diary/create/slow")
    public ResponseEntity<ApiResponse<?>> createSlowTypeDiary(@RequestHeader(value = "Authorization") String authHeader, @RequestPart(value = "imageurl")MultipartFile image,
                                                              @RequestPart(value = "diaryTitle") String diaryTitle,
                                                              @RequestPart(value = "diaryContent") String diaryContent){

        try {
            String accessToken = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
            String imageUrl = s3UploaderService.upload(image);
            Long memberId= jwtProvider.getUserFromJwt(accessToken);
            Member member= memberService.findByIdOrThrow(memberId);
            Diary diary = new Diary();
            diary.setTitle(diaryTitle);
            diary.setMember(member);
            diary.setContent(diaryContent);
            diary.setImageurl(imageUrl);

            Diary savedDiary=diaryService.saveDiary(diary);
            SlowTypeCreateResponseDto responseDto=new SlowTypeCreateResponseDto(diary.getId(),diary.getContent(),diary.getTitle(),diary.getImageurl());

            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,responseDto));
        } catch (Exception e) {
            e.printStackTrace();


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR,e.getMessage()));
        }
    }




}
