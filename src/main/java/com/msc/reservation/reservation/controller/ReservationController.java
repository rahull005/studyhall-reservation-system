package com.msc.reservation.reservation.controller;


import com.msc.reservation.reservation.dto.CreateReservationRequest;
import com.msc.reservation.reservation.dto.ReservationCreationResult;
import com.msc.reservation.reservation.dto.ReservationResponse;
import com.msc.reservation.reservation.repository.ReservationRepository;
import com.msc.reservation.reservation.service.ReservationManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationManagementService reservationService;
    public ReservationController(ReservationManagementService reservationService) {
        this.reservationService = reservationService;
    }

    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey
            ){
        ReservationCreationResult result = reservationService
                .create(request, idempotencyKey);

        if(!result.created()){
            return ResponseEntity.ok(result.reservation());
        }

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.reservation().id())
                .toUri();
    }
}
