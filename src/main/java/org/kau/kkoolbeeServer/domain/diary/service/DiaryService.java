package org.kau.kkoolbeeServer.domain.diary.service;

import com.amazonaws.services.kms.model.NotFoundException;
import org.kau.kkoolbeeServer.domain.advice.Advice;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.kau.kkoolbeeServer.domain.diary.repository.DiaryRepository;
import org.kau.kkoolbeeServer.domain.diary.response.DiaryContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {
    private DiaryRepository diaryRepository;
    @Autowired
    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public DiaryContentResponse getDiaryContent(Long diaryId) {
        // 일기를 데이터베이스에서 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("해당 일기를 찾을 수 없습니다."));

        // 응답 데이터 구성
        String diaryContent = diary.getContent();
        //List<Feeling> feelings = diary.getSummary(); // 이 부분은 수정해야할 수도 있습니다.


        Advice advice = diary.getAdvice();

        return new DiaryContentResponse(diaryContent, feelings, advice);

}
