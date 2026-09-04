package cl.desafiolatam.parking.infrastructure.web.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.desafiolatam.parking.domain.exception.ActiveParkingStayNotFoundException;
import cl.desafiolatam.parking.infrastructure.web.dto.ErrorResponse;
import cl.desafiolatam.parking.domain.exception.ActiveParkingStayAlreadyExistsException;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import cl.desafiolatam.parking.domain.exception.ParkingStayNotFoundException;
import cl.desafiolatam.parking.domain.exception.InvalidParkingStayExitTimeException;
import cl.desafiolatam.parking.domain.exception.ParkingStayAlreadyClosedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(ActiveParkingStayNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleActiveParkingStayNotFound(
                        ActiveParkingStayNotFoundException exception) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage(),
                                LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        @ExceptionHandler(ActiveParkingStayAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleActiveParkingStayAlreadyExists(
                        ActiveParkingStayAlreadyExistsException exception) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                exception.getMessage(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(errorResponse);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException exception) {
                String message = exception
                                .getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(FieldError::getDefaultMessage)
                                .findFirst()
                                .orElse("Request validation failed");

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                message,
                                LocalDateTime.now());

                return ResponseEntity
                                .badRequest()
                                .body(errorResponse);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException exception) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Malformed JSON request",
                                LocalDateTime.now());

                return ResponseEntity
                                .badRequest()
                                .body(errorResponse);
        }

        @ExceptionHandler(ParkingStayNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleParkingStayNotFound(
                        ParkingStayNotFoundException exception) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                exception.getMessage(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(errorResponse);
        }

        @ExceptionHandler(InvalidParkingStayExitTimeException.class)
        public ResponseEntity<ErrorResponse> handleInvalidParkingStayExitTime(
                        InvalidParkingStayExitTimeException exception) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                exception.getMessage(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body(errorResponse);
        }

        @ExceptionHandler(ParkingStayAlreadyClosedException.class)
        public ResponseEntity<ErrorResponse> handleParkingStayAlreadyClosed(
                        ParkingStayAlreadyClosedException exception) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                exception.getMessage(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(errorResponse);
        }
}
