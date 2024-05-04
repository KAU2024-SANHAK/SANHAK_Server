package org.kau.kkoolbeeServer.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.summary.Summary;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member")
    private final List<Diary> diaries=new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private UserDiaryType userDiaryType;

    @OneToMany(mappedBy = "member") // Summary에 대한 참조 추가
    private List<Summary> summaries = new ArrayList<>();

    /**
     * 소셜 로그인 관련
     */
    @Column(nullable = false)
    private String kakaoId;

    private String socialNickname;

    private String socialImage;

    // 로그인 새롭게 할 때마다 해당 필드들 업데이트
    public void updateSocialInfo(String socialNickname, String socialImage) {
        this.socialNickname = socialNickname;
        this.socialImage = socialImage;
    }

    @Builder
    public Member(String kakaoId) {
        this.kakaoId = kakaoId;
    }

    public static Member of(String kakaoId) {
        return new Member(kakaoId);
    }


    public void setDiaryType(UserDiaryType userDiaryType) {
        this.userDiaryType = userDiaryType;
    }
}
