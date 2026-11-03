package com.msc.reservation.reservation.dto;

import com.msc.reservation.reservation.entity.ReservationStatus;

import java.time.Instant;

public record ReservationResponse(

        Long id,
        Long slotId,
        Long resourceId,
        String resourceName,

        Instant startTime,
        Instant endTime,

        String customerName,
        String customerEmail,

        ReservationStatus status,

        Instant createdAt,
        Instant cancelledAt

) {
}
