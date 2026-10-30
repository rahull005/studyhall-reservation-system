package com.msc.reservation.slot.dto;

import com.msc.reservation.slot.entity.SlotStatus;

import java.time.Instant;

public record SlotResponse(
        Long id,
        Long resourceId,
        String resourceName,
        Instant startTime,
        Instant endTime,
        SlotStatus status,
        Long version
) {
}