package com.msc.reservation.reservation.dto;

public record ReservationCreationResult(
        ReservationResponse reservation,
        boolean created
) {
}
