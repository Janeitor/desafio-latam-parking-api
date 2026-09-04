package cl.desafiolatam.parking.domain.exception;

public class ParkingStayAlreadyClosedException
        extends IllegalStateException {

    public ParkingStayAlreadyClosedException() {
        super("Parking stay is already closed");
    }
}