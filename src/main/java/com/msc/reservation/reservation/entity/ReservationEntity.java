package com.msc.reservation.reservation.entity;

import com.msc.reservation.slot.entity.SlotEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private SlotEntity slot;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 254)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    @Column(
            name = "idempotency_key",
            nullable = false,
            unique = true,
            length = 100
    )
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    protected ReservationEntity() {
        // Required by JPA.
    }

    public ReservationEntity(
            SlotEntity slot,
            String customerName,
            String customerEmail,
            String idempotencyKey
    ) {
        this.slot = slot;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.idempotencyKey = idempotencyKey;
        this.status = ReservationStatus.CONFIRMED;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public SlotEntity getSlot() {
        return slot;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (!isConfirmed()) {
            throw new IllegalStateException(
                    "Only confirmed reservations can be cancelled"
            );
        }

        status = ReservationStatus.CANCELLED;
        cancelledAt = Instant.now();
    }
}