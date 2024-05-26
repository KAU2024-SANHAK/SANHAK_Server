package org.kau.kkoolbeeServer.domain.diary.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import jakarta.validation.Valid;
import org.kau.kkoolbeeServer.S3.S3UploaderService;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.kau.kkoolbeeServer.domain.diary.dto.request.*;
import org.kau.kkoolbeeServer.domain.diary.dto.response.*;
import org.kau.kkoolbeeServer.domain.diary.service.DiaryService;
import org.kau.kkoolbeeServer.domain.member.Member;
import org.kau.kkoolbeeServer.domain.member.service.MemberService;

import org.kau.kkoolbeeServer.global.auth.jwt.JwtProvider;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType;
import org.kau.kkoolbeeServer.global.common.dto.enums.SuccessType;
import org.kau.kkoolbeeServer.global.common.exception.model.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
public class DiaryController {
    private static final Logger logger = LoggerFactory.getLogger(DiaryController.class);


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
    public ResponseEntity<ApiResponse<?>> getDiaryContents(@RequestHeader(value = "Authorization") String authHeader,@RequestBody DiaryContentRequestDto diaryContentRequestDto) {

            try{
                Long memberId=extractMemberIdFromRequestHeader(authHeader);

            }
            catch(IllegalArgumentException e){
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.INVALID_HTTP_REQUEST_ERROR));

            }
            catch(RuntimeException e){
                e.printStackTrace();
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.INVALID_HTTP_REQUEST_ERROR));
            }
            Long diaryId = diaryContentRequestDto.getDiaryId();

            Optional<Diary> diaryOptional = diaryService.findDiaryById(diaryId);


            if (diaryOptional.isPresent()) {


                Diary diary = diaryOptional.get();
                AdviceResponseDto adviceResponseDto=AdviceResponseDto.fromAdviceOrNull(diary.getAdvice());
                //feeling이 null인경우 대비
                String feeling = diary.getFeeling() != null ? diary.getFeeling().toString() : null;
                DiaryContentResponseDto responseDto = new DiaryContentResponseDto(
                        diary.getId(),
                        diary.getWritedAt(),
                        diary.getContent(),
                        adviceResponseDto,
                        feeling,
                        diary.getImageurl(),
                        diary.getTitle()
                );

                return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseDto));
            } else {
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.NO_DIARY));
                //diary 가 null 일 경우 요청이상함 반환
            }

    }

    @PostMapping("/api/diary/list/calendar")
    public ResponseEntity<ApiResponse<?>> getDiariesByMonth(@RequestHeader("Authorization") String authHeader,@RequestBody CurrentDateRequestDto requestDto){


        Long memberId= extractMemberIdFromRequestHeader(authHeader);

        LocalDateTime currentDate = requestDto.getCurrentDate();

        List<Diary> diaries = diaryService.findDiariesByMonthAndMemberId(currentDate, memberId);

        if(diaries.isEmpty()){
                return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                        .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_ERROR, "해당 월에 대한 일기가 존재하지 않습니다."));
            }

            List<CalenderDiaryResponseDto> diaryDtos=diaries.stream()
                    .map(diary -> new CalenderDiaryResponseDto(diary.getId(), diary.getTitle(), diary.getWritedAt(), diary.getImageurl()))
                    .collect(Collectors.toList());

            Map<String,List<CalenderDiaryResponseDto>> responseMap= Map.of("monthList",diaryDtos);
            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseMap));


    }

    @PostMapping("/api/diary/list/feeling")
    public ResponseEntity<ApiResponse<?>> getDiariesByFeeling( @RequestHeader(value = "Authorization") String authHeader,@RequestBody FeelingListRequestDto requestDto) {



        Long memberId= extractMemberIdFromRequestHeader(authHeader);
        Feeling feeling=Feeling.valueOf(requestDto.getFeeling());
        List<Diary> diaries = diaryService.findDiariesByMemberIdAndFeeling(memberId,feeling);

        if (diaries.isEmpty()) {
            return ResponseEntity.status(ErrorType.REQUEST_VALIDATION_ERROR.getHttpStatus())
                    .body(ApiResponse.error(ErrorType.REQUEST_VALIDATION_ERROR, "해당 감정에 대한 일기가 존재하지 않습니다."));
        }

        List<FeelingListResponseDto> feelingList = diaries.stream()
                .map(diary -> new FeelingListResponseDto(diary.getId(), diary.getWritedAt(), diary.getTitle(), diary.getImageurl()))
                .collect(Collectors.toList());

        Map<String, List<FeelingListResponseDto>> responseMap = Map.of("feelingList", feelingList);
        return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED, responseMap));

    }

   /* @PostMapping("/api/diary/create/slow")
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
            SlowTypeCreateResponseDto responseDto=new SlowTypeCreateResponseDto(diary.getId(),diary.getContent(),diary.getTitle(),diary.getImageurl());

            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR,"서버 내부 오류"));
        }
    }*/
    @PostMapping("/api/diary/create/slow")
            public ResponseEntity<ApiResponse<?>> createSlowTypeDiary(@RequestHeader(value = "Authorization") String authHeader, @RequestPart(value = "imageUrl",required = false)MultipartFile image,
                    @RequestPart(value = "diaryTitle") String diaryTitle,
                    @RequestPart(value = "diaryContent") String diaryContent){

                try {
                    Long memberId=extractMemberIdFromRequestHeader(authHeader);

                    String imageUrl=null;
                    if(image!=null && !image.isEmpty() ){
                        imageUrl = s3UploaderService.upload(image);

                    }
                    Member member= memberService.findByIdOrThrow(memberId);
                    Diary diary = new Diary();
                    diary.setTitle(diaryTitle);
                    diary.setMember(member);
                    diary.setContent(diaryContent);
                    ZonedDateTime kstNow = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
                    LocalDateTime now = kstNow.toLocalDateTime();
                    diary.setWritedAt(now);
                    diary.setImageurl(imageUrl);


            Diary savedDiary=diaryService.saveDiary(diary);
            SlowTypeCreateResponseDto responseDto=new SlowTypeCreateResponseDto(diary.getId(),diary.getContent(),diary.getTitle(),diary.getImageurl(),diary.getWritedAt());

            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,responseDto));
        }catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorType.INVALID_HTTP_REQUEST_ERROR));
                }
                catch(Exception e){
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR));
                }
    }

