package org.kau.kkoolbeeServer.domain.diary.repository;

import org.kau.kkoolbeeServer.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary,Long> {




}
