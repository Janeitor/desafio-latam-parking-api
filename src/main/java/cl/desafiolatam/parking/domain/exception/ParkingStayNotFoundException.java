package cl.desafiolatam.parking.domain.exception;

import java.util.UUID;

public class ParkingStayNotFoundException
        extends RuntimeException {

    public ParkingStayNotFoundException(UUID id) {
        super("Parking stay not found with id: " + id);
    }
}