@PatchMapping("/api/diary/update")
public ResponseEntity<?> updateDiary(
        @RequestHeader(value = "Authorization") String authHeader,
        @ModelAttribute DiaryUpdateRequestDto requestDto ){
        Long diaryId= requestDto.getDiaryId();
        MultipartFile imageFile= requestDto.getImageUrl();
        String diaryTitle= requestDto.getDiaryTitle();
        String diaryContent= requestDto.getDiaryContent();

    try{

        String accessToken = null;
        Long memberId=extractMemberIdFromRequestHeader(authHeader);
        accessToken = authHeader.substring(7);

        Diary diary=diaryService.findDiaryById(requestDto.getDiaryId()).orElseThrow(()->new NoSuchElementException("해당 ID의 일기를 찾을 수 없습니다."));
        if(diary.getMember().getId()!=memberId){

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorType.NOT_YOUR_DIARY));
        }


        String imageUrl=null;
        if (imageFile!=null && !imageFile.isEmpty()){

            imageUrl=s3UploaderService.upload(imageFile);

            UpdateDiaryResponseDto responseDto=diaryService.updateDiary(diaryId,diaryContent,diaryTitle,imageUrl);
            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,responseDto));

        }
        else{
            UpdateDiaryResponseDto responseDto=diaryService.updateDiaryWithoutImage(diaryId,diaryContent,diaryTitle);
            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,responseDto));


        }

    }
    catch (IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorType.INVALID_HTTP_REQUEST_ERROR));
    }
    catch (Exception e){
        logger.error("An error occurred while updating diary", e);
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR,e.getMessage()));
    }


}

@DeleteMapping("api/diary/delete")
    public ResponseEntity<?>deleteDiary(@RequestHeader(value = "Authorization") String authHeader,@RequestBody String rawBody){
    ObjectMapper objectMapper = new ObjectMapper();
    try {
        JsonNode rootNode = objectMapper.readTree(rawBody);
        JsonNode diaryIdNode = rootNode.get("diaryId");
        if (diaryIdNode == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
        }
        Long diaryId = diaryIdNode.asLong();
        Long memberId = extractMemberIdFromRequestHeader(authHeader);
        Diary diary = diaryService.findDiaryById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 일기를 찾을 수 없습니다."));
        if (!diary.getMember().getId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorType.NOT_YOUR_DIARY));
        }

        diaryService.deleteDiary(diaryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED));

    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorType.INVALID_HTTP_REQUEST_ERROR));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing request by");
    }
}

    @PostMapping("/api/diary/image")
    public ResponseEntity<?> imageCreate(@RequestHeader("Authorization") String authHeader,@RequestBody ImageCreateRequestDto imageCreateRequestDto){
        try{
            Long memberId=extractMemberIdFromRequestHeader(authHeader);
            ImageCreateResponseDto responseDto=diaryService.generateImageFromDiary(imageCreateRequestDto.getDiaryId());

            return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,responseDto));
    }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorType.INVALID_HTTP_REQUEST_ERROR));
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/diary/share")
    public ResponseEntity<?> diaryShare(@RequestParam("diaryId") Long diaryId){


        return ResponseEntity.ok().body(ApiResponse.success(SuccessType.PROCESS_SUCCESSED,diaryService.diaryShare(diaryId)));


    }

    public Long extractMemberIdFromRequestHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            return jwtProvider.getUserFromJwt(accessToken);
        } else {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
    }




}