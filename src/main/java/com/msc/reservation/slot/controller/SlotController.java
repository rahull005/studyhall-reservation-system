package com.msc.reservation.slot.controller;

import com.msc.reservation.slot.dto.CreateSlotRequest;
import com.msc.reservation.slot.dto.SlotResponse;
import com.msc.reservation.slot.service.SlotManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/slots")
public class SlotController {

    private final SlotManagementService slotService;

    public SlotController(SlotManagementService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotResponse create(
            @Valid @RequestBody CreateSlotRequest request
    ) {
        return slotService.create(request);
    }

    @GetMapping("/{id}")
    public SlotResponse findById(@PathVariable Long id) {
        return slotService.findById(id);
    }

    @GetMapping("/available")
    public List<SlotResponse> findAvailable(
            @RequestParam Long resourceId,
            @RequestParam Instant from
    ) {
        return slotService.findAvailable(resourceId, from);
    }
}