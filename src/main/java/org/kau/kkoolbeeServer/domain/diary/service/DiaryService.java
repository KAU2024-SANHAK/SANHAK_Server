package org.kau.kkoolbeeServer.domain.diary.service;

import org.kau.kkoolbeeServer.S3.S3UploaderService;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.kau.kkoolbeeServer.domain.diary.dto.response.UpdateDiaryResponseDto;
import org.kau.kkoolbeeServer.domain.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DiaryService {

    private DiaryRepository diaryRepository;
    private S3UploaderService s3UploaderService;

    @Autowired
    public DiaryService(DiaryRepository diaryRepository,S3UploaderService s3UploaderService) {
        this.diaryRepository = diaryRepository;
        this.s3UploaderService=s3UploaderService;
    }

    public Optional<Diary> findDiaryById(Long diary_id){

        return diaryRepository.findById(diary_id);


    }
   /* public List<Diary> findDiariesByMonth(LocalDateTime date) {
        LocalDateTime startOfMonth = date.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return diaryRepository.findByWritedAtBetween(startOfMonth, endOfMonth);
    }*/

    public List<Diary> findDiariesByMonthAndMemberId(LocalDateTime date, Long memberId) {
        LocalDateTime startOfMonth = date.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return diaryRepository.findByMemberIdAndWritedAtBetween(memberId, startOfMonth, endOfMonth);
    }



   /* public List<Diary> findDiariesByFeeling(String feeling) {
        return diaryRepository.findByFeeling(Feeling.valueOf(feeling));
    }
*/
    public List<Diary> findDiariesByMemberIdAndFeeling(Long memberId,Feeling feeling) {
        return diaryRepository.findByMemberIdAndFeeling(memberId,feeling);
    }
    @Transactional
    public Diary saveDiary(Diary diary){
        return diaryRepository.save(diary);
    }

    public UpdateDiaryResponseDto updateDiary(Long diaryId,String diaryContent,String diaryTitle,String imageUrl){
        Diary diary=findDiaryById(diaryId).orElseThrow(()->new NoSuchElementException("해당 ID의 일기를 찾을 수 없습니다."));
        if(diary.getImageurl()!=null){
            s3UploaderService.deleteFileFromS3(diary.getImageurl());

        }
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


}
