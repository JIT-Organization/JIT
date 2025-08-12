package com.justintime.jit.service.impl;

import com.justintime.jit.entity.Reservation;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.ReservationRepository;
import com.justintime.jit.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

        @Autowired
        private ReservationRepository reservationRepository;

        public List<Reservation> getAllReservations() {
            return reservationRepository.findAll();
        }

        public ResponseEntity<Reservation> getReservationById(Long id) {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found for this id :: " + id));
            return ResponseEntity.ok().body(reservation);
        }

        public Reservation createReservation(Reservation reservation) {
            return reservationRepository.save(reservation);
        }

        public ResponseEntity<Reservation> updateReservation(Long id, Reservation reservationDetails) {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found for this id :: " + id));

            reservation.setUser(reservationDetails.getUser());
            reservation.setRestaurant(reservationDetails.getRestaurant());
            reservation.setShiftCapacity(reservationDetails.getShiftCapacity());
            reservation.setReservationStart(reservationDetails.getReservationStart());
            reservation.setReservationEnd(reservationDetails.getReservationEnd());
            reservation.setHeadCount(reservationDetails.getHeadCount());
            reservation.setStatus(reservationDetails.getStatus());
            reservation.setUpdatedDttm(LocalDateTime.now());

            final Reservation updatedReservation = reservationRepository.save(reservation);
            return ResponseEntity.ok(updatedReservation);
        }

        public ResponseEntity<Void> deleteReservation(Long id) {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found for this id :: " + id));

            reservationRepository.delete(reservation);
            return ResponseEntity.noContent().build();
        }

    @Override
    public Reservation getReservationByReservationNumber(String reservationNumber) {
        return reservationRepository.findByReservationNumber(reservationNumber);
    }
}
