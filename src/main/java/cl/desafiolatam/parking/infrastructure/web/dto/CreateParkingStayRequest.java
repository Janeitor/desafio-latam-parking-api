package cl.desafiolatam.parking.infrastructure.web.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(
        name = "CreateParkingStayRequest",
        description = "Data required to register a vehicle entry")
public record CreateParkingStayRequest(

        @Schema(
                description = "Vehicle license plate",
                example = "ABCD12",
                minLength = 5,
                maxLength = 8,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "License plate is required")
        @Pattern(
                regexp = "^[A-Za-z0-9]{5,8}$",
                message = "License plate must contain between 5 and 8 alphanumeric characters")
        String licensePlate,

        @Schema(
                description = "Date and time when the vehicle entered",
                example = "2026-08-28T10:30:00",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Entry time is required")
        LocalDateTime entryTime) {
}