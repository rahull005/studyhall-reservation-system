package com.msc.reservation.slot.entity;

import com.msc.reservation.resource.entity.ResourceEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "slots")
public class SlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private ResourceEntity resource;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SlotEntity() {
        // Required by JPA.
    }

    public SlotEntity(
            ResourceEntity resource,
            Instant startTime,
            Instant endTime
    ) {
        this.resource = resource;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = SlotStatus.AVAILABLE;
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

    public ResourceEntity getResource() {
        return resource;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }

    public void book() {
        if (!isAvailable()) {
            throw new IllegalStateException(
                    "The slot is not available"
            );
        }

        status = SlotStatus.BOOKED;
    }

    public void release() {
        status = SlotStatus.AVAILABLE;
    }
}
