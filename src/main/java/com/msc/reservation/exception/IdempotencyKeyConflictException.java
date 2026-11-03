package com.msc.reservation.exception;

public class IdempotencyKeyConflictException
        extends RuntimeException {

    public IdempotencyKeyConflictException() {
        super("The idempotency key has already been used for a different request.");
    }
}
