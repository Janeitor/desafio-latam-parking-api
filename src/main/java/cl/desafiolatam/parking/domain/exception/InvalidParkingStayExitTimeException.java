package cl.desafiolatam.parking.domain.exception;

public class InvalidParkingStayExitTimeException
        extends IllegalArgumentException {

    public InvalidParkingStayExitTimeException(
            String message) {
        super(message);
    }
}