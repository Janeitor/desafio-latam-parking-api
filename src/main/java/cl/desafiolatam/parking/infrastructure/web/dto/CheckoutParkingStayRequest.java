package cl.desafiolatam.parking.infrastructure.web.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "CheckoutParkingStayRequest",
        description = "Data required to register a vehicle exit")
public record CheckoutParkingStayRequest(

        @Schema(
                description = "Date and time when the vehicle exited",
                example = "2026-09-02T12:45:00",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Exit time is required")
        LocalDateTime exitTime) {
}