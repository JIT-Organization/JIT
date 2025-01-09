package com.justintime.jit.controller;

import com.justintime.jit.entity.ReservationActivity;
import com.justintime.jit.service.ReservationActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantactivities")
public class ReservationActivityController {
    
    @Autowired
    private ReservationActivityService reservationActivityService;

    @GetMapping
    public List<ReservationActivity> getAllReservationActivities() {
        return reservationActivityService.getAllReservationActivities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationActivity> getReservationActivityById(@PathVariable Long id) {
        return reservationActivityService.getReservationActivityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ReservationActivity createReservationActivity(@RequestBody ReservationActivity reservationActivity) {
        return reservationActivityService.createReservationActivity(reservationActivity);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ReservationActivity> updateReservationActivity(@PathVariable Long id, @RequestBody ReservationActivity reservationActivityDetails) {
//        try {
//            ReservationActivity updatedReservationActivity = reservationActivityService.updateReservationActivity(id, reservationActivityDetails);
//            return ResponseEntity.ok(updatedReservationActivity);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationActivity(@PathVariable Long id) {
        reservationActivityService.deleteReservationActivity(id);
        return ResponseEntity.noContent().build();
    }
}
