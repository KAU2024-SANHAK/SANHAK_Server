package org.kau.kkoolbeeServer.domain.diary;

import jakarta.persistence.*;
import org.kau.kkoolbeeServer.domain.advice.Advice;
import org.kau.kkoolbeeServer.global.common.domain.BaseTimeEntity;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
public class Diary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime writedAt;

    @Enumerated(EnumType.STRING)
    private Feeling feeling;

    @Column(nullable = false,length = 1000)
    private String content;

    String title;

    @OneToOne
    @JoinColumn(name = "advice_id")
    private Advice advice;

}
