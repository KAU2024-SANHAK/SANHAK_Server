package org.kau.kkoolbeeServer.domain.diary.response;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.kau.kkoolbeeServer.domain.advice.Advice;
import org.kau.kkoolbeeServer.domain.diary.Feeling;

import java.util.List;

@AllArgsConstructor
public class DiaryContentResponse {

        private String diaryContent;
        private Advice kindAdvice;
        private Advice spicyAdvice;
        private Feeling kindFeeling;
        private Feeling spicyFeeling;


        // 생성자, Getter, Setter 생략

}
