package org.kau.kkoolbeeServer.domain.diary.service;

import org.kau.kkoolbeeServer.domain.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryService {
    private DiaryRepository diaryRepository;
    @Autowired
    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

}
