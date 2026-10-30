package com.msc.reservation.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResourceRequest(

        @NotBlank(message = "Resource name is required")
        @Size(max = 120, message = "Name cannot exceed 120 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {
}
