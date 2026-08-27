package cl.desafiolatam.parking.infrastructure.web.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateParkingStayRequest(

        @NotBlank(message = "License plate is required")
        @Pattern(
                regexp = "^[A-Za-z0-9]{5,8}$",
                message = "License plate must contain between 5 and 8 alphanumeric characters")
        String licensePlate,

        @NotNull(message = "Entry time is required")
        LocalDateTime entryTime) {
}