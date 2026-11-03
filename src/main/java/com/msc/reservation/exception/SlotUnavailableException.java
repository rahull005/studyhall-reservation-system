package com.msc.reservation.exception;

public class SlotUnavailableException extends RuntimeException {

    public SlotUnavailableException(Long slotId) {
        super("The requested slot is not available: " + slotId);
    }
}