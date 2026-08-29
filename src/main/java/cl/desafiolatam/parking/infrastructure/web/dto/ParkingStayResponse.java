package cl.desafiolatam.parking.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ParkingStayResponse",
        description = "Parking stay information returned by the API")
public record ParkingStayResponse(

        @Schema(
                description = "Unique parking stay identifier",
                example = "03227f66-76dc-4e71-be12-78ca6e869afd",
                format = "uuid",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY)
        UUID id,

        @Schema(
                description = "Normalized vehicle license plate",
                example = "ABCD12",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY)
        String licensePlate,

        @Schema(
                description = "Date and time when the vehicle entered",
                example = "2026-08-28T10:30:00",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime entryTime,

        @Schema(
                description = "Date and time when the vehicle exited; null while the stay is active",
                type = "string",
                format = "date-time",
                nullable = true,
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime exitTime) {
}