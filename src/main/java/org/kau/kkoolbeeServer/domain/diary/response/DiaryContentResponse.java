package org.kau.kkoolbeeServer.domain.diary.response;

import org.kau.kkoolbeeServer.domain.advice.Advice;
import org.kau.kkoolbeeServer.domain.diary.Feeling;

import java.util.List;

public class DiaryContentResponse {
    public class DiaryResponse {
        private String diaryContent;
        private Advice advice;
        private List<Feeling> feelings;

        // 생성자, Getter, Setter 생략
    }
}
