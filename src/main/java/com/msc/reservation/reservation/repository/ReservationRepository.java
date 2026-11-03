package com.msc.reservation.reservation.repository;

import com.msc.reservation.reservation.entity.ReservationEntity;
import com.msc.reservation.reservation.entity.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<ReservationEntity, Long> {

    Optional<ReservationEntity> findByIdempotencyKey(
            String idempotencyKey
    );

    Page<ReservationEntity>
    findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(
            String customerEmail,
            Pageable pageable
    );

    boolean existsBySlot_IdAndStatus(
            Long slotId,
            ReservationStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM ReservationEntity r
            WHERE r.id = :id
            """)
    Optional<ReservationEntity> findByIdForUpdate(
            @Param("id") Long id
    );
}
