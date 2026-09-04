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
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import cl.desafiolatam.parking.infrastructure.web.dto.ErrorResponse;
import org.springframework.web.bind.annotation.PatchMapping;

import cl.desafiolatam.parking.infrastructure.web.dto.CheckoutParkingStayRequest;

@Tag(name = "Parking Stays", description = "Operations for registering and querying vehicle parking stays")

@RestController
@RequestMapping("/api/v1/parking-stays")
public class ParkingStayController {

        private final ParkingStayService service;

        public ParkingStayController(
                        ParkingStayService service) {
                this.service = service;
        }

        @Operation(summary = "Get all parking stays", description = "Returns every parking stay currently stored")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Parking stays retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ParkingStayResponse.class))))
        })
        @GetMapping
        public ResponseEntity<List<ParkingStayResponse>> getAllParkingStays() {
                List<ParkingStayResponse> response = service
                                .findAll()
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get a parking stay by id", description = "Returns a parking stay identified by its UUID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Parking stay found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingStayResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Parking stay not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })

        @GetMapping("/{id}")
        public ResponseEntity<ParkingStayResponse> getParkingStayById(
                        @PathVariable(name = "id") UUID id) {
                ParkingStay parkingStay = service.findById(id);

                return ResponseEntity.ok(toResponse(parkingStay));
        }

        @Operation(summary = "Register a vehicle entry", description = "Creates an active parking stay for a vehicle that is not currently parked")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Parking stay created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingStayResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data or malformed JSON", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "The license plate already has an active parking stay", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
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

        @Operation(summary = "Get an active parking stay by license plate", description = "Returns the active parking stay associated with a vehicle license plate")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Active parking stay found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingStayResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Active parking stay not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/active/{licensePlate}")
        public ResponseEntity<ParkingStayResponse> getActiveParkingStay(
                        @PathVariable(name = "licensePlate") String licensePlate) {
                ParkingStay parkingStay = service.findActiveByLicensePlate(licensePlate);

                return ResponseEntity.ok(toResponse(parkingStay));
        }

        @Operation(
                        summary = "Checkout a parking stay",
                        description = "Registers the vehicle exit time and closes an active parking stay")
        @ApiResponses({
                        @ApiResponse(
                                        responseCode = "200",
                                        description = "Parking stay closed successfully",
                                        content = @Content(
                                                        mediaType = "application/json",
                                                        schema = @Schema(
                                                                        implementation = ParkingStayResponse.class))),
                        @ApiResponse(
                                        responseCode = "400",
                                        description = "Invalid request data or malformed JSON",
                                        content = @Content(
                                                        mediaType = "application/json",
                                                        schema = @Schema(
                                                                        implementation = ErrorResponse.class))),
                        @ApiResponse(
                                        responseCode = "404",
                                        description = "Parking stay not found",
                                        content = @Content(
                                                        mediaType = "application/json",
                                                        schema = @Schema(
                                                                        implementation = ErrorResponse.class))),
                        @ApiResponse(
                                        responseCode = "409",
                                        description = "Parking stay is already closed",
                                        content = @Content(
                                                        mediaType = "application/json",
                                                        schema = @Schema(
                                                                        implementation = ErrorResponse.class))),
                        @ApiResponse(
                                        responseCode = "422",
                                        description = "Exit time violates a business rule",
                                        content = @Content(
                                                        mediaType = "application/json",
                                                        schema = @Schema(
                                                                        implementation = ErrorResponse.class)))
        })
        @PatchMapping("/{id}/checkout")
        public ResponseEntity<ParkingStayResponse> checkoutParkingStay(
                        @PathVariable(name = "id") UUID id,
                        @Valid @RequestBody CheckoutParkingStayRequest request) {

                ParkingStay parkingStay = service.checkout(
                                id,
                                request.exitTime());

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
