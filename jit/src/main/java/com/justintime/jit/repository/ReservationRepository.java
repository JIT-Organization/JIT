package com.justintime.jit.repository;

import com.justintime.jit.entity.Reservation;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends BaseRepository<Reservation,Long> {
    Reservation findByReservationNumber(String reservationNumber);
}
