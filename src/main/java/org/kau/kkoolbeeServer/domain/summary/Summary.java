package org.kau.kkoolbeeServer.domain.summary;

import jakarta.persistence.*;
import org.kau.kkoolbeeServer.domain.member.Member;

@Entity
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String first_feeling;

    String second_feeling;

    @ManyToOne // 다대일 관계 설정
    @JoinColumn(name = "member_id") // 외래 키로 사용될 컬럼 지정
    private Member member;





}
