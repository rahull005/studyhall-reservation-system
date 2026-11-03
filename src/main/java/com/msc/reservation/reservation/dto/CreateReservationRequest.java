package com.msc.reservation.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReservationRequest(

        @NotNull(message = "Slot ID is required")
        Long slotId,

        @NotBlank(message = "Customer name is required")
        @Size(max = 120, message = "Name cannot exceed 120 characters")
        String customerName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 254, message = "Email cannot exceed 254 characters")
        String customerEmail

) {
}
