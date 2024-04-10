package org.kau.kkoolbeeServer.domain.diary.controller;

import org.kau.kkoolbeeServer.domain.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiaryController {


    private DiaryService diaryService;
    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

}
