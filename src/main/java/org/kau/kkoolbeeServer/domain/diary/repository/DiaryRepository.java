package org.kau.kkoolbeeServer.domain.diary.repository;

import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.kau.kkoolbeeServer.domain.diary.Feeling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary,Long> {

    /*List<Diary> findByWritedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);*/
    List<Diary> findByMemberIdAndWritedAtBetween(Long memberId, LocalDateTime startDate, LocalDateTime endDate);

    /*List<Diary> findByFeeling(Feeling feeling);*/

    List<Diary> findByMemberIdAndFeeling(Long memberId,Feeling feeling);

    List<Diary> findByMemberIdAndTitleContainingOrContentContaining(Long memberId, String titleKeyword, String contentKeyword);

}
