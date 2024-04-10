package org.kau.kkoolbeeServer.domain.diary.repository;

import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary,Long> {

}
