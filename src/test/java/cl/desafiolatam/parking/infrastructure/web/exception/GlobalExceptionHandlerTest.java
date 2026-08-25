package cl.desafiolatam.parking.infrastructure.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.desafiolatam.parking.domain.exception.ActiveParkingStayNotFoundException;
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
    
}
