package org.kau.kkoolbeeServer.domain.diary.service;

import org.kau.kkoolbeeServer.S3.S3UploaderService;
import org.kau.kkoolbeeServer.domain.advice.dto.AdviceResponseDto;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.kau.kkoolbeeServer.domain.diary.dto.request.OpenAiImageGenerationRequestDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.DiaryShareResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.ImageCreateResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.OpenAiImageGenerationResponseDto;
import org.kau.kkoolbeeServer.domain.diary.dto.response.UpdateDiaryResponseDto;
import org.kau.kkoolbeeServer.domain.diary.repository.DiaryRepository;
import org.kau.kkoolbeeServer.global.config.OpenAiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DiaryService {
    @Value("${openai.api.key}")
    private String apiKey;
    private RestTemplate restTemplate;

    private DiaryRepository diaryRepository;
    private S3UploaderService s3UploaderService;

    @Autowired
    public DiaryService(RestTemplate restTemplate, DiaryRepository diaryRepository,S3UploaderService s3UploaderService) {
        this.restTemplate=restTemplate;
        this.diaryRepository = diaryRepository;
        this.s3UploaderService=s3UploaderService;

    }

    public Optional<Diary> findDiaryById(Long diary_id){

        return diaryRepository.findById(diary_id);


    }
    public List<Diary> findDiariesByMonthAndMemberId(LocalDateTime date, Long memberId) {
        LocalDateTime startOfMonth = date.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return diaryRepository.findByMemberIdAndWritedAtBetween(memberId, startOfMonth, endOfMonth);
    }




    public List<Diary> findDiariesByMemberIdAndFeeling(Long memberId,Feeling feeling) {
        return diaryRepository.findByMemberIdAndFeeling(memberId,feeling);
    }
    @Transactional
    public Diary saveDiary(Diary diary){
        return diaryRepository.save(diary);
    }

    public UpdateDiaryResponseDto updateDiary(Long diaryId,String diaryContent,String diaryTitle,String imageUrl){
        Diary diary=findDiaryById(diaryId).orElseThrow(()->new NoSuchElementException("해당 ID의 일기를 찾을 수 없습니다."));
       /* if(diary.getImageurl()!=null){
            s3UploaderService.deleteFileFromS3(diary.getImageurl());

        }*/

        diary.setImageurl(imageUrl);
        if(!diary.getContent().equals(diaryContent)){
            diary.setFeeling(null);
            diary.setAdvice(null);
            diary.setContent(diaryContent);
        }
        if(diary.getTitle()!=diaryTitle){
            diary.setTitle(diaryTitle);
        }
        ZonedDateTime kstNow = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime now = kstNow.toLocalDateTime();
        diary.setWritedAt(now);


        Diary savedDiary=diaryRepository.save(diary);
        return new UpdateDiaryResponseDto(diaryId,savedDiary.getContent(),savedDiary.getTitle(),
                savedDiary.getImageurl());




    }

    public UpdateDiaryResponseDto updateDiaryWithoutImage(Long diaryId,String diaryContent,String diaryTitle){
        Diary diary=findDiaryById(diaryId).orElseThrow(()->new NoSuchElementException("해당 ID의 일기를 찾을 수 없습니다."));
        if(!diary.getContent().equals(diaryContent)){
            diary.setFeeling(null);
            diary.setAdvice(null);
            diary.setContent(diaryContent);
        }
        if(diary.getTitle()!=diaryTitle){
            diary.setTitle(diaryTitle);
        }

        ZonedDateTime kstNow = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime now = kstNow.toLocalDateTime();
        diary.setWritedAt(now);
        Diary savedDiary=diaryRepository.save(diary);
        return new UpdateDiaryResponseDto(diaryId,savedDiary.getContent(),savedDiary.getTitle(),
                savedDiary.getImageurl());




    }

    public void deleteDiary(Long diaryId){
        Diary diary=findDiaryById(diaryId).orElseThrow(()->new NoSuchElementException());
        diaryRepository.delete(diary);



    }
    public DiaryShareResponseDto diaryShare(Long diaryId){
        Diary findDiary=diaryRepository.findById(diaryId).orElseThrow(()->new NoSuchElementException());
        String feeling = findDiary.getFeeling() != null ? findDiary.getFeeling().toString() : null;
        AdviceResponseDto adviceResponseDto=AdviceResponseDto.fromAdviceOrNull(findDiary.getAdvice());

        DiaryShareResponseDto responseDto=new DiaryShareResponseDto(findDiary.getId(), findDiary.getContent(),
                findDiary.getTitle(), findDiary.getImageurl(), findDiary.getWritedAt(),findDiary.getMember().getSocialNickname(),
                adviceResponseDto,feeling);
        return responseDto;
    }

    public ImageCreateResponseDto generateImageFromDiary(Long diaryId) throws IOException, NoSuchAlgorithmException{
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(OpenAiConfig.MEDIA_TYPE));
        httpHeaders.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + apiKey);
        Diary diary=findDiaryById(diaryId).orElseThrow(()->new NoSuchElementException("해당 ID의 일기를 찾을 수 없습니다."));
        String formattedPrompt=formatPromptForImageGeneration(diary.getContent());

        OpenAiImageGenerationRequestDto requestDto=
                OpenAiImageGenerationRequestDto.builder()
                        .model(OpenAiConfig.MODEL)
                        .prompt(formattedPrompt)
                        .n(OpenAiConfig.IMAGE_COUNT)
                        .size(OpenAiConfig.IMAGE_SIZE)
                        .build();

        HttpEntity<OpenAiImageGenerationRequestDto> requestDtoHttpEntity=new HttpEntity<>(requestDto,httpHeaders);

        ResponseEntity<OpenAiImageGenerationResponseDto> responseEntity =
                restTemplate.postForEntity(OpenAiConfig.IMAGE_URL,
                        requestDtoHttpEntity,
                        OpenAiImageGenerationResponseDto.class);

        OpenAiImageGenerationResponseDto responseBody = responseEntity.getBody();
        if (responseBody != null && !responseBody.getData().isEmpty()) {
            String imageUrl = responseBody.getData().get(0).getUrl();
            File downloadedFile=downloadFileFromUrl(imageUrl);
            String s3Url = s3UploaderService.uploadFile(downloadedFile);
            diary.setImageurl(s3Url);
            diaryRepository.save(diary);
            downloadedFile.delete();
            return new ImageCreateResponseDto(diary.getId(), diary.getImageurl());


        } else {
            throw new RuntimeException("이미지 생성에 실패했습니다.");
        }


    }

    private File downloadFileFromUrl(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        InputStream inputStream = url.openStream();
        File tempFile = File.createTempFile("image-", ".png");

        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
        }
        return tempFile;
    }

    private String formatPromptForImageGeneration(String diaryContent) {

        String formattedPrompt = String.format("""
유저의 diary 내용 : %s

유저의 diary 내용을 요약해주세요. 아래는 예시입니다.
예시 
    {   내용 : 2024년 4월 1일, 친구들과 함께 제주도의 한라산을 등반했다.
        한라산은 한국에서 가장 높은 산으로, 그 정상에서 바라보는 제주도의 풍경은 정말로 인상적이였다.
        제주도의 파란 바다와 울창한 숲이 한눈에 들어왔고, 그 아름다움에 모두가 말을 잃었다.
        우리는 등반하면서 서로를 의지하며 많은 얘기를 나누었고, 함께 등반한 친구들과 나는 더욱 가까워진 것을 느꼈다.
        우리는 서로에 대해 더 많이 알게 되었고, 이 여행이 우리 사이의 우정을 더욱 깊게 만들었다는 것을 깨달았다. 
        한라산 등반은 단순한 여행 이상의 의미가 있었다.
        이 경험은 나에게 자연의 아름다움을 다시 한번 일깨워 주었고, 친구들과의 관계를 더욱 소중히 여기게 만들었다.
        이번 등반을 통해 얻은 추억과 교훈은 앞으로도 오랫동안 내 마음속에 남아있을 것이다.      
        요약 : 친구들과 함께 제주도의 한라산을 등반했다. 한라산 정상에서의 풍경은 인상적이었고, 함께한 친구들과의 우정도 더 깊어졌다.
    }    
다음 요약된 내용을 바탕으로 다음과 같은 이미지를 생성해주세요.
이미지는 평면적인 2D 일러스트 스타일로 해주세요.
캐릭터는 꿀벌 옷을 입고있는 곰을 모티브로 한 일러스트로, 사용자의 diary 내용에 맞는 포즈와 표정을 취하도록 해주세요.
다양한 요소들을 추가하기보단 불필요한 것들을 빼고 단조로운 스타일로 생성해주세요
""", diaryContent);

        // 여기에 프롬프트를 포맷팅하는 로직을 구현합니다.
        // 예를 들어, 특정 키워드를 추가하거나, 내용을 요약하는 등의 작업을 할 수 있습니다.
        return formattedPrompt; // 일단은 단순히 일기 내용을 그대로 반환하도록 합니다. 실제 구현시에는 수정해야 합니다.
    }


    public List<Diary> searchDiaries(Long memberId, String searchKeyword) {

        return diaryRepository.searchDiaries(memberId, searchKeyword, searchKeyword);
    }






}
