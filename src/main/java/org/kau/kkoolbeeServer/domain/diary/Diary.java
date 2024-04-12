package org.kau.kkoolbeeServer.domain.diary;

import jakarta.persistence.*;
import org.kau.kkoolbeeServer.domain.advice.Advice;
import org.kau.kkoolbeeServer.domain.member.Member;
import org.kau.kkoolbeeServer.global.common.domain.BaseTimeEntity;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Diary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime writedAt;   //이거 중복가능성이 좀 보인다.. BaseTimeEntitiy와 ai서버를 위해서 만든 필드

    @Enumerated(EnumType.STRING)
    private Feeling feeling;


    @Column(nullable = false,length = 1000)
    private String content;

    private String title;

    @OneToOne
    @JoinColumn(name = "advice_id")
    private Advice advice;











}
