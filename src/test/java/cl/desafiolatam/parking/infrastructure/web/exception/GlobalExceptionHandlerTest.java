package cl.desafiolatam.parking.infrastructure.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.desafiolatam.parking.domain.exception.ActiveParkingStayNotFoundException;
import cl.desafiolatam.parking.infrastructure.web.dto.ErrorResponse;
import cl.desafiolatam.parking.domain.exception.ActiveParkingStayAlreadyExistsException;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.desafiolatam.parking.infrastructure.web.dto.ErrorResponse;

public class GlobalExceptionHandlerTest {

    @Test
    void shouldHandleActiveParkingStayNotFound() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ActiveParkingStayNotFoundException exception = new ActiveParkingStayNotFoundException();

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleActiveParkingStayNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.code());
        assertEquals("Active parking stay not found for the given vehicle.", body.message());
        assertNotNull(body.timestamp());

    }

    @Test
    void shouldHandleActiveParkingStayAlreadyExists() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ActiveParkingStayAlreadyExistsException exception = new ActiveParkingStayAlreadyExistsException(
                "ABCD12");

        ResponseEntity<ErrorResponse> response = handler.handleActiveParkingStayAlreadyExists(
                exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(409);
        assertThat(response.getBody().message())
                .contains("ABCD12");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

}
