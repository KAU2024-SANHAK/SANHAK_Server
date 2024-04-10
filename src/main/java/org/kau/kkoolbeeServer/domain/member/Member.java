package org.kau.kkoolbeeServer.domain.member;

import jakarta.persistence.*;
import org.kau.kkoolbeeServer.domain.diary.Diary;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String kakaoId;
    @OneToMany(mappedBy = "member")
    private final List<Diary> diaries=new ArrayList<>();

    private String socialNickname;
    private String socialImage;

    @Enumerated(EnumType.STRING)
    private UserDiaryType userDiaryType;



}