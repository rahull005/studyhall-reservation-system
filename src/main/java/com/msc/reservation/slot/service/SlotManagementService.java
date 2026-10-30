package com.msc.reservation.slot.service;

import com.msc.reservation.exception.ResourceNotFoundException;
import com.msc.reservation.exception.SlotNotFoundException;
import com.msc.reservation.resource.entity.ResourceEntity;
import com.msc.reservation.resource.repository.ResourceRepository;
import com.msc.reservation.slot.dto.CreateSlotRequest;
import com.msc.reservation.slot.dto.SlotResponse;
import com.msc.reservation.slot.entity.SlotEntity;
import com.msc.reservation.slot.entity.SlotStatus;
import com.msc.reservation.slot.repository.SlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SlotManagementService {

    private final SlotRepository slotRepository;
    private final ResourceRepository resourceRepository;

    public SlotManagementService(
            SlotRepository slotRepository,
            ResourceRepository resourceRepository
    ) {
        this.slotRepository = slotRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public SlotResponse create(CreateSlotRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time."
            );
        }

        ResourceEntity resource = resourceRepository
                .findById(request.resourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                request.resourceId()
                        )
                );

        if (!resource.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create slots for an inactive resource."
            );
        }

        SlotEntity slot = new SlotEntity(
                resource,
                request.startTime(),
                request.endTime()
        );

        return toResponse(slotRepository.saveAndFlush(slot));
    }

    @Transactional(readOnly = true)
    public SlotResponse findById(Long id) {
        SlotEntity slot = slotRepository.findById(id)
                .orElseThrow(() -> new SlotNotFoundException(id));

        return toResponse(slot);
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> findAvailable(
            Long resourceId,
            Instant from
    ) {
        ResourceEntity resource = resourceRepository
                .findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(resourceId)
                );

        if (!resource.isActive()) {
            return List.of();
        }

        return slotRepository
                .findByResource_IdAndStatusAndStartTimeGreaterThanEqualOrderByStartTimeAsc(
                        resourceId,
                        SlotStatus.AVAILABLE,
                        from
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SlotResponse toResponse(SlotEntity slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getResource().getId(),
                slot.getResource().getName(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus(),
                slot.getVersion()
        );
    }
}