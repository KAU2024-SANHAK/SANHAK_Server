package org.kau.kkoolbeeServer.domain.diary.controller;

import org.kau.kkoolbeeServer.domain.diary.request.DiaryContentRequest;
import org.kau.kkoolbeeServer.domain.diary.response.DiaryContentResponse;
import org.kau.kkoolbeeServer.domain.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiaryController {


    private DiaryService diaryService;
    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    public ResponseEntity<?> getDiaryContent(@RequestBody DiaryContentRequest request){
        Long diaryId=request.getDiaryId();

        DiaryContentResponse response=diaryService.getDiaryContent(diaryId);
    }





}
