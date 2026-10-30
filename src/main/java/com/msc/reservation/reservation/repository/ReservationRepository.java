package com.msc.reservation.reservation.repository;

import com.msc.reservation.reservation.entity.ReservationEntity;
import com.msc.reservation.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<ReservationEntity, Long> {

    Optional<ReservationEntity> findByIdempotencyKey(
            String idempotencyKey
    );

    Page<ReservationEntity> findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(
            String customerEmail,
            Pageable pageable
    );

    boolean existsBySlot_IdAndStatus(
            Long slotId,
            ReservationStatus status
    );
}
