package org.kau.kkoolbeeServer.domain.diary.service;

import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.kau.kkoolbeeServer.domain.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    private DiaryRepository diaryRepository;

    @Autowired
    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public Optional<Diary> findDiaryById(Long diary_id){

        return diaryRepository.findById(diary_id);


    }
    public List<Diary> findDiariesByMonth(LocalDateTime date) {
        LocalDateTime startOfMonth = date.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return diaryRepository.findByWritedAtBetween(startOfMonth, endOfMonth);
    }

    public List<Diary> findDiariesByFeeling(String feeling) {
        return diaryRepository.findByFeeling(Feeling.valueOf(feeling));
    }


}
