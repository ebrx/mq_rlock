package com.example.mqrlock.repositroy;

import com.example.mqrlock.entity.TestEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
    @Query("SELECT e FROM TestEntity e where e.status = '04' ORDER BY e.id DESC")
    List<TestEntity> findTop5Entities(Pageable pageable);

    @Modifying
    @Query("UPDATE TestEntity e SET e.status = :newValue WHERE e.id = :entityId")
    void updateStatus(@Param("entityId") Long entityId, @Param("newValue") String newValue);

}
