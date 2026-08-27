package cl.desafiolatam.parking.domain.exception;


public class ActiveParkingStayNotFoundException extends RuntimeException {
    public ActiveParkingStayNotFoundException() {
        super("Active parking stay not found for the given vehicle.");
    }

    
}
