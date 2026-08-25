package cl.desafiolatam.parking.infrastructure.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import cl.desafiolatam.parking.infrastructure.web.dto.ParkingStayResponse;


import cl.desafiolatam.parking.domain.exception.ActiveParkingStayNotFoundException;

@RestController
@RequestMapping("/api/v1/parking-stays")
public class ParkingStayController {
    @GetMapping
    public ResponseEntity<List<ParkingStayResponse>> getAllParkingStays() {
        // For now, return an empty list
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/active/{licensePlate}")
    public ResponseEntity<ParkingStayResponse> getActiveParkingStay(
            @PathVariable(name = "licensePlate") String licensePlate) {
        throw new ActiveParkingStayNotFoundException();
    }
}
