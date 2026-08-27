package cl.desafiolatam.parking.infrastructure.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.desafiolatam.parking.application.service.ParkingStayService;
import cl.desafiolatam.parking.domain.model.ParkingStay;
import cl.desafiolatam.parking.infrastructure.web.dto.CreateParkingStayRequest;
import cl.desafiolatam.parking.infrastructure.web.dto.ParkingStayResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/parking-stays")
public class ParkingStayController {

    private final ParkingStayService service;

    public ParkingStayController(
            ParkingStayService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ParkingStayResponse>> getAllParkingStays() {
        List<ParkingStayResponse> response = service
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ParkingStayResponse> registerParkingEntry(
            @Valid @RequestBody CreateParkingStayRequest request) {
        ParkingStay parkingStay = service.registerEntry(
                request.licensePlate(),
                request.entryTime());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(parkingStay.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(toResponse(parkingStay));
    }

    @GetMapping("/active/{licensePlate}")
    public ResponseEntity<ParkingStayResponse> getActiveParkingStay(
            @PathVariable(name = "licensePlate") String licensePlate) {
        ParkingStay parkingStay = service.findActiveByLicensePlate(licensePlate);

        return ResponseEntity.ok(toResponse(parkingStay));
    }

    private ParkingStayResponse toResponse(
            ParkingStay parkingStay) {
        return new ParkingStayResponse(
                parkingStay.getId(),
                parkingStay.getLicensePlate(),
                parkingStay.getEntryTime(),
                parkingStay.getExitTime());
    }
}