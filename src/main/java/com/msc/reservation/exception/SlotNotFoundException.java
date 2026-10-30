package com.msc.reservation.exception;

public class SlotNotFoundException extends RuntimeException {

    public SlotNotFoundException(Long id) {
        super("Slot not found: " + id);
    }
}