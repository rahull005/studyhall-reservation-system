package com.msc.reservation.reservation.service;

import com.msc.reservation.exception.IdempotencyKeyConflictException;
import com.msc.reservation.exception.ReservationNotFoundException;
import com.msc.reservation.exception.SlotNotFoundException;
import com.msc.reservation.exception.SlotUnavailableException;
import com.msc.reservation.reservation.dto.CreateReservationRequest;
import com.msc.reservation.reservation.dto.ReservationCreationResult;
import com.msc.reservation.reservation.dto.ReservationResponse;
import com.msc.reservation.reservation.entity.ReservationEntity;
import com.msc.reservation.reservation.repository.ReservationRepository;
import com.msc.reservation.slot.entity.SlotEntity;
import com.msc.reservation.slot.entity.SlotStatus;
import com.msc.reservation.slot.repository.SlotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ReservationManagementService {
    private final ReservationRepository reservationRepository;
    private final SlotRepository slotRepository;

    public ReservationManagementService(
            ReservationRepository reservationRepository,
            SlotRepository slotRepository
    ){
        this.reservationRepository = reservationRepository;
        this.slotRepository = slotRepository;
    }

    public ReservationCreationResult create(CreateReservationRequest request,String rawIdempotencyKey){
        String key = validateIdempotencyKey(rawIdempotencyKey);

        String customerName = request.customerName().trim();
        String customerEmail = request.customerEmail().trim().toLowerCase();

        // Check whether this request was already processed.
        var existing = reservationRepository.findByIdempotencyKey(key);
        if(existing.isPresent()){
            return replyIfSame(
                    existing.get(),
                    request.slotId(),
                    request.customerName(),
                    request.customerEmail()
            );
        }

        // Lock the slot row until this transaction completes.
        SlotEntity slot = slotRepository
                .findByIdForUpdate(request.slotId())
                .orElseThrow(() -> new SlotNotFoundException(request.slotId()));



        /*
         * Check the key again after acquiring the slot lock.
         * This handles simultaneous retries for the same slot.
         */
        existing = reservationRepository.findByIdempotencyKey(key);
        if(existing.isPresent()){
            return replyIfSame(
                    existing.get(),
                    request.slotId(),
                    customerName,
                    customerEmail
            );
        }

        if(!slot.isAvailable()){
            throw new SlotUnavailableException(slot.getId());
        }

        // Update the slot.
        slot.book();

        ReservationEntity reservation = new ReservationEntity(
                slot,
                customerName,
                customerEmail,
                key
        );

        ReservationEntity saved =
                reservationRepository.saveAndFlush(reservation);

        return new ReservationCreationResult(
                toResponse(saved),
                true
        );

    }


    //utilities
    private String validateIdempotencyKey(String key) {
        if(key == null || key.isBlank() || key.length() > 100){
            throw new IllegalArgumentException( "A valid Idempotency-Key header is required.");
        }

        return key.trim();
    }

    private ReservationCreationResult replyIfSame(
            ReservationEntity existing,
            Long slotId,
            String customerName,
            String customerEmail
    ){
        boolean sameRequest =
                Objects.equals(existing.getSlot().getId(), slotId)
                        && existing.getCustomerName().equals(customerName)
                        && existing.getCustomerEmail()
                        .equalsIgnoreCase(customerEmail);
        if (!sameRequest) {
            throw new IdempotencyKeyConflictException();
        }

        return new ReservationCreationResult(
                toResponse(existing),
                false
        );
    }



    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id) {

        ReservationEntity reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(id)
                        );

        return toResponse(reservation);
    }




    @Transactional(readOnly = true)
    public Page<ReservationResponse> findByCustomerEmail(
            String customerEmail,
            Pageable pageable
    ) {
        return reservationRepository
                .findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(
                        customerEmail.trim(),
                        pageable
                )
                .map(this::toResponse);
    }



    @Transactional
    public ReservationResponse cancel(Long id){
        // Lock the reservation first.
        ReservationEntity reservation =
                reservationRepository.findByIdForUpdate(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(id)
                        );

        // Repeated cancellation returns the existing result.
        if (!reservation.isConfirmed()) {
            return toResponse(reservation);
        }

        Long slotId = reservation.getSlot().getId();

        //Lock the associated seat
        SlotEntity slot = slotRepository
                .findById(id)
                .orElseThrow(() -> new SlotNotFoundException(slotId));


        if(slot.getStatus() != SlotStatus.BOOKED){
            throw new IllegalStateException( "Reservation and slot states are inconsistent.");
        }

        //both changes are commited again
        reservation.cancel();
        slot.release();

        return toResponse(reservation);
    }




    private ReservationResponse toResponse(
            ReservationEntity reservation
    ) {
        SlotEntity slot = reservation.getSlot();

        return new ReservationResponse(
                reservation.getId(),
                slot.getId(),
                slot.getResource().getId(),
                slot.getResource().getName(),
                slot.getStartTime(),
                slot.getEndTime(),
                reservation.getCustomerName(),
                reservation.getCustomerEmail(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getCancelledAt()
        );
    }
}
