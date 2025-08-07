package com.justintime.jit.service;

import com.justintime.jit.entity.Reservation;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReservationService {

    List<Reservation> getAllReservations();
    Reservation createReservation(Reservation reservation);
    ResponseEntity<Reservation> getReservationById(Long id);
    ResponseEntity<Reservation> updateReservation(Long id, Reservation reservationDetails);
    ResponseEntity<Void> deleteReservation(Long id);

    Reservation getReservationByReservationNumber(String reservationNumber);
}
