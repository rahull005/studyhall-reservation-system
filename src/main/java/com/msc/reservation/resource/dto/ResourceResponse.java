package com.msc.reservation.resource.dto;

import java.time.Instant;

public record ResourceResponse(
        Long id,
        String name,
        String description,
        boolean active,
        Instant createdAt
) {
}