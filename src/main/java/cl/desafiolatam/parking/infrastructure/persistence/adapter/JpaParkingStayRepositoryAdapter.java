package cl.desafiolatam.parking.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import cl.desafiolatam.parking.domain.model.ParkingStay;
import cl.desafiolatam.parking.domain.port.ParkingStayRepository;
import cl.desafiolatam.parking.infrastructure.persistence.entity.ParkingStayEntity;
import cl.desafiolatam.parking.infrastructure.persistence.repository.ParkingStayJpaRepository;
import java.util.UUID;

@Repository
public class JpaParkingStayRepositoryAdapter
        implements ParkingStayRepository {

    private final ParkingStayJpaRepository jpaRepository;

    public JpaParkingStayRepositoryAdapter(
            ParkingStayJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ParkingStay save(ParkingStay parkingStay) {
        ParkingStayEntity entity = toEntity(parkingStay);
        ParkingStayEntity savedEntity = jpaRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<ParkingStay> findById(UUID id) {
        return jpaRepository
                .findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<ParkingStay> findActiveByLicensePlate(
            String licensePlate) {
        return jpaRepository
                .findFirstByLicensePlateAndExitTimeIsNull(
                        licensePlate)
                .map(this::toDomain);
    }

    @Override
    public List<ParkingStay> findAll() {
        return jpaRepository
                .findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ParkingStayEntity toEntity(
            ParkingStay parkingStay) {
        return new ParkingStayEntity(
                parkingStay.getId(),
                parkingStay.getLicensePlate(),
                parkingStay.getEntryTime(),
                parkingStay.getExitTime());
    }

    private ParkingStay toDomain(
            ParkingStayEntity entity) {
        return new ParkingStay(
                entity.getId(),
                entity.getLicensePlate(),
                entity.getEntryTime(),
                entity.getExitTime());
    }
}