package cl.desafiolatam.parking.infrastructure.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.desafiolatam.parking.infrastructure.web.dto.ParkingStayResponse;

@RestController
@RequestMapping("/api/v1/parking-stays")
public class ParkingStayController {
    @GetMapping
    public ResponseEntity<List<ParkingStayResponse>> getAllParkingStays() {
        // For now, return an empty list
        return ResponseEntity.ok(List.of());
    }
    
}
