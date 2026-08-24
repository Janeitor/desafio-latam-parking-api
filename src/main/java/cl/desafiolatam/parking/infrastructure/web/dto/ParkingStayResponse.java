package cl.desafiolatam.parking.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParkingStayResponse(
    UUID id,
    String licensePlate,
    LocalDateTime entryTime,
    LocalDateTime exitTime) {
}