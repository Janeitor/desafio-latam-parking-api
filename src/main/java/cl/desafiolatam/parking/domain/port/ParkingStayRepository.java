package cl.desafiolatam.parking.domain.port;

import java.util.List;
import java.util.Optional;

import cl.desafiolatam.parking.domain.model.ParkingStay;

public interface ParkingStayRepository {

    ParkingStay save(ParkingStay parkingStay);

    Optional<ParkingStay> findActiveByLicensePlate(
            String licensePlate);

    List<ParkingStay> findAll();
}