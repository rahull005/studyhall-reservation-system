package com.msc.reservation.slot.repository;

import com.msc.reservation.slot.entity.SlotEntity;
import com.msc.reservation.slot.entity.SlotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SlotRepository
        extends JpaRepository<SlotEntity, Long> {

    List<SlotEntity>
    findByResource_IdAndStatusAndStartTimeGreaterThanEqualOrderByStartTimeAsc(
            Long resourceId,
            SlotStatus status,
            Instant from
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SlotEntity s WHERE s.id = :id")
    Optional<SlotEntity> findByIdForUpdate(
            @Param("id") Long id
    );
}
