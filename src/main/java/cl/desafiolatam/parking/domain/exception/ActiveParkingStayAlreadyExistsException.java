package cl.desafiolatam.parking.domain.exception;

public class ActiveParkingStayAlreadyExistsException
        extends RuntimeException {

    public ActiveParkingStayAlreadyExistsException(
            String licensePlate) {
        super(
                "An active parking stay already exists for license plate: "
                        + licensePlate);
    }
}