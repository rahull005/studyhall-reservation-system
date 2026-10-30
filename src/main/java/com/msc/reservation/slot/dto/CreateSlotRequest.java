package com.msc.reservation.slot.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateSlotRequest(

        @NotNull(message = "Resource ID is required")
        Long resourceId,

        @NotNull(message = "Start time is required")
        Instant startTime,

        @NotNull(message = "End time is required")
        Instant endTime
) {
}
