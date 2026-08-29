package cl.desafiolatam.parking.infrastructure.web.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ErrorResponse",
        description = "Standard error response returned by the API")
public record ErrorResponse(

        @Schema(
                description = "HTTP status code",
                example = "409",
                type = "integer",
                format = "int32",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY)
        int code,

        @Schema(
                description = "Human-readable error description",
                example = "An active parking stay already exists for license plate: ABCD12",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY)
        String message,

        @Schema(
                description = "Date and time when the error occurred",
                example = "2026-08-28T10:35:00",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime timestamp) {
}