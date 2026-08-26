package cl.desafiolatam.parking.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.desafiolatam.parking.infrastructure.persistence.entity.ParkingStayEntity;

public interface ParkingStayJpaRepository
        extends JpaRepository<ParkingStayEntity, UUID> {

    Optional<ParkingStayEntity> findFirstByLicensePlateAndExitTimeIsNull(
            String licensePlate);
}